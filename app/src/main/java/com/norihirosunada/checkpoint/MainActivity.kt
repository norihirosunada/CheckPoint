package com.norihirosunada.checkpoint

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    var markerList = ArrayList<RowData>()

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

        markerList.add(RowData("OsakaOffice", 34.697296, 135.492451, false))
        markerList.add(RowData("OsakaStation", 34.702460, 135.495926, false))

        main_activity_container.setHasFixedSize(true)
        main_activity_container.layoutManager = LinearLayoutManager(this)
        main_activity_container.adapter = MarkerAdapter(this, markerList)

        fab.setOnClickListener { view ->
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("markers", markerList)
            startActivity(intent)
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

}
