package com.norihirosunada.checkpoint

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.recycler_item_footer_view.view.*

class RecyclerItemFooterView(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attributeSet, defStyleAttr){
    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.recycler_item_footer_view, this)
        setOnClickListener {
            // クリック処理
        }
    }

    fun update(state: RecyclerState){
//        recyclerItemFooterText.text = state.text
    }
}
