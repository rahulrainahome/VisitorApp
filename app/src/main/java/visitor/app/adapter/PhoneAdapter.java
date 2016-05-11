package visitor.app.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import visitor.app.visitorapp.R;

public class PhoneAdapter extends ArrayAdapter<String> {

    Activity activity;
    Context context;
    LayoutInflater inflater;
    View.OnClickListener event;
    ArrayList<String> holder = null;

    public PhoneAdapter(Activity activity, Context context, View.OnClickListener event, ArrayList<String> holder) {

        super(activity, R.layout.list_add_phone, holder);

        this.activity = activity;
        this.context = context;
        this.inflater = activity.getLayoutInflater();
        this.event = event;
        this.holder = holder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.list_add_phone, null);

        EditText e = (EditText)view.findViewById(R.id.editText);
        ImageView img = (ImageView)view.findViewById(R.id.imageView);

        e.setTag("" + position);
        img.setTag("" + position);

        img.setOnClickListener(event);

        return view;
    }
}
