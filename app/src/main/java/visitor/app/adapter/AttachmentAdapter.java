package visitor.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import visitor.app.utils.Constants;
import visitor.app.visitorapp.R;

/**
 * Created by user on 29/6/16.
 */
public class AttachmentAdapter extends ArrayAdapter<String> {

    public ArrayList<String> list;
    public Activity activity;
    private LayoutInflater inflater;
    public CompoundButton.OnCheckedChangeListener listener;

    public AttachmentAdapter (Activity activity, CompoundButton.OnCheckedChangeListener listener, ArrayList<String> list)
    {
        super(activity, R.layout.item_attach_list, list);

        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.list = list;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        CheckBox checkBox;

        if(view == null)
        {
            view = inflater.inflate(R.layout.item_attach_list, null);
        }

        checkBox = (CheckBox)view.findViewById(R.id.checkBox);
        checkBox.setText("" + list.get(position).toString());
        checkBox.setTag(position);
        checkBox.setOnCheckedChangeListener(listener);

        String str = Constants.attachDocs.get(position).toString();
        if(str.equals(""))
        {
            checkBox.setChecked(false);
        }
        else
        {
            checkBox.setChecked(true);
        }


        return view;
    }
}
