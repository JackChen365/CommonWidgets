package com.cz.widgets.sample.zoomlayout.theater

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_theater_sample1.*

@SampleSourceCode
@RefRegister(title= R.string.zoom_theater1,desc = R.string.zoom_layout1_desc,category = R.string.zoom_theater)
class TheaterSample1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theater_sample1)

        val tableAdapter1 = SeatTableAdapter1(this, 16, 24)
        seatTable.setAdapter(tableAdapter1)

        previewLayout.attachToHostView(seatTable)
    }
}
