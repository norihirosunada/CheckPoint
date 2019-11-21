package com.norihirosunada.checkpoint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MarkerAdapter(private val context: Context, private val markerList: MutableList<RowData>) : RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>() {
    // RecyclerViewの一要素となるXML要素の型を引数に指定する
    // この場合はdiary_list_item.xmlのTextView
    class MarkerViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleTextView: TextView = view.findViewById(R.id.row_title)
        val detailTextView: TextView = view.findViewById(R.id.row_title)
        val checkedView: TextView = view.findViewById(R.id.row_checked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder =
        MarkerViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))

    // 第１引数のViewHolderはこのファイルの上のほうで作成した`class ViewHolder`です。
    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.titleTextView.text = markerList[position].title
        holder.detailTextView.text = "LatLng: " + markerList[position].lat + "/" + markerList[position].lng
        holder.checkedView.text = if (markerList[position].checkedFlag) "Checked" else "not Checked"
    }

    override fun getItemCount() = markerList.size

    fun insertToRecyclerView(item: RowData){
        if (markerList != null){
            if (markerList.indexOf(item) == -1){
                markerList.add(0,item)
                notifyItemInserted(0)
            }
        }
    }

    fun updateToRecyclerView(item: RowData){
        if (markerList != null){
            val index = markerList.indexOf(item)
            if (index != -1){
                notifyItemChanged(index)
            }
        }
    }

    fun deleteToRecyclerView(item: RowData){
        if (markerList != null){
            val index = markerList.indexOf(item)
            if (index != -1){
                if (markerList.remove(item)){
                    notifyItemRemoved(index)
                }
            }
        }
    }
}