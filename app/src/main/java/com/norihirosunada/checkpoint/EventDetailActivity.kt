package com.norihirosunada.checkpoint

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_event_detail.*

const val RESULT_EVENTMAP = 900

class EventDetailActivity : AppCompatActivity() {

    lateinit var checkpointsList: ArrayList<RecyclerState>
    private lateinit var mAdapter: MarkerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        mAdapter = MarkerAdapter(this, checkpointsList)
        checkpoints_container.adapter = mAdapter

        button.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, RESULT_EVENTMAP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            RESULT_EVENTMAP ->{
                if (resultCode == Activity.RESULT_OK){
                    if (data != null){
//                        mAdapter.insertToRecyclerView()
                    }
                }
            }
        }

    }

}
