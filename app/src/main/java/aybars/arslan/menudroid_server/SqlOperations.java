package aybars.arslan.menudroid_server;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/***
 * Clase para manipular las operaciones simples con db local como son insert,delete,update,etc
 */
public class SqlOperations {

    // Database fields
    private SQLiteDatabase database;
    private SqliteConnection sqliteconnection;
    private static final String KEY_NUMBER_TABLE = "number_table";
    private static final String KEY_KIND_REQUEST = "kind_of_request";
   private static final String KEY_REQUEST_TEXT = "request_text";


    public SqlOperations(Context context) {
        sqliteconnection = new SqliteConnection(context);  //conexion y/o creacion de DB
    }

    public void open() throws SQLException {
        database = sqliteconnection.getWritableDatabase(); // avaliable to write in the db.
    }

    public void close() {
        sqliteconnection.close(); //close db
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
       * */
       //GET THE REQUEST CHAIN and split the first character and get the table number
       String kind_request=request.substring(0,1);
       String number=request.substring((request.length()-1));

       ContentValues row = new ContentValues();
       row.put(KEY_NUMBER_TABLE, number);
       row.put(KEY_KIND_REQUEST, kind_request);
       row.put(KEY_REQUEST_TEXT, request);
       database.insert(SqliteConnection.TABLE_NAME, null, row); //insert in DB the request

       Log.d("REQUEST","Kind is : "+kind_request+
       "request : "+ request+
       "number : "+number);

   }


}
