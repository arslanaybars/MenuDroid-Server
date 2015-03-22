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
    String sqlCreateTableRestaurant= "CREATE TABLE Restaurant (_id INTEGER PRIMARY KEY, number_table INTEGER, kind_of_request INTEGER, request_text TEXT)";

    public SqliteConnection(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTableRestaurant);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("DROP TABLE IF EXISTS Restaurant");

        //db.execSQL(sqlCreateTableRestaurant);

    }


}