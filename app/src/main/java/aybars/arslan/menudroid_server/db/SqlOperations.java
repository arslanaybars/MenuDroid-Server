package aybars.arslan.menudroid_server.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * Clase para manipular las operaciones simples con db local como son insert,delete,update,etc
 */
public class SqlOperations {

    //the next two variable it is only for debugging test.
    private String TAG = this.getClass().getSimpleName();
    private boolean LogDebug=true;

    // Database fields
    private SQLiteDatabase database;
    private SqliteConnection sqliteconnection;
    private static final String KEY_NUMBER_TABLE = "number_table";
    private static final String KEY_KIND_REQUEST = "kind_of_request";
   private static final String KEY_REQUEST_TEXT = "request_text";
    private static final String KEY_CONFIRM_SESSION = "confirmSession";
    private static final String KEY_SHOW = "show";



    private static final String KEY_QTY = "quantity";
    private static final String KEY_FOOD_NAME = "food_name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_TOTAL = "total";


    public SqlOperations(Context context) {
        sqliteconnection = new SqliteConnection(context);  //conexion y/o creacion de DB
    }

    public void open() throws SQLException {
        database = sqliteconnection.getWritableDatabase(); // avaliable to write in the db.
    }

    public void close() {
        sqliteconnection.close(); //close db
    }

    public String getSpecificTableStatus(int number) {
        Cursor cursor;
        String status="";
        String select = "SELECT kind_of_request FROM Restaurant where number_table="+number+" order by _id desc limit 1";
        cursor = database.rawQuery(select, null);
        if (cursor.getCount() == 0) // if there are no elements do nothing
        {
            Log.d(TAG, "no elements");
        } else {
            cursor.moveToNext();
            status=cursor.getString(0);
        }
        if(cursor!=null) cursor.close();
        return status;
    }


    public void updatevalueShow(int numbertable){
        /*This method is to doesnt show the Login Dialog*/
        ContentValues row = new ContentValues();
        row.put(KEY_SHOW,0);
        database.update(SqliteConnection.TABLE_NAME, row, "number_table "+"="+numbertable, null);
    }

    public void updatevalueConfirm(int numbertable){
        /*This method is to know if the session was confirmed*/
        ContentValues row = new ContentValues();
        row.put(KEY_CONFIRM_SESSION,1);
        database.update(SqliteConnection.TABLE_NAME, row, "number_table "+"="+numbertable, null);
    }

    public  ArrayList<HashMap<String,String>>  getTableStatus (){

        Cursor cursor;
        ArrayList<HashMap<String, String>> allElementsDictionary = new ArrayList<HashMap<String, String>>();
        String select = "SELECT distinct(number_table) ,kind_of_request,request_text,confirmSession,show FROM Restaurant group by number_table order by _id desc";
        /*The  rawQuery do a query that we write before (select ... from restaurant ...)*/
        cursor = database.rawQuery(select,null);
        if(cursor.getCount()==0) // if there are no elements do nothing
        {
            Log.d(TAG,"no elements");
                    ///return allElements dictionary empty.
        }
        else
        { //if there are elemnts
            Log.d(TAG,"there are elemnets");
            //get all the rows and pass the data to allElements dictionary.

                while(cursor.moveToNext()){

                    //The cursor save all the rows returned by the query.
                    //moveToNext is to advance at the next row.
                   /*
                               COLUMN (0) number_table ,  COLUMN (1) kind_of_request ,  COLUMN (2) request_Text
                   * row (1) =      2                   O                           O-MenuDroidTable2
                   * row (2) ==    1                    W
                   * ...
                   * moveToNext means if I am in row(2) I will pass to row(3)
                   *
                   *
                   *
                   *
                   *
                   * */

                    //cursor.getString(1) means I get the data from column with index 1 in this case "kind_of_request"
                    /*
                    *
                    * */


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(KEY_NUMBER_TABLE, cursor.getString(0));
                    map.put(KEY_KIND_REQUEST, cursor.getString(1));
                    map.put(KEY_REQUEST_TEXT, cursor.getString(2));
                    map.put(KEY_CONFIRM_SESSION, cursor.getString(3));
                    map.put(KEY_SHOW, cursor.getString(4));
                    allElementsDictionary.add(map);

                    if (LogDebug) {
                        Log.d(TAG, "number : " + cursor.getString(0) +
                                        "\n kind :" + cursor.getString(1)+
                                        "\n text :" + cursor.getString(2)+
                                        "\n confirmSession :" + cursor.getString(3)+
                                        "\n show :" + cursor.getString(4)
                        );
                    }

            }
        }
        if (cursor!=null)
        {
            cursor.close();//It is important close the cursor when you finish your process.
        }

        return allElementsDictionary;
    }

