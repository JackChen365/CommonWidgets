package com.cz.widgets.sample.zoomlayout.theater.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import com.cz.widgets.sample.zoomlayout.theater.SeatTableAdapter1
import kotlinx.android.synthetic.main.activity_theater_reccyler_test.*

@SampleSourceCode
@RefRegister(title= R.string.zoom_theater_recycler,desc = R.string.zoom_theater_recycler_desc,category = R.string.zoom_theater)
class TheaterRecyclerTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theater_reccyler_test)

        val tableAdapter1 = SeatTableAdapter1(this, 6, 6)
        seatTable.setAdapter(tableAdapter1)
    }
}
