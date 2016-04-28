package visitor.app.visitorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;


public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    EditText txtName, txtCompany, txtMobile, txtEmail, txtNotes, txtDate;
    Spinner spProdInt;
    Button btnSubmit;

    ArrayAdapter<String> adapter  = null;

    String selectProdInt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        txtName = (EditText)findViewById(R.id.et_name);
        txtCompany = (EditText)findViewById(R.id.et_company);
        txtMobile = (EditText)findViewById(R.id.et_mobile);
        txtEmail = (EditText)findViewById(R.id.et_email);
        txtNotes = (EditText)findViewById(R.id.et_notes);
        txtDate = (EditText)findViewById(R.id.et_date);
        spProdInt = (Spinner)findViewById(R.id.sp_prod_int);
        btnSubmit = (Button)findViewById(R.id.id_button_submit);

        Constants.list = new ArrayList<String>();
        for(int i = 0; i < 20; i++)
        {
            Constants.list.add("ProdInt: " + i);
        }

        adapter = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_spinner_dropdown_item, Constants.list);
        spProdInt.setOnItemSelectedListener(this);

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectProdInt = Constants.list.get(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        //validate the field data.
        //if valid then save in DB.
    }
}
