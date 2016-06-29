package visitor.app.visitorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        txtAttachment.setVisibility(View.GONE);

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

        //Reset all the Attachments.
        Constants.attachDocs = new ArrayList<String>();

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
                                try {

                                    mydatabase = openOrCreateDatabase(Constants.dbname, MODE_PRIVATE, null);
                                    mydatabase.execSQL("CREATE TABLE IF NOT EXISTS visitor(id INTEGER PRIMARY KEY NOT NULL, name varchar, company varchar, mobile varchar, email varchar, notes varchar, date varchar, prodint varchar );");
                                    mydatabase.execSQL("DELETE FROM visitor where id = " + visitor.id + ";");
                                    Toast.makeText(getApplicationContext(), "Deleted.", Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e) {

                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Error while deleting data", Toast.LENGTH_SHORT).show();
                                }
                                startActivity(new Intent(VisitorActivity.this, ViewActivity.class));
                                finish();
                            }
                        }).show();
            }
        });

        imgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //send email with attachments.
                //use email intent with attachments and receiver email as Intent Extra.
                ArrayList<Uri> sendDoc = new ArrayList<Uri>();  //Attachments Array
                Iterator<String> iterator = Constants.attachDocs.iterator();
                String dataVal = "";
                while (iterator.hasNext()){

                    dataVal = "" + iterator.next();
                    if(dataVal.trim().equals(""))
                    {
                        continue;
                    }
                    File fileIn = new File(dataVal);
                    Uri u = Uri.fromFile(fileIn);
                    sendDoc.add(u);
                }

                Toast.makeText(getApplicationContext(), "Attachments: " + sendDoc.size(), Toast.LENGTH_SHORT).show();
                final Intent ei = new Intent(Intent.ACTION_SEND_MULTIPLE);
                ei.setType("plain/text");
                ei.putExtra(Intent.EXTRA_EMAIL, new String[] {"" + visitor.email});
                ei.putExtra(Intent.EXTRA_SUBJECT, "Acknowledgement");
                ei.putParcelableArrayListExtra(Intent.EXTRA_STREAM, sendDoc);
                startActivityForResult(Intent.createChooser(ei, "Sending multiple attachment"), 12345);

            }
        });

        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Attach documents.
                startActivity(new Intent(VisitorActivity.this, AttachmentActivity.class));
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

    @Override
    protected void onDestroy() {

        Constants.attachDocs = null;
        super.onDestroy();
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

}
