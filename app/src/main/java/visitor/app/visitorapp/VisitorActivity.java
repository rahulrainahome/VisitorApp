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
import visitor.app.utils.Constants;

/**
 * @class: VisitorActivity
 * @desc: Class responsible for showing Detail of a visitor.
 */

public class VisitorActivity extends AppCompatActivity {

    //SQLiteDatabase object declaration.
    SQLiteDatabase mydatabase = null;

    //UI Controls
    TextView txtName, txtCompany, txtMobile, txtEmail, txtNotes, txtDate, txtProdInt, txtAttachment;
    ImageView imgEmail, imgCall, imgAttach;

    Visitor visitor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtAttachment = (TextView)findViewById(R.id.id_number_attach);
        txtName = (TextView)findViewById(R.id.id_name);
        txtCompany = (TextView)findViewById(R.id.id_company);
        txtMobile = (TextView)findViewById(R.id.id_phone);
        txtEmail = (TextView)findViewById(R.id.id_email);
        txtNotes = (TextView)findViewById(R.id.id_notes);
        txtDate = (TextView)findViewById(R.id.id_date);
        txtProdInt = (TextView)findViewById(R.id.id_prod_int);
        imgEmail = (ImageView)findViewById(R.id.id_email_send);
        imgCall = (ImageView)findViewById(R.id.id_call_phone);
        imgAttach = (ImageView)findViewById(R.id.id_email_attach);

        visitor = Constants.selectedVisitor;

        txtName.setText("" + visitor.name);
        txtCompany.setText("" + visitor.category);
        txtMobile.setText("" + visitor.mobile);
        txtEmail.setText("" + visitor.email);
        txtNotes.setText("" + visitor.notes);
        txtDate.setText("" + visitor.date);
        txtProdInt.setText("" + visitor.prodint);

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
