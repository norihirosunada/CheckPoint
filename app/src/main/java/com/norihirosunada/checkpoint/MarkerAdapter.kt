package com.norihirosunada.checkpoint

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MarkerAdapter(private val context: Context, private val markerList: MutableList<RecyclerState>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // RecyclerViewの一要素となるXML要素の型を引数に指定する
    // この場合はdiary_list_item.xmlのTextView
    // これを使う時は　MutableList<RowData): RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>()とすること
    class MarkerViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleTextView: TextView = view.findViewById(R.id.row_title)
        val detailTextView: TextView = view.findViewById(R.id.row_title)
        val checkedView: TextView = view.findViewById(R.id.row_checked)
    }

    class RecyclerItemViewHolder(private val view: RecyclerItemView) : RecyclerView.ViewHolder(view) {
        fun update(state: RecyclerState){
            view.update(state)
        }
    }

    class RecyclerItemSectionViewHolder(private val view: RecyclerItemSectionView) : RecyclerView.ViewHolder(view) {
        fun update(state: RecyclerState){
            view.update(state)
        }
    }

    class RecyclerItemFooterViewHolder(private val view: RecyclerItemFooterView) : RecyclerView.ViewHolder(view) {
        fun update(state: RecyclerState){
//            view.update(state)
        }
    }

    class RecyclerItemHeaderViewHolder(private val view: RecyclerItemHeaderView) : RecyclerView.ViewHolder(view) {
        fun update(state: RecyclerState){
            view.update(state)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
//        MarkerViewHolder =MarkerViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
        return when(RecyclerType.fromInt(viewType)){
            RecyclerType.HEADER -> {
                val view = RecyclerItemHeaderView(context)
                return RecyclerItemHeaderViewHolder(view)
            }
            RecyclerType.FOOTER -> {
                val view = RecyclerItemFooterView(context)
                return RecyclerItemFooterViewHolder(view)
            }
            RecyclerType.SECTION -> {
                val view = RecyclerItemSectionView(context)
                return RecyclerItemSectionViewHolder(view)
            }
            RecyclerType.BODY -> {
                val view = RecyclerItemView(context)
                return RecyclerItemViewHolder(view)
            }
        }
    }


    // 第１引数のViewHolderはこのファイルの上のほうで作成した`class ViewHolder`です。
    // MarkerViewHolderを使う場合はholder: MarkerViewHolderとすること
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.titleTextView.text = markerList[position].title
//        holder.detailTextView.text = "LatLng: " + markerList[position].lat + "/" + markerList[position].lng
//        holder.checkedView.text = if (markerList[position].checkedFlag) "Checked" else "not Checked"

        when(holder){
            is RecyclerItemHeaderViewHolder ->{
                holder.update(markerList[position])
            }
            is RecyclerItemFooterViewHolder ->{
//                holder.update(markerList[position])
            }
            is RecyclerItemSectionViewHolder ->{
                holder.update(markerList[position])
            }
            is RecyclerItemViewHolder ->{
                holder.update(markerList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return markerList[position].type.int
    }

    override fun getItemCount() = markerList.size

    fun insertToRecyclerView(item: RecyclerState){
        if (markerList != null){
            if (markerList.indexOf(item) == -1){
                markerList.add(1,item)
                notifyItemInserted(1)
            }
        }
    }

    fun updateToRecyclerView(item: RecyclerState){
        if (markerList != null){
            val index = markerList.indexOf(item)
            if (index != -1){
                notifyItemChanged(index)
            }
        }
    }

    fun deleteToRecyclerView(item: RecyclerState){
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
