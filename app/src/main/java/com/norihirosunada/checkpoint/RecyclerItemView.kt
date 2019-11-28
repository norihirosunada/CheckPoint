package com.norihirosunada.checkpoint

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.recycler_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class RecyclerItemView constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr){

    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.recycler_item_view, this)
        setOnClickListener {
            // クリック処理
        }
    }

    fun update(state: RecyclerState){
        recyclerItemTitle.text = state.text
        val mCheckPoint = state.checkPoint
        if (mCheckPoint != null){
            recyclerItemTitle.text = mCheckPoint.title
            recyclerItemDetail.text = mCheckPoint.lat.toString() + mCheckPoint.lng.toString()
        }
        val mDate = state.date
        if (mDate != null){
            recyclerItemDate.text = SimpleDateFormat("MM月dd日 HH:mm").format(mDate).toString()
        }
    }
}