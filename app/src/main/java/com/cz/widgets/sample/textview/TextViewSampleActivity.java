package com.cz.widgets.sample.textview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cz.android.sample.api.RefRegister;
import com.cz.android.sample.api.Register;
import com.cz.widgets.sample.R;

@RefRegister(title=R.string.text_span_title,desc=R.string.text_span_desc,category = R.string.text_span)
public class TextViewSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_sample);

        SimpleTextView2 textView=findViewById(R.id.textView);
        textView.setText("12345ABCDEFg");
    }
}
