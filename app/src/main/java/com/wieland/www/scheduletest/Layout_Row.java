package com.wieland.www.scheduletest;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.nodes.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wieland on 28.03.2017.
 */

public class Layout_Row extends RecyclerView.Adapter<Layout_Row.Layout_Holder> {

    private List<String> listData;
    private LayoutInflater inflater;
    private ArrayList<ArrayList<String>> list;

    public Layout_Row(List<String> listData, ArrayList<ArrayList<String>> list, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
        this.list = list;
    }

    @Override
    public Layout_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_row, parent, false);
        return new Layout_Holder(view);
    }

    @Override
    public void onBindViewHolder(Layout_Holder holder, int position) {
        String item = listData.get(position);
        //ScheduleHandler myNewHandler = new ScheduleHandler(this.doc);
        ArrayList<String> rowrowList = this.list.get(position);

        
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(holder.itemView.getContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                rowrowList);

        holder.recyclerView.setEnabled(false);

        holder.title.setText(item);
        holder.recyclerView.setAdapter(adapter);

        setListViewHeightBasedOnItems(holder.recyclerView);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class Layout_Holder extends RecyclerView.ViewHolder {

        private TextView title;
        private ListView recyclerView;

        public Layout_Holder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textTitleRow);
            recyclerView = (ListView) itemView.findViewById(R.id.innerList);
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}
