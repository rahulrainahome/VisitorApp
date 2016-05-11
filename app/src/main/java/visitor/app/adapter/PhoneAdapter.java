package visitor.app.adapter;


import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.list_add_phone, null);

        EditText e = (EditText)view.findViewById(R.id.editText);
        ImageView img = (ImageView)view.findViewById(R.id.imageView);

        e.setTag("" + position);
        e.setText(holder.get(position));
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                holder.set(position, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if(position != 0) //First number is important.
        {
            img.setTag("" + position);
            img.setOnClickListener(event);
            img.setVisibility(View.VISIBLE);
        }
        else
        {
            img.setVisibility(View.GONE);
        }

        return view;
    }

}
