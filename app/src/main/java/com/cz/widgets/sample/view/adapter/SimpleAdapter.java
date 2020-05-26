package com.cz.widgets.sample.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author Created by cz
 * @date 2020-05-22 19:51
 * @email bingo110@126.com
 */
public class SimpleAdapter extends BaseAdapter<RecyclerView.ViewHolder,String> {

    public SimpleAdapter(@NonNull List<String> itemList) {
        super(itemList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new RecyclerView.ViewHolder(listItem) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        TextView textView=holder.itemView.findViewById(android.R.id.text1);
        String text = getItem(position);
        textView.setText(text);
    }
}
