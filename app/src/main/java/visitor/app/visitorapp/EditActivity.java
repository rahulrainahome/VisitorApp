package visitor.app.visitorapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import visitor.app.adapter.EditPhoneAdapter;
import visitor.app.adapter.PhoneAdapter;
import visitor.app.model.Visitor;
import visitor.app.utils.Constants;


/**
 * @class: EditActivity
 * @desc: Class responsible for Editing the selected visitor data.
 */

public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //UIView object declarations.
    EditText txtName, txtCompany, txtEmail, txtNotes;
    TextView txtDate;
    Spinner spProdInt;
    Button btnSubmit, btnAddPhone;
    ListView lw;

    //SQLiteDatabase object declaration.
    SQLiteDatabase mydatabase = null;

    //Other data objects and data-structures.
    //For handling Spinner
    ArrayAdapter<String> adapter  = null;
    ArrayList<String> list = null;
    //For handling PhoneListView
    EditPhoneAdapter editPhoneAdapter = null;
    ArrayList<String> holder = null;
    DisplayMetrics metrics;

    //Flags declarations.
    String selectProdInt = "";
    int densityDpi = 0;

    //Today
    int DAY, MONTH, YEAR;

    //Model Class
    Visitor visitor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //View Mapping to xml view
        txtName = (EditText)findViewById(R.id.et_name);
        txtCompany = (EditText)findViewById(R.id.et_company);
        txtEmail = (EditText)findViewById(R.id.et_email);
        txtNotes = (EditText)findViewById(R.id.et_notes);
        txtDate = (TextView)findViewById(R.id.et_date);
        spProdInt = (Spinner)findViewById(R.id.sp_prod_int);
        btnSubmit = (Button)findViewById(R.id.id_button_submit);
        btnAddPhone = (Button)findViewById(R.id.id_button_addphone);
        lw = (ListView)findViewById(R.id.listView2);

        //Getting calander time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String datee = sdfDate.format(c.getTime());
        MONTH = c.get(Calendar.MONTH);
        DAY = c.get(Calendar.DAY_OF_MONTH);
        YEAR = c.get(Calendar.YEAR);

        //Adding Event handler.
        spProdInt.setOnItemSelectedListener(this);
        txtDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnAddPhone.setOnClickListener(this);

        //Populate the old data values on UI.
        visitor = Constants.selectedVisitor;
        txtDate.setText(visitor.date.trim());
        txtCompany.setText(visitor.category);
        txtEmail.setText(visitor.email);
        txtNotes.setText(visitor.notes);
        txtName.setText(visitor.name);

        //Splitting into phone array.
        String phoneNos[] = visitor.mobile.split(",");
        holder = new ArrayList<String>();
        for(int pno = 0; pno < phoneNos.length; pno++)
        {
            holder.add(phoneNos[pno].trim());
        }

        //Handling phone ListView
        editPhoneAdapter = new EditPhoneAdapter(EditActivity.this, this, this, holder);
        lw.setAdapter(editPhoneAdapter);

        //handling phone list View height-dimension
        metrics = getResources().getDisplayMetrics();
        densityDpi = (int)(metrics.density * 160f);
        ViewGroup.LayoutParams params = lw.getLayoutParams();
        int x = 0;
        x = densityDpi / 3;
        params.height = x * holder.size();
        lw.setLayoutParams(params);

        //Get Product-Interest from SharedPreferences into list and populate spinner
        SharedPreferences s = getSharedPreferences(Constants.pref_prod, 0);
        String prodInt = s.getString("data","[]");
        list = new ArrayList<String>();
        try {
            JSONArray jarray = new JSONArray(prodInt.toString());
            for(int i = 0; i < jarray.length(); i++) {
                if(jarray.getString(i).trim().equals(visitor.prodint.trim()))
                {
                    list.add(0, "" + jarray.getString(i));
                }
                else
                {
                    list.add("" + jarray.getString(i));
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "JSONArray parsing Exception while processing Product Interest list", Toast.LENGTH_LONG).show();
        }
        adapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
        spProdInt.setAdapter(adapter);

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

        startActivity(new Intent(EditActivity.this, ViewActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_visitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_discard:

                //Dont save and navigate to visitor list.
                startActivity(new Intent(this, ViewActivity.class));
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
                editPhoneAdapter.notifyDataSetChanged();

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
                //Toast.makeText(getApplicationContext(), "" + tagVal, Toast.LENGTH_SHORT).show();
                holder.remove(Integer.parseInt(tagVal));
                x = densityDpi / 3;
                params.height = x * holder.size();
                lw.setLayoutParams(params);
                editPhoneAdapter.notifyDataSetChanged();

                break;
            case R.id.et_date:

                //DatePickerDialog
                showDialog(999);

                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, myDateListener, YEAR, MONTH, DAY);
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

        String raw = "update visitor set name = ?, company = ?, mobile = ?, email = ?, notes = ?, date = ?, prodint = ? where id = " + Constants.selectedVisitor.id;
        SQLiteStatement stmt = mydatabase.compileStatement(raw);
        stmt.bindString(1, name);
        stmt.bindString(2, company);
        stmt.bindString(3, phones);
        stmt.bindString(4, email);
        stmt.bindString(5, notes);
        stmt.bindString(6, date);
        stmt.bindString(7, prodInter);
        stmt.execute();

        Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "" + phones, Toast.LENGTH_LONG).show();
        startActivity(new Intent(EditActivity.this, ViewActivity.class));
        finish();
    }

    /**
     * @InnerClass: DatePickerDialog
     * @desc: Inner class for datePickerDialog Event Handling.
     */
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

            showDate(arg1, arg2+1, arg3);
        }

        private void showDate(int year, int month, int day) {
            Date dt = new Date();
            dt.setMonth(month - 1);
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            String month_name = month_date.format(dt.getTime());
            txtDate.setText(new StringBuilder().append(day).append("-")
                    .append(month_name).append("-").append(year));
        }

    };
}
