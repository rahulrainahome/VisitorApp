package visitor.app.visitorapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProductViewActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    ListView lw;
    ArrayAdapter<String> adapter = null;
    ArrayList<String> list = null;
    SharedPreferences s = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lw = (ListView)findViewById(R.id.listView1);

        //Get Product Interest data from Shared Prederences.
        s = getSharedPreferences(Constants.pref_prod, 0);
        String prodInt ="" + s.getString("data","[]");
        list = new ArrayList<String>();

        //parse data into JSONArray and then into ArrayList
        try
        {
            JSONArray jarray = new JSONArray(prodInt.toString());
            for(int i = 0; i < jarray.length(); i++)
            {
                list.add("" + jarray.getString(i));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "JSONArray parsing Exception while processing Product Interest list", Toast.LENGTH_LONG).show();
        }

        adapter = new ArrayAdapter<String>(ProductViewActivity.this, android.R.layout.simple_list_item_1, list);
        lw.setAdapter(adapter);
        lw.setOnItemLongClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProductInterest();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        Snackbar.make(view, "Are you sure to delete this?", Snackbar.LENGTH_LONG)
                .setAction("Yes ?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            //Get the old data from cache and remove selected data and again save in cache.
                            String oldStr = s.getString("data", "[]");
                            JSONArray json = new JSONArray(oldStr);
                            JSONArray newjson = new JSONArray();
                            for(int a = 0; a < json.length(); a++)
                            {
                                if(a == position)
                                {
                                    continue;
                                }
                                newjson.put("" + json.getString(a));
                            }
                            SharedPreferences.Editor se = getSharedPreferences(Constants.pref_prod, 0).edit();
                            se.putString("data", newjson.toString());
                            se.commit();

                            //Again get data from cache and parse into list and reload listView.
                            json = new JSONArray(s.getString("data", "[]"));
                            list.clear();
                            for(int a = 0; a < json.length(); a++)
                            {
                                list.add("" + json.getString(a));
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Successfully Deleted.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Exception: Some internal error ocurred.", Toast.LENGTH_LONG).show();
                        }
                    }

                }).show();
        return true;
    }

    /**
     * @method: createProductInterest
     * @desc: Creates input AlertDialog to inout product interest string and then inserts in the cache.
     */
    private void createProductInterest()
    {
        //Create AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Product Interest");
        builder.setCancelable(false);

        // Set up the EditText.
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newStr = input.getText().toString();
                        try {
                            String oldStr = s.getString("data", "[]");
                            JSONArray json = new JSONArray(oldStr);
                            json.put("" + newStr);
                            SharedPreferences.Editor se = getSharedPreferences(Constants.pref_prod, 0).edit();
                            se.putString("data", json.toString());
                            se.commit();
                            json = new JSONArray(s.getString("data", "[]"));
                            list.clear();
                            for (int a = 0; a < json.length(); a++) {
                                list.add("" + json.getString(a));
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Successfully Added.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Exception: Some internal error ocurred.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }
        );
        builder.show();
    }
}
