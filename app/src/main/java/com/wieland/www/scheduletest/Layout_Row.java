package com.wieland.www.scheduletest;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wieland on 28.03.2017.
 */

public class Layout_Row extends RecyclerView.Adapter<Layout_Row.Layout_Holder> {

    private List<String> listData;
    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    private Layout_Row_Row rowRow;

    public Layout_Row(List<String> listData, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    @Override
    public Layout_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_row, parent, false);
        return new Layout_Holder(view);
    }

    @Override
    public void onBindViewHolder(Layout_Holder holder, int position) {
        String item = listData.get(position);
        holder.title.setText(item);
        ArrayList<String> rowrowList = new ArrayList<>();
        rowrowList.add(listData.get(position));
        rowrowList.add(listData.get(position));
        rowrowList.add(listData.get(position));
        rowrowList.add(listData.get(position));
        rowrowList.add(listData.get(position));
        rowrowList.add(listData.get(position));
        rowrowList.add(listData.get(position));
        rowrowList.add(listData.get(position));

        holder.adapter.setListData(rowrowList);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class Layout_Holder extends RecyclerView.ViewHolder {

        Layout_Row_Row adapter;
        private TextView title;
        private RecyclerView recyclerView;
        //private View conainer;

        public Layout_Holder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textTitleRow);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.innerList);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            adapter = new Layout_Row_Row();
            recyclerView.setAdapter(adapter);
            //conainer = itemView.findViewById(R.id.thListOfDoom);
        }
    }
}
