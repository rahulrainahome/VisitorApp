package visitor.app.visitorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

    //Data structures for holding data globally.
    Visitor visitor;
    ArrayList<String> listDocs;
    public static ArrayList<String> attachDocs;

    //Other Android Service objects.
    SharedPreferences s = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        s = getSharedPreferences(Constants.pref_docs, 0);

        //Mappuingi all the UI components
        txtAttachment = (TextView)findViewById(R.id.id_number_attach);
        txtName = (TextView)findViewById(R.id.id_name);
        txtCompany = (TextView)findViewById(R.id.id_company);
        txtMobile = (TextView)findViewById(R.id.id_phone);
        txtEmail = (TextView)findViewById(R.id.id_email);
        txtNotes = (TextView)findViewById(R.id.id_notes);
        txtDate = (TextView)findViewById(R.id.id_date);
        txtProdInt = (TextView)findViewById(R.id.id_prod_int);
        imgEmail = (ImageView)findViewById(R.id.id_email_send);
        imgAttach = (ImageView)findViewById(R.id.id_email_attach);
        imgCall = (ImageView)findViewById(R.id.id_call_phone);
        imgCall.setVisibility(View.GONE);

        //Get and show the selected visitor data.
        visitor = Constants.selectedVisitor;

        txtName.setText("" + visitor.name);
        txtCompany.setText("" + visitor.category);

        //Splitting mobile numbers around comma (,)
        String mobs[] = visitor.mobile.split(",");
        String mob = "";
        for(int nmob = 0; nmob < mobs.length; nmob++)
        {
            if(nmob == 0)
            {
                mob = "" + mobs[nmob].toString();
            }
            else
            {
                mob = "" + mob + "\n" + mobs[nmob].toString();
            }
        }
        txtMobile.setText("" + mob);
        txtEmail.setText("" + visitor.email);
        txtNotes.setText("" + visitor.notes);
        txtDate.setText("" + visitor.date);
        txtProdInt.setText("" + visitor.prodint);

        //make mobile hyperlink and setting the hypertext fonts.
        Linkify.addLinks(txtMobile, Linkify.WEB_URLS | Linkify.PHONE_NUMBERS | Linkify.EMAIL_ADDRESSES);
        txtMobile.setLinkTextColor(txtName.getCurrentTextColor());
        stripUnderlines(txtMobile);

        //Get all the docs from cache and hold in global variable.
        try
        {
            attachDocs = new ArrayList<String>();
            listDocs = new ArrayList<String>();
            String cache_docs = s.getString("data", "[]");
            JSONArray jsonArrayDocs = new JSONArray(cache_docs.toString());
            for(int i = 0; i <jsonArrayDocs.length(); i++)
            {
                listDocs.add(jsonArrayDocs.getString(i).toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "EXCEPTION: Cache Docs Fetching and Parsing.", Toast.LENGTH_SHORT).show();
        }

        //FloatingActionButton code here.
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

        imgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //send email with attachments.
                //use email intent with attachments and receiver email as Intent Extra.
                Toast.makeText(getApplicationContext(), "Will send the email", Toast.LENGTH_SHORT).show();
            }
        });

        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Attach documents.
                createPopupList();
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

        //Ensure DB close before moving to background.
        if(mydatabase != null)
        {
            if(mydatabase.isOpen())
            {
                mydatabase.close();
            }
        }
        super.onPause();
    }

    /**
     * @method stripUnderlines
     * @param textView
     * @desc This method removes the underlines from textView Linkables.
     */
    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    /**
     * @class: URLSpanNoUnderline
     * @desc: removes span and underline from text in text View
     */
    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    /**
     * @method createPopupList
     * @decs Creates Popup List View in alert dialog for displaying attachments.
     */
    private void createPopupList()
    {
        ListView lw = new ListView(getApplicationContext());
        ArrayAdapter<String> adapter = null;
        attachDocs.clear();

        adapter = new ArrayAdapter<String>(VisitorActivity.this, android.R.layout.simple_list_item_checked, listDocs);
        lw.setAdapter(adapter);

        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckedTextView check = (CheckedTextView) view;
                TextView textItem = (TextView) view;
                String item = textItem.getText().toString();

                if (check.isChecked()) {
                    check.setChecked(false);
                    //remove item from attachment.
                    for (int i = 0; i < attachDocs.size(); i++) {
                        if (attachDocs.get(i).trim().equalsIgnoreCase(item.trim())) {
                            attachDocs.remove(i);
                            break;
                        }
                    }
                } else {
                    check.setChecked(true);
                    //add item to attachment.
                    attachDocs.add(item);

                }
                txtAttachment.setText(attachDocs.size() + " Email Attachments");
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitorActivity.this);
        alertDialogBuilder.setPositiveButton("Attach", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(VisitorActivity.this, "" + attachDocs.size() + " documents attached", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(android.R.drawable.ic_menu_share);
        alertDialogBuilder.setTitle("Select Attachments");
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(lw);
        alertDialog.show();
    }
}
