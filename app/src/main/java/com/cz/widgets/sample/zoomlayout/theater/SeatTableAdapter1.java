package com.cz.widgets.sample.zoomlayout.theater;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cz.widgets.sample.R;
import com.cz.widgets.zoomlayout.ZoomLayout;
import com.cz.widgets.zoomlayout.theater.TableAdapter;

/**
 * @author Created by cz
 * @date 2020-05-14 22:34
 * @email bingo110@126.com
 */
public class SeatTableAdapter1 extends TableAdapter {
    private final SparseBooleanArray selectedArray=new SparseBooleanArray();
    private final LayoutInflater layoutInflater;
    private final int rowCount;
    private final int columnCount;

    public SeatTableAdapter1(Context context,int rowCount,int columnCount){
        this.layoutInflater =LayoutInflater.from(context);
        this.rowCount=rowCount;
        this.columnCount=columnCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public int getHorizontalSpacing(int column) {
        return 10;
    }

    @Override
    public int getVerticalSpacing(int row) {
        return 10;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public boolean isDisable(int row, int column) {
        return row==10||(row==2&&column==2);
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        return layoutInflater.inflate(R.layout.zoom_seat_table_item,parent,false);
    }

    @Override
    public void onBindView(final ZoomLayout parent, View view, final int row, final int column) {
        int columnCount = getColumnCount();
        final int i = row * columnCount + column;
        view.setSelected(selectedArray.get(i));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = v.isSelected();
                v.setSelected(!selected);
                selectedArray.put(i,!selected);
                Toast.makeText(v.getContext(),"Row:"+row+" Column:"+column, Toast.LENGTH_SHORT).show();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(),"LongClick:"+row+" Column:"+column,Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
