package visitor.app.visitorapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import visitor.app.adapter.PhoneAdapter;
import visitor.app.utils.Constants;

/**
 * @class: FormActivity
 * @desc: Class responsible for Adding visitor data.
 */

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //UIView instances declarations.
    EditText txtName, txtCompany, txtEmail, txtNotes, txtDate;
    Spinner spProdInt;
    Button btnSubmit, btnAddPhone;
    ListView lw;

    //SQLiteDatabase object declaration.
    SQLiteDatabase mydatabase = null;

    //Other data objects and data-structures.
    ArrayAdapter<String> adapter  = null;
    ArrayList<String> list = null;
    PhoneAdapter phoneAdapter = null;
    ArrayList<String> holder = null;
    DisplayMetrics metrics;

    //Flags declarations.
    String selectProdInt = "";
    int densityDpi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //View Mapping to layout xml
        txtName = (EditText)findViewById(R.id.et_name);
        txtCompany = (EditText)findViewById(R.id.et_company);
        txtEmail = (EditText)findViewById(R.id.et_email);
        txtNotes = (EditText)findViewById(R.id.et_notes);
        txtDate = (EditText)findViewById(R.id.et_date);
        spProdInt = (Spinner)findViewById(R.id.sp_prod_int);
        btnSubmit = (Button)findViewById(R.id.id_button_submit);
        btnAddPhone = (Button)findViewById(R.id.id_button_addphone);
        lw = (ListView)findViewById(R.id.listView2);

        //Handling ListView
        holder = new ArrayList<String>();
        holder.add("");
        phoneAdapter = new PhoneAdapter(FormActivity.this, this, this, holder);
        lw.setAdapter(phoneAdapter);
        metrics = getResources().getDisplayMetrics();
        densityDpi = (int)(metrics.density * 160f);

        //Set the current date in field.
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String datee = sdfDate.format(c.getTime());
        txtDate.setText(datee.toString());

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
        spProdInt.setAdapter(adapter);
        spProdInt.setOnItemSelectedListener(this);
        btnSubmit.setOnClickListener(this);
        btnAddPhone.setOnClickListener(this);
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
    public void onBackPressed() {

        Toast.makeText(FormActivity.this, "Data Discarded.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(FormActivity.this, ViewActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_visitor:

                startActivity(new Intent(FormActivity.this, ViewActivity.class));
                finish();
                break;
            case R.id.action_product:

                startActivity(new Intent(FormActivity.this, ProductViewActivity.class));
                finish();
                break;
            case R.id.action_docs:

                startActivity(new Intent(FormActivity.this, DocsActivity.class));
                finish();
                break;
            case R.id.action_export:

                startActivity(new Intent(FormActivity.this, ExportActivity.class));
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectProdInt = list.get(position).toString();
   }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v)
    {
        ViewGroup.LayoutParams params = lw.getLayoutParams();
        int x = 0;
        switch (v.getId())
        {
            case R.id.id_button_addphone:

                //Add phone field.
                holder.add("");
                phoneAdapter.notifyDataSetChanged();

                x = densityDpi / 3;
                params.height = x * holder.size();
                lw.setLayoutParams(params);

                break;
            case R.id.id_button_submit:

                //Save the data.
                saveData();

                break;
            case R.id.imageView:

                //Handle phone field remove option.
                String tagVal = String.valueOf(v.getTag());
                Toast.makeText(getApplicationContext(), "" + tagVal, Toast.LENGTH_SHORT).show();
                holder.remove(Integer.parseInt(tagVal));
                x = densityDpi / 3;
                params.height = x * holder.size();
                lw.setLayoutParams(params);
                phoneAdapter.notifyDataSetChanged();

                break;
        }
    }

    /**
     * @method: saveData
     * @desc: This method gets data from fields, validated and saves in DB.
     */
    private void saveData()
    {
        //validate the field data.
        String name = txtName.getText().toString();
        String company = txtCompany.getText().toString();
        String phones = "";
        for(int a = 0; a < holder.size(); a++)
        {
            if(a != 0)
            {
                phones = "" + phones + ",";
            }
            phones = "" + phones + holder.get(a);
        }
        //String phones = "123,345,4544,234324,2313"; //holder -- holding all the phone numbers entered.
        String prodInter = selectProdInt.toString(); //-- holds the selected product interest for this visitor.
        String email = txtEmail.getText().toString();
        String notes = txtNotes.getText().toString();
        String date = txtDate.getText().toString();

        //if valid then save in DB.
        //mydatabase.execSQL("INSERT INTO visitor (name, company, mobile, email, notes, date, prodint) VALUES ('" + name + "','" + company + "','" + 00000 + "','" + email + "','" + notes + "','" + date + "','" + selectProdInt + "')");
        String raw = "INSERT INTO visitor (name, company, mobile, email, notes, date, prodint) VALUES (?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement stmt = mydatabase.compileStatement(raw);
        stmt.bindString(1, name);
        stmt.bindString(2, company);
        stmt.bindString(3, phones);
        stmt.bindString(4, email);
        stmt.bindString(5, notes);
        stmt.bindString(6, date);
        stmt.bindString(7, prodInter);
        stmt.execute();

        Toast.makeText(getApplicationContext(), "Visitor data added", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "" + phones, Toast.LENGTH_LONG).show();
        startActivity(new Intent(FormActivity.this, ViewActivity.class));
        finish();
    }


}
