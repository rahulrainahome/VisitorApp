package visitor.app.visitorapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;


public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //UIView instances declarations.
    EditText txtName, txtCompany, txtMobile, txtEmail, txtNotes, txtDate;
    Spinner spProdInt;
    Button btnSubmit;

    //SQLiteDatabase object declaration.
    SQLiteDatabase mydatabase = null;

    //Other data objects and data-structures.
    ArrayAdapter<String> adapter  = null;
    ArrayList<String> list = null;

    //Flags declarations.
    String selectProdInt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //View Mapping to layout xml
        txtName = (EditText)findViewById(R.id.et_name);
        txtCompany = (EditText)findViewById(R.id.et_company);
        txtMobile = (EditText)findViewById(R.id.et_mobile);
        txtEmail = (EditText)findViewById(R.id.et_email);
        txtNotes = (EditText)findViewById(R.id.et_notes);
        txtDate = (EditText)findViewById(R.id.et_date);
        spProdInt = (Spinner)findViewById(R.id.sp_prod_int);
        btnSubmit = (Button)findViewById(R.id.id_button_submit);

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

        adapter = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
        spProdInt.setOnItemSelectedListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {

        super.onResume();

        //Create and open db. and create table for first time.
        mydatabase = openOrCreateDatabase(Constants.dbname, MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS visitor(id INTEGER PRIMARY KEY NOT NULL, name varchar, company varchar, mobile varchar, email varchar, notes varchar, date varchar, prodint varchar );");

    }

    @Override
    protected void onPause() {

        //Check and close the database if open.
        if(mydatabase != null)
        {
            if(mydatabase.isOpen())
            {
                mydatabase.close();
            }
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save)
        {

        }
        else if(id == R.id.action_discard)
        {
            startActivity(new Intent(FormActivity.this, ProductViewActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectProdInt = list.get(position).toString();
        Snackbar.make(view, "Selected: " + list.get(position).toString(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        //validate the field data.
        String name = txtName.getText().toString();
        String company = txtCompany.getText().toString();
        String mobile = txtMobile.getText().toString();
        String email = txtEmail.getText().toString();
        String notes = txtNotes.getText().toString();
        String date = txtDate.getText().toString();

        //if valid then save in DB.
        mydatabase.execSQL("INSERT INTO visitor (name, company, mobile, email, notes, date, prodint) VALUES ('" + name + "','" + company + "','" + mobile + "','" + email + "','" + notes + "','" + date + "','" + selectProdInt + "')");

    }


}
