package socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import aybars.arslan.menudroid_server.SqlOperations;

/**
 * AsyncTask which handles the commiunication with clients
 */
public class ServerAsyncTask extends AsyncTask<Socket, Void, String> {

    private Context mContext;
    private String TAG="ServerSocket";

    public ServerAsyncTask(Context context) {
        this.mContext = context;
    }
    private SqlOperations sqliteoperation;
        //Background task which serve for the client
        @Override
        protected String doInBackground(Socket... params) {
            String result = null;
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
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read the contents of the data buffer
               // result = br.readLine();
           // result="hola";
                Log.d(TAG,"hola");

                dataInputStream = new DataInputStream(
                        mySocket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        mySocket.getOutputStream());

                String messageFromClient, messageToClient, request;

                messageToClient = "Connection Accepted";
                dataOutputStream.writeUTF(messageToClient);

                //If no message sent from client, this code will block the program
                messageFromClient = dataInputStream.readUTF();

                final JSONObject jsondata;
                jsondata = new JSONObject(messageFromClient);

                try {
                    request = jsondata.getString("request");
                    Log.d(TAG,""+request);
                    if (request.equals("order")) {
                        String clientIPAddress = jsondata.getString("success");
                        String array = jsondata.getString("Students");
                        Log.d(TAG,""+clientIPAddress +"and the students array is "+ array);
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                    }else if(!request.equals("")){
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                        String message = jsondata.getString("message");
                        Log.d(TAG, "you send "+request + " "+message);
                        result=request+message;

                    }else{
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                        Log.d(TAG, "you send other thing");
                        dataOutputStream.flush();
                    }
                } catch (JSONException e) {
                    //  e.printStackStrace();
                    Log.e(TAG, "Unable to get request");
                    dataOutputStream.flush();
                }


                //Close the client connection
                mySocket.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //After finishing the execution of background task data will be write the text view
            Log.i("CLIENT", "The message from client is: "+s);
            sqliteoperation = new SqlOperations(mContext); //new instantiate SqlOperations, this is our class to do insert,delete,update to the databse.
            sqliteoperation.open(); //open the Database, (the database is ready to be read or write on it.
            sqliteoperation.insertRequest(s);
            /*call the our method insertRequest ,
            this take the client-request (B-MenuDroidTable1) and split the number table,
             and the capital letter and save it at database */

        }
    }

