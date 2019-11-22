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

        mAdapter = MarkerAdapter(this, markersList)
        main_activity_container.adapter = mAdapter

        fab.setOnClickListener { view ->
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("markers", markersList)
            startActivityForResult(intent, RESULT_MAPS)
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
                val res = data.getSerializableExtra("checkedMarkers") as ArrayList<RowData>
                res.forEach {
                    mAdapter.insertToRecyclerView(it)
                }

            }
        }
    }

}
