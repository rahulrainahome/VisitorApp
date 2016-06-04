package visitor.app.visitorapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import visitor.app.adapter.VisitorAdapter;
import visitor.app.model.Visitor;
import visitor.app.utils.Constants;

/**
 * @class: ViewActivity
 * @desc: Class responsible for Showing the list of visitors.
 */

public class ViewActivity extends AppCompatActivity {

    //Data Objects for ListView
    ListView lw;
    ArrayList<Visitor> list = new ArrayList<Visitor>();
    VisitorAdapter adapter;

    //SQLiteDatabase object declaration.
    SQLiteDatabase mydatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lw = (ListView)findViewById(R.id.listView3);

        int ii = 0;
        if(mydatabase == null)
        {
            mydatabase = openOrCreateDatabase(Constants.dbname, MODE_PRIVATE, null);
        }
        Cursor expenseSet = mydatabase.rawQuery("Select id, name, company, mobile, email, notes, date, prodint from visitor", null);
        while(expenseSet.moveToNext())
        {
            Visitor visitor = new Visitor();
            visitor.id = Integer.parseInt(expenseSet.getString(0));
            visitor.name = "" + expenseSet.getString(1);
            visitor.category = "" + expenseSet.getString(2);
            visitor.mobile = "" + expenseSet.getString(3);
            visitor.email = "" + expenseSet.getString(4);
            visitor.notes = "" + expenseSet.getString(5);
            visitor.date = "" + expenseSet.getString(6);
            visitor.prodint = "" + expenseSet.getString(7);

            list.add(visitor);
            ii++;
        }
        Toast.makeText(getApplicationContext(), "Total- " + ii, Toast.LENGTH_SHORT).show();

        adapter = new VisitorAdapter(ViewActivity.this, this, list);
        lw.setAdapter(adapter);

        if(mydatabase != null)
        {
            if(mydatabase.isOpen())
            {
                mydatabase.close();
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewActivity.this.startActivity(new Intent(ViewActivity.this, FormActivity.class));
                ViewActivity.this.finish();
            }
        });
    }

}
