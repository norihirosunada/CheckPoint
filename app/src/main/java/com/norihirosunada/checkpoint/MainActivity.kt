package com.norihirosunada.checkpoint

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val RESULT_MAPS = 1000

class MainActivity : AppCompatActivity() {

//    var markerList = mapOf(<RowData>)
    val places = mapOf(
        "OsakaOffice" to LatLng(34.697296, 135.492451),
        "OsakaStation" to LatLng(34.702460, 135.495926),
        "MyHouse" to LatLng(35.109348, 137.116275)
    )

    val markers = mapOf<String, RowData>(
        "OsakaOffice" to RowData("OsakaOffice", 34.697296, 135.492451, false),
        "OsakaStation" to RowData("OsakaStation", 34.702460, 135.495926, false),
        "MyHouse" to RowData("MyHouse", 35.109348, 137.116275, false)
    )

    val markersList = arrayListOf(
        RowData("OsakaOffice", 34.697296, 135.492451, false),
        RowData("OsakaStation", 34.702460, 135.495926, false),
        RowData("MyHouse", 35.109348, 137.116275, false)
    )

    // 名古屋イルミネーションを巡るウォークラリー
    val ilumiList = listOf(
        CheckPoint("ノリタケの森", 35.179875, 136.881588),
        CheckPoint("大名古屋ビルヂング", 35.171995, 136.884629),
        CheckPoint("名古屋広小路通", 35.167871, 136.886403),
        CheckPoint("名古屋駅前地区周辺", 35.169876, 136.884619),
        CheckPoint("ジェイアール名古屋タカシマヤ", 35.171160, 136.882610),
        CheckPoint("グローバルゲート", 35.162285, 136.883450),
        CheckPoint("名古屋マリオットアソシアホテル", 35.170525, 136.883061),
        CheckPoint("笹島交差点", 35.167573, 136.885562),
        CheckPoint("KITTE名古屋", 35.173103, 136.882393),
        CheckPoint("MIDLAND SQUARE", 35.170033, 136.885241),
        CheckPoint("名古屋ルーセントタワー", 35.174935, 136.881131)
    )

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

        val states = arrayListOf<RecyclerState>()
        // ヘッダ追加
        val headerState = RecyclerState(RecyclerType.HEADER, "へっだ")
        states.add(headerState)

        var secCounter = 0
        for(i in 1..10){

            // 2 件目 と 3 件目　の上にセクションを追加
            if(i == 2 || i == 3){
                secCounter++
                val sectionState = RecyclerState(RecyclerType.SECTION, "セクション（区切り） No. $secCounter")
                states.add(sectionState)
            }

            val state = RecyclerState(RecyclerType.BODY, "$i 件目")
            states.add(state)
        }

        // フッタ追加
        val footerState = RecyclerState(RecyclerType.FOOTER, "ふった")
        states.add(footerState)

        mAdapter = MarkerAdapter(this, states)
        main_activity_container.adapter = mAdapter

        fab.setOnClickListener { view ->
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("markers", markersList)
            startActivityForResult(intent, RESULT_MAPS)
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
        Log.d("result", "onActivityResult()")
        //MapsActivityからチェックされたマークのArrayList<RowData>を受け取る
        if (requestCode == RESULT_MAPS && resultCode == Activity.RESULT_OK && data != null){
            Log.d("result", "Result Maps ")
            val data = data
            if (data.hasExtra("checkedMarkers")) {
                Log.d("result", "has extra")
                val res = data.getSerializableExtra("checkedMarkers") as ArrayList<RecyclerState>
                res.forEach {
                    mAdapter.insertToRecyclerView(it)
                }

            }
        }
    }

}
