package com.cz.laboratory.app.android.view.seat

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.cz.android.sample.api.RefRegister
import com.cz.laboratory.app.R
import kotlinx.android.synthetic.main.android_view_seat_fragment_table.*

/**
 * @author Created by cz
 * @date 2020-02-01 19:08
 * @email bingo110@126.com
 */
@Keep
@SampleSourceCode
@RefRegister(title= R.string.android_view_seat_table1_title,desc = R.string.android_view_seat_table1_desc,category = R.string.android_view)
class AndroidViewSeatTableFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.android_view_seat_fragment_table, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val localContext=context
        if(null!=localContext){
            setHasOptionsMenu(true)
            seatTable.setAdapter(SampleSeatTableAdapter(localContext, seatTable, 400, 600))
        }
    }

    /**
     * Show a dialog that you could choose how many seats you want to test.
     */
    private fun showSelectDialog(context: Context) {
        AlertDialog.Builder(context).
            setTitle(R.string.android_view_seat_table_number_hint).
            setCancelable(false).
            setItems(resources.getStringArray(R.array.android_view_seat_table_array)) { _, which ->
                when(which){
                    0->seatTable.setAdapter(SampleSeatTableAdapter(context, seatTable, 40000, 60000))
                    1->seatTable.setAdapter(SampleSeatTableAdapter(context, seatTable, 4000, 6000))
                    2->seatTable.setAdapter(SampleSeatTableAdapter(context, seatTable, 400, 600))
                    3->seatTable.setAdapter(SampleSeatTableAdapter(context, seatTable, 12, 24))
                }
                //重置后滚回中间
                seatTable.scrollToCenter()
            }.setPositiveButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss()}.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.android_view_seat_table_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_item1){
            val localContext=context
            if(null!=localContext){
                showSelectDialog(localContext)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
