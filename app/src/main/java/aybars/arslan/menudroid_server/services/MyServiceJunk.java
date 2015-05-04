package aybars.arslan.menudroid_server.services;

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
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import aybars.arslan.menudroid_server.socket.JsonSocketServer;

/**
 * This is a IntentService , it will be running in background , every time was listen to request from client device.
 */
public class MyServiceJunk extends IntentService {
    private final int SERVER_PORT = 8080; //Define the server port
    private static Timer timer;
    private boolean isPaused = true;
    private SQLiteDatabase dbGlobal;
    private Cursor cursorSearch;
    private Context c=this;
    public MyServiceJunk() {
        super("MyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService(); //this is for create and call the startService method.
    }

    private void startService() {
        int delay = 100;
        int period = 5000;
        final Context ctx = this;
        timer = new Timer();
        /*This is a thread that works every 1/2 second you can adjust the time with the variable called period ,
        evey 1000 is equal to 1 second*/
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (isPaused == true) {
                    /*This is your server socket code */
                        //Infinite loop will listen for client requests to connect
                        while (true) {
                            Log.d("SERVICE", "running");
                            JsonSocketServer socketServerThread = new JsonSocketServer();
                            socketServerThread.start();
                        }

                }
            }
        }, delay, period);
    }

    @Override
    public void onDestroy() {
        isPaused = false;
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {

        /*The start_sticky is necesarry to retain and recreate the service in case of this was killed.  .*/
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
        /*This code it is similar to the START_STICKY s function */
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

