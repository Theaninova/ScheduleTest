package com.wieland.www.scheduletest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ActivityRowNew extends BaseAdapter {

    private ArrayList<String> data;
    private Context context;

    public ActivityRowNew(Context context, ArrayList<String> data) {
        super();
        this.data = data;
        this.context = context;
    }

    public ActivityRowNew() {}


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.layout_row_new, parent, false);

        TextView text1 = (TextView) rowView.findViewById(R.id.textView4);
        TextView text2 = (TextView) rowView.findViewById(R.id.textView5);

        String x = data.get(position);

        String a = x.substring(0, 2);
        String b = x.substring(3);


        text1.setText(a);
        text2.setText(b);

        return rowView;
    }

}