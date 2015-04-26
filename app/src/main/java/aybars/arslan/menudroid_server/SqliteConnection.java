package aybars.arslan.menudroid_server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by renesotolira on 21/03/15.
 */
public class SqliteConnection  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DroidRestaurant.db";
    public static final String TABLE_NAME = "Restaurant";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_ORDER = "OrderClient";



    String sqlCreateTableOrder= "CREATE TABLE OrderClient (_id INTEGER PRIMARY KEY, number_table INTEGER, quantity INTEGER, price TEXT, total TEXT, food_name TEXT)";

    String sqlCreateTableRestaurant= "CREATE TABLE Restaurant (_id INTEGER PRIMARY KEY, number_table INTEGER, kind_of_request INTEGER, request_text TEXT)";

    /*Add new tables :
    * Categories
    * Food
    * */
    String sqlCreateTableCategories= "CREATE TABLE Categories (_idCategory INTEGER PRIMARY KEY, category_name TEXT)";
    String sqlCreateTableFood= "CREATE TABLE Food (_idFood INTEGER PRIMARY KEY, categoryID INTEGER, food_name TEXT)";

    public SqliteConnection(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTableRestaurant);
        db.execSQL(sqlCreateTableCategories);
        db.execSQL(sqlCreateTableFood);
        db.execSQL(sqlCreateTableOrder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("DROP TABLE IF EXISTS Restaurant");

        //db.execSQL(sqlCreateTableRestaurant);

    }


}