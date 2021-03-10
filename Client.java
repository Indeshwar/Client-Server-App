import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class Client {
    public static boolean findSinbol(String str, char ch){
        for(int i = 0; i < str.length(); i++){
            //check the symbol is matched or not
            if(str.charAt(i) == ch){
                return true;
            }
        }
        return false;
    }

    public static void main(String args[]) {

        try {

            // Create client socket to connect to certain server (Server IP, Port address)
            // we use either "localhost" or "127.0.0.1" if the server runs on the same device as the client
            Socket mySocket = new Socket("127.0.0.1", 6666);


            // to interact (send data / read incoming data) with the server, we need to create the following:

            //DataOutputStream object to send data through the socket
            DataOutputStream outStream = new DataOutputStream(mySocket.getOutputStream());

            // BufferReader object to read data coming from the server through the socket
            BufferedReader inStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));


            String statement = "";
            Scanner in = new Scanner(System.in);
            int count = 0;
            while(!statement.equals("exit")) {
                //create a Json object
                JSONObject obj = new JSONObject();
                System.out.println("Enter command:");
                statement = in.nextLine();  			// read user input from the terminal data to the server
                count++;
                //concastinating the instruction and count
                String key = "Instruction" + count;

                if(findSinbol(statement, ';')){
                    //create an array of String by splitting the String
                    String[] strArr = statement.split(";");
                    //create an object of jsonArray
                    JSONArray jsonArray = new JSONArray();
                    for(int i = 0; i < strArr.length; i++){
                        JSONObject j = new JSONObject();
                        j.put(key, strArr[i]);
                        jsonArray.add(j);
                        count++;
                        key = "Instruction" + count;

                    }
                    count--;
                    key = "Instruction" + count;
                    //System.out.println(jsonArray);
                    obj.put("batchCommand", jsonArray);
                    //System.out.println(obj);
                    String s = obj.toString();
                    outStream.writeBytes(s + "\n");//send data to the server
                }else{
                    //insert keys and values in json object
                    //instruction is key and and statement is value
                    obj.put(key, statement);
                    //convert json object into String object
                    String stat = obj.toString();
                    //System.out.println(stat);
                    outStream.writeBytes(stat + "\n");        // send such input data to the server
                }

                String str = inStream.readLine();     	// receive response from server

                System.out.println(str);                // print this response

            }

           System.out.println("Closing the connection and the sockets");

            //close connection.
            outStream.close();
            inStream.close();
            mySocket.close();

        } catch (Exception exc) {
            System.out.println("Error is : " + exc.toString());

        }
    }
}
