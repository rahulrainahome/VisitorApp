package visitor.app.visitorapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

import visitor.app.utils.Constants;

/**
 * @class: DocsActivity
 * @desc: Class responsible for Document and Attachment Add/Del.
 */

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
        s = getSharedPreferences(Constants.pref_docs, 0);
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
        lw.setAdapter(adapter);
        lw.setOnItemLongClickListener(this);

        //FloatActionButton to add new document.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Pick new file using intent.
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_docs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_visitor:

                startActivity(new Intent(DocsActivity.this, ViewActivity.class));
                finish();
                break;

            case R.id.action_product:

                startActivity(new Intent(DocsActivity.this, ProductViewActivity.class));
                finish();
                break;

            case R.id.action_export:

                startActivity(new Intent(DocsActivity.this, ExportActivity.class));

                break;
        }
        return true;
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
                            for (int a = 0; a < json.length(); a++) {
                                if (a == pos) {
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

                            for (int a = 0; a < json.length(); a++) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    saveDataInPref(FilePath);
                }
                break;

        }
    }

    /**
     * @method saveDataInPref
     * @param filePath
     * @desc Gets fileName and path and saves in cache as JSONArray format.
     */

    private void saveDataInPref(String filePath)
    {
        Toast.makeText(getApplicationContext(), "" + filePath, Toast.LENGTH_LONG).show();
        String data = s.getString("data","[]");
        try
        {
            //Save new json array in cache.
            JSONArray jarray = new JSONArray(data);
            jarray.put("" + filePath);
            SharedPreferences.Editor se = getSharedPreferences(Constants.pref_docs, 0).edit();
            se.putString("data", jarray.toString());
            se.commit();

            //Get data from sharedPref and parse into JSONArray and then ArrayList.
            String strData = s.getString("data","[]");
            JSONArray jsonArray = new JSONArray(strData);
            list.clear();
            for(int a = 0; a < jsonArray.length(); a++)
            {
                list.add("" + jsonArray.getString(a));
            }
            adapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
