package com.wieland.www.scheduletest;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
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
    private ArrayList<ArrayList<android.text.Spanned>> list;
    private Context context;

    public Layout_Row(List<String> listData, ArrayList<ArrayList<android.text.Spanned>> list, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
        this.list = list;
        this.context = context;
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
        ArrayList<android.text.Spanned> rowrowList = this.list.get(position);

        //ActivityRowNew adapter = new ActivityRowNew(holder.itemView.getContext(), rowrowList);
        
        ArrayAdapter<android.text.Spanned> adapter = new ArrayAdapter<android.text.Spanned>(holder.itemView.getContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                rowrowList);

        holder.recyclerView.setEnabled(false);

        holder.title.setText(item);
        holder.recyclerView.setAdapter(adapter);
        if(rowrowList.size() == 1) {
            holder.subtitle.setText(rowrowList.size() + " Eintrag");
        } else {
            holder.subtitle.setText(rowrowList.size() + " Einträge");
        }

        setListViewHeightBasedOnItems(holder.recyclerView);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class Layout_Holder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView subtitle;
        private ListView recyclerView;

        public Layout_Holder(View itemView) {
            super(itemView);

            subtitle = (TextView) itemView.findViewById(R.id.textSubtitleRow);
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
