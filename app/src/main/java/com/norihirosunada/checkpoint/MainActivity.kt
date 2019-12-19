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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.collections.ArrayList

const val RESULT_MAPS = 1000

class MainActivity : AppCompatActivity() {

    // チェックイン履歴用
    //　チェックイン毎に追加
    var historyList = arrayListOf<CheckPointRecord>()
    class CheckPointRecord(val checkPoint: CheckPoint, val date: Date)

    private lateinit var mAdapter: MarkerAdapter

    // 参加しているラリーイベントのリスト
    var rallyArrayList = arrayListOf<String>("I0UL0lvLUFwub9uKtKqc")

    var spinnerItems = arrayListOf<String>("01")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val tabFragments = arrayListOf(
            TabFragment::class.java,
            TabFragment::class.java,
            TabFragment::class.java
        )

//        initTablayout()
        initRecyclerView()

        // Spinner設定
        var arrayAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                val item = spinnerParent.selectedItem as String
                //textView.text = item
            }
            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }

        // ダイアログ表示 ラリーイベントID入力
        addRallyButton.setOnClickListener{
            rallyArrayList.add("oUVbtjhghPAuRTu2gfRf")
            spinnerItems.add("02")


        }

        fab.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            val array: ArrayList<String> = arrayListOf()
            historyList.forEachIndexed { index, it ->
                array.add(it.checkPoint.title)
            }
            val rallyArray: Array<String> = rallyArrayList.toTypedArray()
            intent.putExtra("rallyArray", rallyArray)
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
        when (item.itemId) {
            R.id.action_settings -> {

                return true
            }
            else -> return super.onOptionsItemSelected(item)
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
        for (i in 0 until listSize){
            arrayList.add(gson.fromJson(dataStore.getString(key+i, ""), CheckPointRecord::class.java))
        }
//        if (jsonArray.length() >= 1) {
//            for (i in 0 until jsonArray.length()) {
//                arrayList.add(jsonArray.get(i) as CheckPointRecord)
//            }
//        }
        return arrayList
    }

    fun initTablayout(){
//        pager.adapter = CustomPagerAdapter(supportFragmentManager)
//        tab_layout.setupWithViewPager(pager)
    }
    fun initRecyclerView(){
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
    }
}
