package visitor.app.visitorapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import visitor.app.adapter.AttachmentAdapter;
import visitor.app.utils.Constants;

public class AttachmentActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ListView listView;
    ArrayList<String> list = new ArrayList<String>();
    AttachmentAdapter adapter;
    SharedPreferences s = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);
        listView = (ListView)findViewById(R.id.id_list_view);
        s = getSharedPreferences(Constants.pref_docs, 0);

        //Get all the docs from cache and hold in global variable.
        try
        {
            boolean flag = false;
            if(Constants.attachDocs.size() == 0)
            {
                flag = true;
            }
            list = new ArrayList<String>();
            String cache_docs = s.getString("data", "[]");
            JSONArray jsonArrayDocs = new JSONArray(cache_docs.toString());
            for(int i = 0; i <jsonArrayDocs.length(); i++)
            {
                if(flag) {
                    Constants.attachDocs.add("");
                }
                list.add(jsonArrayDocs.getString(i).toString());

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "EXCEPTION: Cache Docs Fetching and Parsing error.", Toast.LENGTH_SHORT).show();
        }

        adapter = new AttachmentAdapter(this, this, list);
        listView.setAdapter(adapter);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int i = Integer.parseInt(String.valueOf(buttonView.getTag()));
        if(isChecked)
        {
            Constants.attachDocs.set(i, "" + list.get(i));
        }
        else
        {
            Constants.attachDocs.set(i, "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_attachment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id) {
            case R.id.action_done:

                try
                {

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
                    ei.putExtra(Intent.EXTRA_EMAIL, new String[] {"" + Constants.selectedVisitor.email});
                    ei.putExtra(Intent.EXTRA_SUBJECT, "Visitor List");
                    ei.putParcelableArrayListExtra(Intent.EXTRA_STREAM, sendDoc);
                    startActivityForResult(Intent.createChooser(ei, "Sending multiple attachment"), 12345);

                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Unable to send email.", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
