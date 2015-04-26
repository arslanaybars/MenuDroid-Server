package socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import aybars.arslan.menudroid_server.SqlOperations;

/**
 * AsyncTask which handles the commiunication with clients
 */
public class ServerAsyncTask extends AsyncTask<Socket, Void, JSONObject> {

    private Context mContext;
    private String TAG="ServerSocket";

    public ServerAsyncTask(Context context) {
        this.mContext = context;
    }
    private SqlOperations sqliteoperation,sqliteoperation2;
        //Background task which serve for the client
        @Override
        protected JSONObject doInBackground(Socket... params) {
            JSONObject jsondata = null;
            //Get the accepted socket object
            Socket mySocket = params[0];

            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                //Get the data input stream comming from the client
                InputStream is = mySocket.getInputStream();
                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        mySocket.getOutputStream(), true);
                //Write data to the data output stream
                out.println("Connection Accepted");


                //Buffer the data input stream
              //  BufferedReader br = new BufferedReader(
                //        new InputStreamReader(is));
                //Read the contents of the data buffer
               // result = br.readLine();
           // result="hola";
                Log.d(TAG,"hola");


                dataInputStream = new DataInputStream(
                        mySocket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        mySocket.getOutputStream());

                String messageFromClient, messageToClient, request;


                //If no message sent from client, this code will block the program
                messageFromClient = dataInputStream.readUTF();
                jsondata = new JSONObject(messageFromClient);
                messageToClient = "Connection Accepted";
                dataOutputStream.writeUTF(messageToClient);

                String message = jsondata.getString("request");
                Log.d(TAG,""+message);


                //Close the client connection
                mySocket.close();
            } catch (IOException e) {
                Log.e(TAG,"excepction socket "+ e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG, "excepction json "+e.toString());
                e.printStackTrace();
            }
            return jsondata;
        }

        @Override
        protected void onPostExecute(JSONObject jsondata) {
            //After finishing the execution of background task data will be write the text view
           // Log.i("CLIENT", "The message from client is: "+s);
            if(jsondata!=null) {
                String result="";
                try {
                    String request = jsondata.getString("request");
                    Log.d(TAG,""+request);
                    if (request.equals("O-")) { //if is an order
                        String array = jsondata.getString("messageArray");
                        String message = jsondata.getString("message");
                        Log.d(TAG,"the food array is "+ array);
                        Log.d(TAG, "you send "+request + " "+message);
                        result=request+message;

                        JSONArray OrderArray = jsondata.getJSONArray("messageArray");
                        if(OrderArray!=null) { //only save if the order have information
                            saveStatusTable(result);
                            //now save the customer order.
                            int last = result.lastIndexOf("Table") + 5;
                            String number = result.substring(last);
                            sqliteoperation2 = new SqlOperations(mContext); //new instantiate SqlOperations, this is our class to do insert,delete,update to the databse.
                            sqliteoperation2.open(); //open the Database, (the database is ready to be read or write on it.
                            sqliteoperation2.insertOrder(OrderArray, number);
                            sqliteoperation2.close();
                        }

                    }else if(!request.equals("")){

                        String message = jsondata.getString("message");
                        Log.d(TAG, "you send "+request + " "+message);
                        result=request+message;
                        saveStatusTable(result);

                    }else{
                        Log.d(TAG, "you send other thing");

                    }
                } catch (JSONException e) {
                    //  e.printStackStrace();
                    Log.e(TAG, "Unable to get request");
                   // dataOutputStream.flush();
                }



            }
            /* in case of order ARRAY save at SQLITE other table*/

            /*call the our method insertRequest ,
            this take the client-request (B-MenuDroidTable1) and split the number table,
             and the capital letter and save it at database */

        }

    public void saveStatusTable(String result) {
        sqliteoperation = new SqlOperations(mContext); //new instantiate SqlOperations, this is our class to do insert,delete,update to the databse.
        sqliteoperation.open(); //open the Database, (the database is ready to be read or write on it.
        sqliteoperation.insertRequest(result);
        sqliteoperation.close();
    }
}

