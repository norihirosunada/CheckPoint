package com.norihirosunada.checkpoint

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

const val RESULT_MAPS = 1000

class MainActivity : AppCompatActivity() {

//    var markerList = mapOf(<RowData>)
//    val places = mapOf(
//        "OsakaOffice" to LatLng(34.697296, 135.492451),
//        "OsakaStation" to LatLng(34.702460, 135.495926),
//        "MyHouse" to LatLng(35.109348, 137.116275)
//    )

//    val markers = mapOf<String, RowData>(
//        "OsakaOffice" to RowData("OsakaOffice", 34.697296, 135.492451, false),
//        "OsakaStation" to RowData("OsakaStation", 34.702460, 135.495926, false),
//        "MyHouse" to RowData("MyHouse", 35.109348, 137.116275, false)
//    )

//    val markersList = arrayListOf(
//        RowData("OsakaOffice", 34.697296, 135.492451, false),
//        RowData("OsakaStation", 34.702460, 135.495926, false),
//        RowData("MyHouse", 35.109348, 137.116275, false)
//    )

    // チェックイン履歴用
    //　チェックイン毎に追加
    var historyList = arrayListOf<CheckPointRecord>()
    class CheckPointRecord(val checkPoint: CheckPoint, val date: Date)

    private lateinit var mAdapter: MarkerAdapter

//    companion object {
//        const val KEY_PREFERENCES: String = "preferences"
//        const val KEY_SAVE_TEXT: String = "save_text"
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        val sharedPreferences: SharedPreferences = getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = sharedPreferences.edit()
//        savedTextView.text = sharedPreferences.getString(KEY_SAVE_TEXT, "No Data")
//        editor.putString(KEY_SAVE_TEXT, saveText)
//        editor.apply()


//        markerList.add(RowData("OsakaOffice", 34.697296, 135.492451, false))
//        markerList.add(RowData("OsakaStation", 34.702460, 135.495926, false))
//        markerList.add(RowData("MyHouse", 35.109348, 137.116275, false))

        main_activity_container.setHasFixedSize(true)
        main_activity_container.layoutManager = LinearLayoutManager(this)

        //チェックイン履歴読み込み
        historyList = loadArrayList("history")

        val states = arrayListOf<RecyclerState>()

        // ヘッダ追加
        val headerState = RecyclerState(RecyclerType.HEADER, "0", null, null)
        states.add(headerState)

        // RecycleView動作確認用
        var secCounter = 0
        for(i in 1..5){
            // 2 件目 と 3 件目　の上にセクションを追加
            if(i == 2 || i == 3){
                secCounter++
                val sectionState = RecyclerState(RecyclerType.SECTION, "セクション（区切り） No. $secCounter", null, null)
                states.add(sectionState)
            }
            val state = RecyclerState(RecyclerType.BODY, "$i 件目", CheckPoint("タイトル", 123.456, 789.123), Date())
            states.add(state)
        }
        // 本番用
//        for (i in 0 until historyList.size){
//            states.add(RecyclerState(RecyclerType.BODY, null, historyList[i].checkPoint, historyList[i].date))
//        }

        // フッタ追加
        val footerState = RecyclerState(RecyclerType.FOOTER, null, null, null)
        states.add(footerState)

        mAdapter = MarkerAdapter(this, states)
        main_activity_container.adapter = mAdapter

        fab.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
//            intent.putExtra("markers", markersList)
            intent.flags = FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
            startActivityForResult(intent, RESULT_MAPS)
//            mAdapter.insertToRecyclerView(RecyclerState(RecyclerType.BODY, null, CheckPoint("title", 12.34, 56.78), Date()))
//            mAdapter.updateToRecyclerView(0, RecyclerState(RecyclerType.HEADER, "1", null, null))
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item1 -> {
//                    setFragment()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.item2 -> {
//                    setFragment()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
//                    setFragment()
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //MapsActivityからチェックされたマークのArrayListを受け取る
        when(requestCode){
            RESULT_MAPS ->{
                if (resultCode == Activity.RESULT_OK){
                    Log.d("result", "RESULT_OK")
                    if (data != null){
                        Log.d("result", "data not null")
                        if (data.hasExtra("checkedMarkers")) {
                            val res = data.getSerializableExtra("checkedMarkers") as ArrayList<CheckPoint>
                            res.forEach {
                                val date = Date()
                                mAdapter.insertToRecyclerView(RecyclerState(RecyclerType.BODY, null, it, date))
                                historyList.add(0, CheckPointRecord(it, date))
                            }
                            // チェックイン履歴をSharedPreferenceに保存
                            saveArrayList("history", historyList)
                        }
                    }
                }else if (resultCode == Activity.RESULT_CANCELED){
                    Log.d("result", "RESULT_CANCELED")
                }
            }
        }

    }

    fun saveArrayList(key: String, arrayList: ArrayList<CheckPointRecord>){
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()
        val jsonArray = JSONArray(arrayList)
        editor.putString(key, jsonArray.toString())
        editor.apply()
    }

    fun loadArrayList(key: String): ArrayList<CheckPointRecord>{
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val jsonArray = JSONArray(dataStore.getString(key, "[]"))
        val arrayList: ArrayList<CheckPointRecord> = ArrayList()
        for (i in 0 until jsonArray.length()){
            arrayList.add(jsonArray.get(i) as CheckPointRecord)
        }
        return arrayList
    }

}
