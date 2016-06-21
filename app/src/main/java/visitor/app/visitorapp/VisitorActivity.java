package visitor.app.visitorapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import visitor.app.model.Visitor;

/**
 * @class: VisitorActivity
 * @desc: Class responsible for showing Detail of a visitor.
 */

public class VisitorActivity extends AppCompatActivity {

    //SQLiteDatabase object declaration.
    SQLiteDatabase mydatabase = null;

    //UI Controls
    TextView txtName, txtCompany, txtMobile, txtEmail, txtNotes, txtDate, txtProdInt;
    ImageView imgEmail, imgCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Are you sure to delete this?", Snackbar.LENGTH_LONG)
                        .setAction("delete ?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //Delete this current visitor.
                                Toast.makeText(getApplicationContext(), "To be deleted.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });



        //CREATE TABLE IF NOT EXISTS visitor (
        // id INTEGER PRIMARY KEY NOT NULL,
        // name varchar,
        // company varchar,
        // mobile varchar,
        // email varchar,
        // notes varchar,
        // date varchar,
        // prodint varchar

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(VisitorActivity.this, ViewActivity.class));
        super.onBackPressed();

    }

    @Override
    protected void onPause() {

        if(mydatabase != null)
        {
            if(mydatabase.isOpen())
            {
                mydatabase.close();
            }
        }
        super.onPause();
    }
}
