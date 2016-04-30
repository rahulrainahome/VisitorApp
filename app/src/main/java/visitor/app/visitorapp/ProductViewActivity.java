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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get Product Interest data from Shared Prederences.
        SharedPreferences s = getSharedPreferences(Constants.pref_prod, 0);
        String prodInt = s.getString("data","[]");
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
                createCategory();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Snackbar.make(view, "Are you sure to delete this ?", Snackbar.LENGTH_LONG)
                .setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //delete the item from activity.
                    }
                }).show();
        return true;
    }

    /**
     * @method: createCategory
     * @desc: Creates input AlertDialog to inout category string and then inserts in the SQLite DB.
     */
    private void createCategory()
    {
        //Create AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Category Name");
        builder.setCancelable(false);

        // Set up the EditText.
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {

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
