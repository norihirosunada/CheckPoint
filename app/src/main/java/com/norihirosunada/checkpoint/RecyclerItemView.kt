package com.norihirosunada.checkpoint

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.recycler_item_view.view.*
import java.io.IOException
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
//        recyclerItemTitle.text = state.text
        val mCheckPoint = state.checkPoint
        if (mCheckPoint != null){
            recyclerItemTitle.text = mCheckPoint.title
            recyclerItemDetail.text = mCheckPoint.lat.toString() +" "+ mCheckPoint.lng.toString()

            //  緯度経度より住所表示
//            try {
//                val geocoder = Geocoder(this.context)
//                val addresses = geocoder.getFromLocation(mCheckPoint.lat, mCheckPoint.lng, 1)
//                if (null != addresses && !addresses.isEmpty()) {
//                    val address = addresses[0]
//                    var addressText = ""
//                    for (i in 0 until address.maxAddressLineIndex) {
//                        addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
//                    }
//                    recyclerItemDetail.text = addressText
//                }
//            } catch (e: IOException) {
//                Log.e("MapsActivity", e.localizedMessage)
//            }
        }
        val mDate = state.date
        if (mDate != null){
            recyclerItemDate.text = SimpleDateFormat("MM月dd日 HH:mm").format(mDate).toString()
        }
    }
}