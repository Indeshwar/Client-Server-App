import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static ArrayList<Integer> arrayList= new ArrayList<>();

    public static void add(String[] strArr){

        for(int i = 0; i < strArr.length; i++){
            //parsing the string into integer
            int num = Integer.parseInt(strArr[i]);
            //add the num in the arrayList
            arrayList.add(num);
        }

    }

    public static void remove(String[] strArr){
        for(int i = 0; i < strArr.length; i++){
            //parsing the string into integer
            int num = Integer.parseInt(strArr[i]);
            //remove the num from the arrayList
            arrayList.remove(new Integer(num));
        }
    }

    public static String inputValues(JSONObject jsonObj, String key){

        //create an json object
        JSONObject jsonObj2 = new JSONObject();
        String s = null;
        s = jsonObj.get(key).toString();
        if (s.equals("Get_Summation")) {
            //put key and value in json object
            jsonObj.put(key, "The summation is " + summationValues());
            //convert json object into string object
            s = jsonObj.toString();

        } else if (s.equals("Sort_A")) {
            sort();
            //create an object JsonArray
            JSONArray list = new JSONArray();
            for (int i = 0; i < arrayList.size(); i++) {
                //add sorted element in the list
                list.add(arrayList.get(i));
            }
            jsonObj.put(key, "The sorted list is " + list);
            s = jsonObj.toString();

        } else {
            //convert the json value into string
            //then split it into two parts from ":"
            String[] strArr = jsonObj.get(key).toString().split(":");
            //part1 contains instruction name such add, sortA
            String[] part1 = strArr[0].split(" ");
            //part2 contain values that are inserted into arrayList
            String[] part2 =  strArr[1].split(",");
            if(part1[0].equals("add") || part1[0].equals("Add")){
                add(part2);
                //put key and value in json object
                jsonObj2.put(key, " added successfully");
                //convert json object into string object
                s = jsonObj2.toString();
                //ps.println(s);    //respond back to the client
            }else if(part1[0].equals("remove") || part1[0].equals("Remove")){
                remove(part2);
                //put key and value in json object
                jsonObj2.put(key, " removed successfully");
                //convert json object into string object
                s = jsonObj2.toString();
                //ps.println(s);    //respond back to the client
            }

        }
        return s;
    }

    public static int summationValues(){
        int sum = 0;
        for(int i = 0; i < arrayList.size(); i++){
            sum += arrayList.get(i);
        }
        return sum;
    }

    public static void sort(){
        int n = arrayList.size();
        int min;
        int tempIndex;
        for(int i = 0; i < n-1; i++){
            min = arrayList.get(i);
            tempIndex = i;
            for(int j = i+1; j < n; j++){
                if(min > arrayList.get(j)){
                    min = arrayList.get(j);
                    tempIndex = j;
                }

            }
            if(i != tempIndex){
                int tempVal = arrayList.get(i);
                //swapping the value
                arrayList.set(i, min);
                arrayList.set(tempIndex, tempVal);
            }
        }

    }

    public static void main(String args[]) {
        try {

            // Create server Socket that listens/bonds to port/endpoint address 6666 (any port id of your choice, should be >=1024, as other port addresses are reserved for system use)
            // The default maximum number of queued incoming connections is 50 (the maximum number of clients to connect to this server)
            // There is another constructor that can be used to specify the maximum number of connections
            ServerSocket mySocket = new ServerSocket(6666);


            System.out.println("Startup the server side over port 6666 ....");

            // use the created ServerSocket and accept() to start listening for incoming client requests targeting this server and this port
            // accept() blocks the current thread (server application) waiting until a connection is requested by a client.
            // the created connection with a client is represented by the returned Socket object.
            Socket connectedClient = mySocket.accept();


            // reaching this point means that a client established a connection with your server and this particular port.
            System.out.println("Connection established");


            // to interact (read incoming data / send data) with the connected client, we need to create the following:

            // BufferReader object to read data coming from the client
            BufferedReader br = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));

            // PrintStream object to send data to the connected client
            PrintStream ps = new PrintStream(connectedClient.getOutputStream());


            // Let's keep reading data from the client, as long as the client does't send "exit".
            String inputData;
            //create an object of JSONParser class
            JSONParser parser = new JSONParser();
            int count = 0;
            while (!(inputData = br.readLine()).equals("exit")) {

                //converting the string into Json object
                JSONObject jsonObj = (JSONObject)parser.parse(inputData);
                count++;
                //generating the key that is send by the client
                String key = "Instruction" + count;
                String s = null;
                if(jsonObj.containsKey("batchCommand")){
                    //retrieve the jsonArray by using the key "batchCommand" from json object
                    //then type casting it into jsonArray
                    JSONArray jArray = (JSONArray)jsonObj.get("batchCommand");
                    jArray.get(0);
                    for(int i = 0; i < jArray.size(); i++){
                        JSONObject jsonObject =(JSONObject)jArray.get(i);
                        //this method do following functions:
                        // add,remove, Sort_A, Get_Summation
                        //return string as result
                        s = inputValues(jsonObject, key);
                        count++;
                        key = "Instruction" + count;

                    }
                    System.out.println("received a message from client: " + inputData);
                    ps.println(s);//respond back to client
                    count--;
                    key = "Instruction" + count;

                }else {
                    //this method do following functions:
                    // add,remove, Sort_A, Get_Summation
                    //return string as result
                    s = inputValues(jsonObj, key);
                    ps.println(s); //send Respond back to client
                    System.out.println("received a message from client: " + inputData);
                }

            }


            System.out.println("Closing the connection and the sockets");

            // close the input/output streams and the created client/server sockets
            ps.close();
            br.close();
            mySocket.close();
            connectedClient.close();

        } catch (Exception exc) {
            System.out.println("Error :" + exc.toString());
        }

    }


}
