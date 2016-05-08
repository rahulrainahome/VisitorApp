package visitor.app.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class PhoneAdapter extends ArrayAdapter<String> {

    Activity activity;
    Context context;
    public PhoneAdapter(Activity activity, Context context, int resource) {

        super(context, resource);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        return view;
    }
}
