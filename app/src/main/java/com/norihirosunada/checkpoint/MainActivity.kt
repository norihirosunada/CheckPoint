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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        markerList.add(RowData("OsakaOffice", 34.697296, 135.492451, false))
//        markerList.add(RowData("OsakaStation", 34.702460, 135.495926, false))
//        markerList.add(RowData("MyHouse", 35.109348, 137.116275, false))

        main_activity_container.setHasFixedSize(true)
        main_activity_container.layoutManager = LinearLayoutManager(this)

        // sharedpreference　データ消去
//        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
//        dataStore.edit().clear().apply()

        //チェックイン履歴読み込み
        historyList = loadArrayList("history")

        val states = arrayListOf<RecyclerState>()

        // ヘッダ追加
        val headerState = RecyclerState(RecyclerType.HEADER, historyList.size)
        states.add(headerState)
        // RecycleView動作確認用
//        var secCounter = 0
//        for(i in 1..5){
//            // 2 件目 と 3 件目　の上にセクションを追加
//            if(i == 2 || i == 3){
//                secCounter++
//                val sectionState = RecyclerState(RecyclerType.SECTION, "セクション（区切り） No. $secCounter")
//                states.add(sectionState)
//            }
//            val state = RecyclerState(RecyclerType.BODY, "$i 件目", CheckPoint("タイトル", 123.456, 789.123), Date())
//            states.add(state)
//        }
        // 本番用
        for (i in 0 until historyList.size){
            states.add(RecyclerState(RecyclerType.BODY, historyList[i].checkPoint, historyList[i].date))
        }
        // フッタ追加
        val footerState = RecyclerState(RecyclerType.FOOTER)
        states.add(footerState)

        mAdapter = MarkerAdapter(this, states)
        main_activity_container.adapter = mAdapter

        fab.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            val array: ArrayList<String> = arrayListOf()
            historyList.forEachIndexed { index, it ->
                array.add(it.checkPoint.title)
            }
            intent.putExtra("checked markers", array)
            intent.flags = FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
            startActivityForResult(intent, RESULT_MAPS)
        }

//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.item1 -> {
////                    setFragment()
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.item2 -> {
////                    setFragment()
//                    return@setOnNavigationItemSelectedListener true
//                }
//                else -> {
////                    setFragment()
//                    return@setOnNavigationItemSelectedListener true
//                }
//            }
//        }
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
                                mAdapter.insertToRecyclerView(RecyclerState(RecyclerType.BODY, it, date))
                                historyList.add(0, CheckPointRecord(it, date))
                            }
                            //ヘッダー更新
                            mAdapter.updateToRecyclerView(0, RecyclerState(RecyclerType.HEADER, historyList.size))
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
        if (arrayList.isNotEmpty()) {
            val dataStore: SharedPreferences =
                getSharedPreferences("DataStore", Context.MODE_PRIVATE)
            val editor = dataStore.edit()
            val gson = Gson()
//        val jsonArray = JSONArray(arrayList)
            arrayList.forEachIndexed { index, checkPointRecord ->
                editor.putString(key + index, gson.toJson(checkPointRecord))
            }
            editor.putInt(key + "size", arrayList.size)
            editor.apply()
        }
    }

    fun loadArrayList(key: String): ArrayList<CheckPointRecord>{
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
//        val jsonArray = JSONArray(dataStore.getString(key, "[]"))
        val gson = Gson()
        val arrayList = ArrayList<CheckPointRecord>()
//        val listType = TypeToken<CheckPointRecord>()
        val listSize = dataStore.getInt(key+"size", 0)
        Log.d("loadarray", listSize.toString())
        for (i in 0..(listSize-1)){
            arrayList.add(gson.fromJson(dataStore.getString(key+i, ""), CheckPointRecord::class.java))
        }
//        if (jsonArray.length() >= 1) {
//            for (i in 0 until jsonArray.length()) {
//                arrayList.add(jsonArray.get(i) as CheckPointRecord)
//            }
//        }
        return arrayList
    }

}
