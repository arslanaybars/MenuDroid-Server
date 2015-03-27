package aybars.arslan.menudroid_server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivityServer extends ActionBarActivity {
    private static Timer timer2;
    private boolean isPaused = true;
    private SqlOperations sqliteoperation;
    private static final String KEY_NUMBER_TABLE = "number_table";
    private static final String KEY_KIND_REQUEST = "kind_of_request";
    private static final String KEY_REQUEST_TEXT = "request_text";
    private Button btnTable;
    private TextView tvIP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);


        startService(new Intent(MainActivityServer.this, MyService.class)); //this line enabled the Intent service.

        sqliteoperation = new SqlOperations(getApplicationContext());
        sqliteoperation.open();

        tvIP = (TextView) findViewById(R.id.tvIP);

        getDeviceIpAddress(); //Ipaddress method.
        doLoopProcess();


    }

    public void getDeviceIpAddress() {
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        tvIP.setText(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
        }
    }

    public void doLoopProcess() {
        //Timer with a thread inside to search the status of each table.
        int delay = 100; //is the delay or sleep between every timer loop.
        int period = 10000;//ten seconds
        final Context ctx = this;
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(1000);

                                /* call the method getTableStatus from SqlOperations class ,
                                this method returns an ArrayList<HashMap<String, String>>
                                this means_:
                                 Index-   KEY               VALUE
                                 (1)     number_table          1
                                         kind_of_request       W
                                         request_text           W-MenuDroidTable1
                                 (2)    number_table          2
                                         kind_of_request      O
                                         request_text          O-MenuDroidTable1
                                 (3)  ....
                                      ....
                                      ...
                                  (4)
                                  .
                                  .
                                  .
                                  .
                                  ETC
                                */
                                final ArrayList<HashMap<String, String>> dictionary = sqliteoperation.getTableStatus();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < dictionary.size(); i++) {
                                            /*I start at index 0 and finish at the penultimate index */
                                            HashMap<String, String> map = dictionary.get(i); //Get the corresponding map from the index
                                            Log.d("DictionaryMAinActivity", map.get(KEY_NUMBER_TABLE) + " --- " +
                                                    map.get(KEY_KIND_REQUEST) + "------" +
                                                    map.get(KEY_REQUEST_TEXT)); /*this is a simple log XD, to verify if there is information.*/
                                            btnTable = chooseTable(Integer.parseInt(map.get(KEY_NUMBER_TABLE).toString()));
                                            //get the capital letter from each Map,
                                            ChangeColorTable(btnTable, map.get(KEY_KIND_REQUEST).toString().toUpperCase());
                                        }
                                    }
                                });

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            /*Get possible execption*/
                        }
                    }

                    ;
                };
                thread.start();
            }
        }, delay, period);
    }


    public Button chooseTable(int number) {
        Button btnChooseTable = (Button) findViewById(R.id.btn1);
        // GET the value from number_table key form the actual Map. (this map has "key"-"value
        switch (number) {
            //Depending the number is the table (button) that we get to change the color.
            case 1:
                btnChooseTable = (Button) findViewById(R.id.btn1);
                break;
            case 2:
                btnChooseTable = (Button) findViewById(R.id.btn2);
                break;
            case 3:
                btnChooseTable = (Button) findViewById(R.id.btn3);
                break;
            case 4:
                btnChooseTable = (Button) findViewById(R.id.btn4);
                break;
            case 5:
                btnChooseTable = (Button) findViewById(R.id.btn5);
                break;
            case 6:
                btnChooseTable = (Button) findViewById(R.id.btn6);
                break;

        }
        return btnChooseTable;
    }

    public void ChangeColorTable(Button tableColor, String capitalLetter) {
        if (capitalLetter.equals("B")) {
                                                /*B- bill = the  color change to yellow*/
            tableColor.setBackgroundResource(R.drawable.main_custom_button_yellow);
            tableColor.setTextColor(R.drawable.main_custom_button_blue);
        } else if (capitalLetter.equals("O")) {
                                                /*O- order = the  color change to blue*/
            tableColor.setBackgroundResource(R.drawable.main_custom_button_blue);
        } else if (capitalLetter.equals("W")) {
                                                /*W  waiter = the  color change to green  */
            tableColor.setBackgroundResource(R.drawable.main_custom_button_green);
        } else {
            // If the result is diferrent to B,O,W , the color change to brown
            // TODO I think we dont need to brown button if the table non use so its red
            tableColor.setBackgroundResource(R.drawable.main_custom_button);
        }
    }

    @Override
    protected void onDestroy() {
        sqliteoperation.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}