package aybars.arslan.menudroid_server;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import aybars.arslan.menudroid_server.db.SqlOperations;
import aybars.arslan.menudroid_server.services.MyService;




public class MainActivityServer extends ActionBarActivity {
    private static Timer timer2;
    private boolean isPaused = true;
    private SqlOperations sqliteoperation;
    private static final String KEY_NUMBER_TABLE = "number_table";
    private static final String KEY_KIND_REQUEST = "kind_of_request";
    private static final String KEY_REQUEST_TEXT = "request_text";
    private static final String KEY_CONFIRM_SESSION = "confirmSession";
    private static final String KEY_SHOW = "show";
    static String TABLE_NAME = "Table Name";
    private Button btnTable;
    private TextView tvIP;

    private Handler handler = new Handler();

    private SqlOperations sqliteoperationShow,sqliteoperation2 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        Log.d("SERVICE", "nani");
        startService(new Intent(MainActivityServer.this, MyService.class)); //this line enabled the Intent service.
        sqliteoperation = new SqlOperations(getApplicationContext());
        sqliteoperation.open();
        tvIP = (TextView) findViewById(R.id.tvIP);
        getDeviceIpAddress(); //Ipaddress method.

    }

    @Override
    public void onResume() {
        Log.i("Activity", "onResume()");
        handler.postDelayed(runnable, 1000);
        super.onResume();

    }


    public void tableClick(View v){
        String IdAsString = v.getResources().getResourceName(v.getId());
        //this return namepackage: id /btn1 or btn2 , etc
        //this is to get "btn1" , "btn5" depending from the View selected.
        String idString[]=IdAsString.split("/");
        Log.d("idString",IdAsString);
        Log.d("idString[1]",idString[1]); //btn?
        int tableNumber=Integer.parseInt(idString[1].substring(3));
        Log.d("Table","table # "+tableNumber);//get tables number
        //get color status
         sqliteoperation2 = new SqlOperations(getApplicationContext());
        sqliteoperation2.open();
        String status=sqliteoperation2.getSpecificTableStatus(tableNumber);
        Log.d("status","status is "+status);//get tables number
        sqliteoperation2.close();

        if(status.equals("O")){
//            Intent intentOrder = new Intent(MainActivityServer.this, OrderDetailsActivity.class);
//            intentOrder.putExtra("number", String.valueOf(tableNumber));
//            startActivity(intentOrder);

            showOrder(String.valueOf(tableNumber));
        } else if (status.equals("W")) {
            showWaiter(tableNumber);
        } else if (status.equals("B")) {
            showBill(tableNumber);
        } else if (status.equals("L")) {
            showLogined(tableNumber);
        } else {

        }
    }

    private void showOrder(String number) {
        //TODO
        //add delivered button after click delivered the button rechange the table color

        sqliteoperation= new SqlOperations(getApplicationContext());
        sqliteoperation.open();
        ArrayList<HashMap<String, String>> dictionary =sqliteoperation.getOrder(Integer.parseInt(number));
        sqliteoperation.close();

        String totalbyFood,quantity,food_name,messageOrder,price;
        messageOrder="\nOrder\nYour ordered";
        float totalbyOrder=0;
        int j;
        for (int i = 0; i < dictionary.size(); i++) {

            j=i+1;
            /*I start at index 0 and finish at the penultimate index */
            HashMap<String, String> map = dictionary.get(i); //Get the corresponding map from the index
            totalbyFood=map.get("totalByFood").toString();
            price=map.get("price").toString();
            quantity=map.get("quantity").toString();
            food_name=map.get("food_name").toString();
            messageOrder+="\n "+j+" - "+food_name+" ("+price+" $  x  "+ quantity +")  "+ totalbyFood+"$";
            totalbyOrder+=Float.parseFloat(totalbyFood);
        }
        messageOrder+="\n Total = "+totalbyOrder+"$";

        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(messageOrder);//R.string.main_order_message)
        dialogBuilder.setTitle("Details");

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Scan Barcode
                // clean
               // ChangeColorTable(btnTable, "L");
            }
        });
        dialogBuilder.create().show();
    }

    private void showWaiter(final int number) {
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage("message");
        dialogBuilder.setTitle("Title");

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // After called waiter rechange table
                // but still table have some people so showed logined color
                btnTable = chooseTable(number);
                btnTable.setBackgroundResource(R.drawable.main_custom_button_logined);
            }
        });

        dialogBuilder.create().show();
    }

    private void showBill(final int number) {
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage("Accept = finish session");
        dialogBuilder.setTitle("Finish session");

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // after payment recolor
                // TODO after payment also finish the session
                btnTable = chooseTable(number);
                btnTable.setBackgroundResource(R.drawable.main_custom_button_logined);
            }
        });

        dialogBuilder.create().show();
    }

    private void showLogined(final int number) {
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage("message");
        dialogBuilder.setTitle("Title");

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // After called waiter rechange table
                // but still table have some people so showed logined color
                btnTable = chooseTable(number);
                btnTable.setBackgroundResource(R.drawable.main_custom_button_logined);
            }
        });

        dialogBuilder.create().show();
    }

    /**
     * Get ip address of the device
     */
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
           try{
               doLoopProcess2();
           }catch(Exception e)
           {
               Log.d("Exception", ""+e.toString());
           }

      /* and here comes the "trick" */
            handler.postDelayed(this, 10000);
        }
    };



    @Override
    public void onPause() {
         Log.i("Activity", "onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(runnable);
        Log.i("Activity", "onStop()");
        super.onStop();
    }

    public void doLoopProcess2() {

        final ArrayList<HashMap<String, String>> dictionary = sqliteoperation.getTableStatus();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < dictionary.size(); i++) {
                                            /*I start at index 0 and finish at the penultimate index */
                    HashMap<String, String> map = dictionary.get(i); //Get the corresponding map from the index
                    Log.d("DictionaryMAinActivity", map.get(KEY_NUMBER_TABLE) + " --- " +
                            map.get(KEY_KIND_REQUEST) + "------" +
                            map.get(KEY_REQUEST_TEXT) + "------" +
                            map.get(KEY_CONFIRM_SESSION) + "------" +
                            map.get(KEY_SHOW)); /*this is a simple log XD, to verify if there is information.*/
                    TABLE_NAME = map.get(KEY_REQUEST_TEXT);


                    btnTable = chooseTable(Integer.parseInt(map.get(KEY_NUMBER_TABLE).toString()));
                    //get the capital letter from each Map,
                    ChangeColorTable(btnTable, map.get(KEY_KIND_REQUEST).toString().toUpperCase(),
                            Integer.parseInt(map.get(KEY_CONFIRM_SESSION).toString()), Integer.parseInt(map.get(KEY_SHOW).toString()), Integer.parseInt(map.get(KEY_NUMBER_TABLE).toString()));
                }
            }
        });
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
                                                    map.get(KEY_REQUEST_TEXT) + "------" +
                                                    map.get(KEY_CONFIRM_SESSION) + "------" +
                                                    map.get(KEY_SHOW)); /*this is a simple log XD, to verify if there is information.*/
                                            TABLE_NAME = map.get(KEY_REQUEST_TEXT);


                                            btnTable = chooseTable(Integer.parseInt(map.get(KEY_NUMBER_TABLE).toString()));
                                            //get the capital letter from each Map,
                                            ChangeColorTable(btnTable, map.get(KEY_KIND_REQUEST).toString().toUpperCase(),
                                                    Integer.parseInt(map.get(KEY_CONFIRM_SESSION).toString()),Integer.parseInt(map.get(KEY_SHOW).toString()),Integer.parseInt(map.get(KEY_NUMBER_TABLE).toString()));
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
            case 7:
                btnChooseTable = (Button) findViewById(R.id.btn7);
                break;
            case 8:
                btnChooseTable = (Button) findViewById(R.id.btn8);
                break;
            case 9:
                btnChooseTable = (Button) findViewById(R.id.btn9);
                break;

        }
        return btnChooseTable;
    }

    public void ChangeColorTable(Button tableColor, String capitalLetter,int session,int show,int numbertable) {
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
        } else if (capitalLetter.equals("L")) {
                                                /*L  login = the  color logined  */
            tableColor.setBackgroundResource(R.drawable.main_custom_button_logined);
            if(session==0 && show==1){
                ///update show to cero
                sqliteoperationShow = new SqlOperations(getApplicationContext());
                sqliteoperationShow.open();
                sqliteoperationShow.updatevalueShow(numbertable);
                sqliteoperationShow.close();

                showInstantLogin(numbertable);
               //when you accept de dialog update session to 1
            }

        } else {
            // If the result is diferrent to B,O,W , the color change to brown
            // TODO I think we dont need to brown button if the table non use so its red -RIGHT
            tableColor.setBackgroundResource(R.drawable.main_custom_button);
        }
    }

    private void showInstantLogin(final int numbertable) {
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setTitle("test");
        dialogBuilder.setMessage( TABLE_NAME + " is logined");

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SqlOperations sqliteoperationConfirm = new SqlOperations(getApplicationContext());
                sqliteoperationConfirm .open();
                sqliteoperationConfirm .updatevalueConfirm(numbertable);
                sqliteoperationConfirm .close();
                dialog.dismiss();
            }
        });

        dialogBuilder.create().show();
    }

    @Override
    protected void onDestroy() {
        Log.i("Activity", "onStop()");
        handler.removeCallbacks(runnable);
        if(sqliteoperation!=null)  sqliteoperation.close();
       if(sqliteoperation2!=null) sqliteoperation2.close();
        if(sqliteoperationShow!=null)  sqliteoperationShow.close();
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

        return true;
    }

}