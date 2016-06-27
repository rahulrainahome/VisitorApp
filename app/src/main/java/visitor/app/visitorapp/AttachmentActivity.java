package visitor.app.visitorapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AttachmentActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        listView = (ListView)findViewById(R.id.id_list_view);
        for(int i = 0;i < 20; i++)
        {
            list.add("adapter " + i);
        }
        adapter = new ArrayAdapter<String>(AttachmentActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

    }
}
