package com.cz.widgets.sample.zoomlayout.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cz.widgets.sample.R;
import com.cz.widgets.zoomlayout.table.TableZoomLayout;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SimpleDatabaseAdapter extends TableZoomLayout.Adapter {
    private static final int HEADER_TYPE=0;
    private static final int CELL_ITEM=1;
    private final LayoutInflater layoutInflater;
    private final String[] columnNames;
    private final Cursor cursor;

    public SimpleDatabaseAdapter(Context context, Cursor cursor) {
        this.cursor=cursor;
        this.columnNames = cursor.getColumnNames();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getRowCount() {
        return 1+cursor.getCount();
    }

    @Override
    public int getColumnCount() {
        return cursor.getColumnCount();
    }

    @Override
    public float getColumnWeight(int column) {
        if(1==column){
            return 1;
        } else {
            return super.getColumnWeight(column);
        }
//        return super.getColumnWeight(column);
    }

    @Override
    public int getViewType(int row, int column) {
        return 0==row ? HEADER_TYPE : CELL_ITEM;
    }
    public String getHeaderItem(int column){
        return columnNames[column];
    }
    public String getItem(int row, int column){
        String item=null;
        if(cursor.moveToPosition(row)){
            item = cursor.getString(column);
        }
        return item;
    }

    @Override
    public View getView(Context context,ViewGroup parent, int viewType) {
        if(viewType==HEADER_TYPE){
            return layoutInflater.inflate(R.layout.zoom_database_header_item,parent,false);
        } else {
            return layoutInflater.inflate(R.layout.zoom_database_item,parent,false);
        }
    }

    @Override
    public void onBindView(View view, final int row, final int column) {
        int viewType = getViewType(row, column);
        if(viewType==HEADER_TYPE){
            final String header = getHeaderItem(column);
            final TextView textView=view.findViewById(R.id.textView);
            textView.setText(header);
        } else {
            final String item = getItem(row-1, column);
            final TextView textView=view.findViewById(R.id.textView);
            textView.setText(item);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.invalidate();
                    Toast.makeText(v.getContext(), "Press Row:"+row+" Column:"+column, Toast.LENGTH_SHORT).show();
                }
            });
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(v.getContext(), "LongPress Row:"+row+" Column:"+column, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
}
