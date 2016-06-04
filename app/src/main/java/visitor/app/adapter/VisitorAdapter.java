package visitor.app.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import visitor.app.model.Visitor;
import visitor.app.visitorapp.R;

public class VisitorAdapter extends ArrayAdapter<Visitor> {

    //Data members
    Activity activity;
    Context context;
    LayoutInflater inflater;
    ArrayList<Visitor> list;

    public VisitorAdapter(Activity activity, Context context, ArrayList<Visitor> list) {
        super(activity, R.layout.item_visitor_list, list);

        //Initializing all the data members.
        this.activity = activity;
        this.context = context;
        this.inflater = activity.getLayoutInflater();
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.item_visitor_list, null);

        TextView txtSno = (TextView)view.findViewById(R.id.textSno);
        TextView txtName = (TextView)view.findViewById(R.id.textName);
        TextView txtProdInt = (TextView)view.findViewById(R.id.textProdInt);
        TextView txtCategory = (TextView)view.findViewById(R.id.textCategory);
        TextView txtDate = (TextView)view.findViewById(R.id.txtDate);

        txtSno.setText("" + (position + 1));
        txtName.setText("" + list.get(position).name);
        txtProdInt.setText("" + list.get(position).prodint);
        txtCategory.setText("" + list.get(position).category);
        txtDate.setText("" + list.get(position).date);


        return view;
    }
}
