package aybars.arslan.menudroid_server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * AsyncTask which handles the commiunication with clients
 */
public class ServerAsyncTask extends AsyncTask<Socket, Void, String> {

    private Context mContext;

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
            try {
                //Get the data input stream comming from the client
                InputStream is = mySocket.getInputStream();
                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        mySocket.getOutputStream(), true);
                //Write data to the data output stream
                out.println("Hello from MenuDroid-Server");
                //Buffer the data input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read the contents of the data buffer
                result = br.readLine();
                //Close the client connection
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //After finishing the execution of background task data will be write the text view
            Log.i("CLIENT", "The message from client is: "+s);
            sqliteoperation = new SqlOperations(mContext);
            sqliteoperation.open();
            sqliteoperation.insertRequest(s);
            //Then add the data to a SqliteDatabase
        }
    }

