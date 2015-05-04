package aybars.arslan.menudroid_server;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import java.util.ArrayList;
import java.util.HashMap;


public class OrderDetailsActivity extends ActionBarActivity {
    private SqlOperations sqliteoperation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Intent intent = getIntent();
        String numberString = intent.getStringExtra("number");
        Log.d("number", "the number is : "+numberString);
        showDialog(numberString);
    }

    public void showDialog(String number){

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
        dialogBuilder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_details, menu);
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
