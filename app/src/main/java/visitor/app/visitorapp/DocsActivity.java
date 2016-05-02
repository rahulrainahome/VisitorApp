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

import java.util.ArrayList;

import visitor.app.utils.Constants;

public class DocsActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    ListView lw;
    ArrayAdapter<String> adapter = null;
    ArrayList<String> list = null;
    SharedPreferences s = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get sharedPreferences
        s = getSharedPreferences(Constants.pref_prod, 0);
        try
        {
            //Get data from sharedPref and parse into JSONArray and then ArrayList.
            String strData = s.getString("data","[]");
            JSONArray jsonArray = new JSONArray(strData);
            list = new ArrayList<String>();
            for(int a = 0; a < jsonArray.length(); a++)
            {
                list.add("" + jsonArray.getString(a));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Some internal error ocurred.\n Please restart the app.", Toast.LENGTH_LONG).show();
        }

        //Init listView and adapter and do longitemclick event handling.
        lw = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(DocsActivity.this, android.R.layout.simple_list_item_1, list);
        lw.setOnItemLongClickListener(this);

        //FloatActionButton to add new document.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create new Attachment
                createAttachment();
            }
        });

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        final int pos = position;

        //Prompt in snackbar before deletion.
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
                                if(a == pos)
                                {
                                    //leave the deleting entry.
                                    continue;
                                }
                                newjson.put("" + json.getString(a));
                            }

                            //Save new data in cache.
                            SharedPreferences.Editor se = getSharedPreferences(Constants.pref_docs, 0).edit();
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
     * @method: createAttachment
     * @desc: Creates input AlertDialog to input  string and then inserts in the cache.
     */
    private void createAttachment()
    {
        //Create AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Document");
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
                            SharedPreferences.Editor se = getSharedPreferences(Constants.pref_docs, 0).edit();
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
