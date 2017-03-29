package com.wieland.www.scheduletest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Wieland on 28.03.2017.
 */

public class Layout_Row_Row extends RecyclerView.Adapter<Layout_Row_Row.Layout_Holder> {

    private List<String> listData;
    private LayoutInflater inflater;

    public Layout_Row_Row() {
    }

    @Override
    public Layout_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.inflater = LayoutInflater.from(parent.getContext());
        View view = this.inflater.inflate(R.layout.layout_row_row, parent, false);
        return new Layout_Holder(view);
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
    }

    @Override
    public void onBindViewHolder(Layout_Holder holder, int position) {
        String item = listData.get(position);
        holder.lesson.setText(item);
        holder.multi.setText(item);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class Layout_Holder extends RecyclerView.ViewHolder {

        private TextView lesson;
        private TextView multi;

        public Layout_Holder(View itemView) {
            super(itemView);

            lesson = (TextView) itemView.findViewById(R.id.textLesson);
            multi = (TextView) itemView.findViewById(R.id.textMulti);
        }
    }
}