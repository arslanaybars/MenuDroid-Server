package aybars.arslan.menudroid_server;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is a IntentService , it will be running in background , every time was listen to request from client device.
 */
public class MyService extends IntentService {
    public final static String EXTRA_PACKAGEFOREGORUND = "paquete"; // ??
    private static boolean estadoServicio = false; //??
    private final int SERVER_PORT = 8080; //Define the server port
    private static Timer timer;
    private boolean isPaused = true;
    private SQLiteDatabase dbGlobal;
    private Cursor cursorSearch;
    private Context c = this;

    public MyService() {
        super("MyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService(); //this is for create and call the startService method.
    }

    private void startService() {
        int delay = 100;
        int period = 500;
        final Context ctx = this;
        timer = new Timer();
        /*This is a thread that works every 1/2 second you can adjust the time with the variable called period ,
        evey 1000 is equal to 1 second*/
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (isPaused == true) {
                    /*This is your server socket code */
                    try {
                        //Create a server socket object and bind it to a port
                        ServerSocket socServer = new ServerSocket(SERVER_PORT);
                        //Create server side client socket reference
                        Socket socClient = null;
                        //Infinite loop will listen for client requests to connect
                        while (true) {
                            //Accept the client connection and hand over communication to server side client socket
                            socClient = socServer.accept();
                            //For each client new instance of AsyncTask will be created
                            ServerAsyncTask serverAsyncTask = new ServerAsyncTask(c);
                            //Start the AsyncTask execution
                            //Accepted client socket object will pass as the parameter
                            serverAsyncTask.execute(new Socket[]{socClient});
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, delay, period);
    }

    @Override
    public void onDestroy() {
        if (cursorSearch != null) {
            cursorSearch.close();
        }
        if (null != dbGlobal) {
            dbGlobal.close();
        }

        isPaused = false;
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        if (Build.VERSION.SDK_INT >= 14) {
            super.onTaskRemoved(rootIntent);
        }
    }
}