   public void insertRequest(String request){
       /*The request could be:
       * B-MenuDroidTable#
       * O-
       * W-
       *
       * Where # is the table number e.g. 1,2,3,4 ,etc
       * B means Bill
       * O means order
       * W mean Waiter
       * L mean Login
       * */
       //GET THE REQUEST CHAIN and split the first character and get the table number
       String kind_request=request.substring(0,1);
       int last=request.lastIndexOf("Table")+5;
       Log.d("LAST", ""+last);
       String number=request.substring(last);
       Log.d("LAST", ""+number);
       ContentValues row = new ContentValues();
       row.put(KEY_NUMBER_TABLE, number);
       row.put(KEY_KIND_REQUEST, kind_request);
       row.put(KEY_REQUEST_TEXT, request);
       row.put(KEY_CONFIRM_SESSION, 0);
       row.put(KEY_SHOW, 1);
       database.insert(SqliteConnection.TABLE_NAME, null, row); //insert in DB the request

       Log.d("REQUEST","Kind is : "+kind_request+
       "request : "+ request+
       "number : "+number);

   }
    public void insertOrder(JSONArray order,String numberTable) throws JSONException {

        for (int i = 0; i < order.length(); i++) { //Search inner the Categories array
            String totalByFood = order.getJSONObject(i).getString("totalByFood");
            String price = order.getJSONObject(i).getString("price");
            String quantity = order.getJSONObject(i).getString("quantity");
            String food_name = order.getJSONObject(i).getString("food_name");

            Log.d("TOTAL", "The total by food is " + totalByFood);
            Log.d("TOTAL", "The price " + price);
            Log.d("TOTAL", "The qty" + quantity);
            Log.d("TOTAL", "The food " + food_name);
            ContentValues row = new ContentValues();
            row.put(KEY_FOOD_NAME, food_name);
            row.put(KEY_QTY, Integer.parseInt(quantity));
            row.put(KEY_PRICE, price);
            row.put(KEY_NUMBER_TABLE, Integer.parseInt(numberTable));
            row.put(KEY_TOTAL,totalByFood);
            database.insert(SqliteConnection.TABLE_NAME_ORDER, null, row); //insert in DB the request
        }
    }


    public  ArrayList<HashMap<String,String>>  getOrder(int number){

        Cursor cursor;
        ArrayList<HashMap<String, String>> allElementsDictionary = new ArrayList<HashMap<String, String>>();
        String select = "SELECT quantity,price,food_name,total,number_table from OrderClient where number_table="+number+"";
        cursor = database.rawQuery(select,null);
        if(cursor.getCount()==0) // if there are no elements do nothing
        {
            Log.d(TAG,"no elements");
        }
        else
        { //if there are elemnts
            Log.d(TAG,"there are elemnets");
            //get all the rows and pass the data to allElements dictionary.
            float totalByOrder=0;
            while(cursor.moveToNext()){
                int qty=Integer.parseInt(cursor.getString(0));

                if(qty>0){
                    float totalByFood=Float.parseFloat(cursor.getString(3));// qty * price
                    totalByOrder+=totalByFood;
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(KEY_QTY, cursor.getString(0));
                    map.put(KEY_PRICE, cursor.getString(1));
                    map.put(KEY_FOOD_NAME, cursor.getString(2));
                    map.put("totalByFood", cursor.getString(3));
                    allElementsDictionary.add(map);
                    if (LogDebug) {
                        Log.d(TAG, "qty : " + cursor.getString(0) +
                                        "\n price :" + cursor.getString(1)+
                                        "\n foodname :" + cursor.getString(2)+
                                        "\n totalByFood :"+ cursor.getString(3)+
                                        "\n table :"+ cursor.getString(4)
                        );
                    }
                }
                Log.d(TAG,"total is :"+totalByOrder);
            }
        }
        if (cursor!=null)
        {
            cursor.close();//It is important close the cursor when you finish your process.
        }
        return allElementsDictionary;
    }


}
