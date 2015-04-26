package socket;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by renesotolira on 25/04/15.
 */
public class JsonSocketServer extends Thread {
   /*This is the new Socket Server , this listen JSON data*/

    private int SocketServerPort = 8080;
    private String TAG="ServerSocket";
    @Override
    public void run() {

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            Log.i(TAG, "Creating server socket");
            ServerSocket serverSocket = new ServerSocket(SocketServerPort);

          //  while (true) {
                socket = serverSocket.accept();
                dataInputStream = new DataInputStream(
                        socket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());

                String messageFromClient, messageToClient, request;

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
                    } else if (request.equals("O-")) {
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                       Log.d(TAG, "you send O-");
                    }else if (request.equals("B-")) {
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                        Log.d(TAG, "you send B-");
                    }else if (request.equals("W-")) {
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                        Log.d(TAG, "you send W-");
                    }else if (request.equals("L-")) {
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                        Log.d(TAG, "you send L-");
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
            serverSocket.close();
           // }

        } catch (IOException e) {
            // e.printStackStrace();
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
        }

    }

}