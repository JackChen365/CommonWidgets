package com.cz.widgets.sample.textview.span.table;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cz.widgets.sample.R;
import com.cz.widgets.sample.data.Data;
import com.cz.widgets.textview.span.click.TouchableMovementMethod;
import com.cz.widgets.textview.span.table.TableLayout;
import com.cz.widgets.textview.span.view.TouchableViewSpan;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SimpleTableAdapter extends TableLayout.Adapter {
    private static final int HEADER_TYPE=0;
    private static final int CELL_ITEM=1;
    private final LayoutInflater layoutInflater;
    private final List<List<String>> items;

    public SimpleTableAdapter(Context context, List<List<String>> items) {
        this.layoutInflater = LayoutInflater.from(context);
        this.items=items;
    }

    @Override
    public float getColumnWeight(int column) {
        return 1;
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
    public int getViewType(int row, int column) {
        return 0==row ? HEADER_TYPE : CELL_ITEM;
    }

    public String getItem(int row, int column){
        return items.get(row).get(column);
    }

    @Override
    public View getView(ViewGroup parent, int viewType) {
        if(viewType==HEADER_TYPE){
            return layoutInflater.inflate(R.layout.text_simple_header_text_item,parent,false);
        } else {
            return layoutInflater.inflate(R.layout.text_simple_text_table_item,parent,false);
        }
    }

    @Override
    public void onBindView(View view, int row, int column) {
        int viewType = getViewType(row, column);
        final String item = getItem(row, column);
        if(viewType==HEADER_TYPE){
             final TextView textView=view.findViewById(R.id.textView);
             textView.setText(item);
        } else {
            final TextView textView=view.findViewById(R.id.outerTextView);
            textView.setMovementMethod(TouchableMovementMethod.getInstance());
            View layout = View.inflate(view.getContext(), R.layout.text_simple_table_item, null);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            TouchableViewSpan tableSpan = new TouchableViewSpan(textView, layout);
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(tableSpan, 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
            bindTextView(layout,item);
        }
    }

    private void bindTextView(View view, final String item){
        String image = Data.getImage();
        final ImageView imageView=view.findViewById(R.id.imageView);
        final TextView textView=view.findViewById(R.id.textView);
        Glide.with(view.getContext()).load(image).transition(withCrossFade()).into(imageView);
//            Glide.with(view.getContext()).load(R.mipmap.ic_launcher).into(imageView);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(item);
        final Random random=new Random();
        for(int i=0;i<1;i++){
            float red = random.nextInt(255);
            float green = random.nextInt(255) / 2f;
            float blue = random.nextInt(255) / 2f;
            int color= 0xff000000 | ((int) (red   * 255.0f + 0.5f) << 16) |
                    ((int) (green * 255.0f + 0.5f) <<  8) | (int) (blue  * 255.0f + 0.5f);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            stringBuilder.setSpan(colorSpan, i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(stringBuilder);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), item, Toast.LENGTH_SHORT).show();
            }
        });
//        textView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(v.getContext(), "Long press "+value, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
        textView.setMovementMethod(new TouchableMovementMethod());
    }
}
