## ZoomTable

The basic implementation of the zoom layout.

* ![image1](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_table.gif?raw=true)

* ![image2](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_db.gif?raw=true)


### Style

```
<attr name="zoom_minimum" format="float"/>
<attr name="zoom_maximum" format="float"/>
<attr name="zoom_enabled" format="boolean"/>
```

### Usage

```
By implementing from the TableZoomLayout.Adapter you give me all the layout and data.
Just like you use the ListView or RecyclerView.

Here is an example:
public class SimpleTableLayoutAdapter extends TableZoomLayout.Adapter {
    private static final int HEADER_TYPE=0;
    private static final int CELL_ITEM=1;
    private final SparseArray<String> imageArray=new SparseArray<>();
    private final LayoutInflater layoutInflater;
    private final List<List<String>> items;

    public SimpleTableLayoutAdapter(Context context, List<List<String>> items) {
        this.layoutInflater = LayoutInflater.from(context);
        this.items=items;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return items.get(0).size();
    }

    @Override
    public float getColumnWidth(int row, int column) {
        return 300;
    }

    @Override
    public float getColumnWeight(int column) {
//        return super.getColumnWeight(column);
    }

    @Override
    public int getViewType(int row, int column) {
        return 0==row ? HEADER_TYPE : CELL_ITEM;
    }

    public String getItem(int row, int column){
        return items.get(row).get(column);
    }

    @Override
    public View getView(Context context,ViewGroup parent, int viewType) {
        if(viewType==HEADER_TYPE){
            return layoutInflater.inflate(R.layout.zoom_simple_header_text_item,parent,false);
        } else {
            return layoutInflater.inflate(R.layout.zoom_simple_table_item,parent,false);
        }
    }

    @Override
    public void onBindView(View view, final int row, final int column) {
        //Initialize all the data and binding to the View.
    }
}

// Initialize with the adapter.
val tableAdapter = SimpleTableLayoutAdapter(this, list)
zoomLayout.addItemDecoration(TableDecoration(this))
zoomLayout.setAdapter(tableAdapter)
```

### References.

* [The zoom strategy](./zoom.md).
* [The recycle strategy](./zoom_recycler.md).
* [The preview layout](./preview.md).

### Problems

* About the recycler strategy
To be honest This is the second version of the ZoomLayout. But until this view, I didn't find a better strategy to recycle the child views.
So Here I just use the recycler strategy from LinearLayoutManager from RecyclerView library, which is recycler vertically or horizontally.
There are some performance consumptions here.

This means the columns of the table out of the screen I didn't recycle those child views.
Sorry about that. But after two days later I finger it out. I just don't have to time to change the recycler strategy.
If you curious about this recycle strategy for the table. Take a look at:[recycler](./zoom_recycler.md)
