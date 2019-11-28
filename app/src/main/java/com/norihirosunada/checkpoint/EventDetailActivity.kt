package com.norihirosunada.checkpoint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_event_detail.*

class EventDetailActivity : AppCompatActivity() {

    lateinit var checkpointsList: ArrayList<RecyclerState>
    private lateinit var mAdapter: MarkerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        mAdapter = MarkerAdapter(this, checkpointsList)
        checkpoints_container.adapter = mAdapter
    }


}
