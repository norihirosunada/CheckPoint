package com.norihirosunada.checkpoint

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class RowData(val title: String, val lat: Double, val lng: Double, var checkedFlag: Boolean): Serializable