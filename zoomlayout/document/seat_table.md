## SeatTable

#### Picture

![zoom_threater](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_threater.gif?raw=true)


### Style

```
<attr name="zoom_minimum" format="float"/>
<attr name="zoom_maximum" format="float"/>
<attr name="zoom_enabled" format="boolean"/>
```

### Usage

```
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
        ...
    }
}
```

We ask all the table cells from this Adapter.

```
//Initialize the table with this Adapter.
val tableAdapter1 = SeatTableAdapter1(this, 16, 24)
seatTable.setAdapter(tableAdapter1)
```

Just like we use the ListView or RecyclerView, handle the touch events and changes.

That's the benefit of using ViewGroup instead of drawing all things.

### Preview.

```
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.cz.widgets.zoomlayout.tree.PreViewLayout
        android:id="@+id/previewLayout"
        android:layout_width="200dp"
        android:layout_height="wrap_content"/>

    <com.cz.widgets.zoomlayout.theater.SeatTable
        android:id="@+id/seatTable"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/md_grey_900"/>
</FrameLayout>
```

Attach the Previewable to the PreViewLayout

```
previewLayout.attachToHostView(seatTable)
```

For a TableLayout We actually do not need to measure the Layout at mode:wrap_content.
It is not useful and makes everything complicated. So Here I assume you would use the layout fill the screen or use an exact size.

### References.

* [The zoom strategy](./zoom.md).
* [The recycle strategy](./zoom_recycler.md).
* [The preview layout](./preview.md).

### Note

I do want to try other recycle strategy. A recycle strategy without the TableIndexer but scroll to anywhere calculate dynamically.
But the truth is I just do not have enough time to do it. I have to wrap this up and rewrite a few other widgets.
Next time, I will be ready for a totally performance test and more flexible implementation