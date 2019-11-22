package com.norihirosunada.checkpoint

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import android.location.Criteria
import android.location.LocationManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.inputmethodservice.Keyboard
import kotlinx.android.synthetic.main.activity_maps.*

//import sun.jvm.hotspot.utilities.IntArray

const val REQUEST_CODE_LOCATION = 123
const val LOCATION_PERMISSION = 42

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    protected var mLastLocation: Location? = null
    protected var mLastMarker: Marker? = null

    private val DEFAULT_ZOOM: Float = 15.0F

    //チェックポイントにチェックできる最大距離（m）
    private val maxCheckPointDistance: Float = 200.0F

    lateinit var markerList: ArrayList<RowData>
    lateinit var markerMap: Map<String, RowData>

    private val resultIntent = Intent()
    var checkedMarkerList = ArrayList<RowData>()

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private var showPermissionDeniedDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        markerList = intent.getSerializableExtra("markers") as ArrayList<RowData>
        markerMap = markerList.associateBy({it.title}, {it})

        // FABを押すとマーカーの情報をcheckedMarkerListに追加する
        // MainActivityにcheckedMarkerListを返す
        checkFab.setOnClickListener {
            val mLastMarker = this.mLastMarker
            if (mLastMarker != null){
                if (markerMap.containsKey(mLastMarker.title)){
                    checkedMarkerList.add(markerMap.getValue(mLastMarker.title))
                    Log.d("fab", "Clicked checkFab")
                }
            }
            resultIntent.putExtra("checkedMarkers", checkedMarkerList)
            setResult(Activity.RESULT_OK, resultIntent)
            if(checkedMarkerList.isEmpty()){
                Log.d("checkedMarkerList", "empty")
            }else{
                Log.d("checkedMarkerList", "not empty"+checkedMarkerList[0])
            }
            checkFab.hide()
//            finish()
        }

        checkFab.hide()
    }

    /**
     * Display a dialog box asking the user to grant permissions if they were denied
     */
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (showPermissionDeniedDialog) {
            AlertDialog.Builder(this).apply {
                setPositiveButton(R.string.ok, null)
                setMessage(R.string.location_permission_denied)
                create()
            }.show()
            showPermissionDeniedDialog = false
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        enableMyLocation()
        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
            isIndoorLevelPickerEnabled = true
            isMapToolbarEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isRotateGesturesEnabled = true
        }

        mMap.setOnMarkerClickListener(this@MapsActivity)
        mMap.setOnMapClickListener {
            checkFab.hide()
        }

        getLastLocatioin()
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM))

        // チェックポイントのマーカーを設置する
//        val osakaOffice = LatLng(34.697296, 135.492451)
//        val osakaStation = LatLng(34.702460, 135.495926)
//        mMap.addMarker(MarkerOptions().position(osakaOffice).title("大阪オフィス"))
//        mMap.addMarker(MarkerOptions().position(osakaStation).title("大阪駅"))

        markerList.forEach {
            mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lng)).title(it.title))
        }

    }

    /** Override the onRequestPermissionResult to use EasyPermissions */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            mMap.isMyLocationEnabled = true
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION_PERMISSION)
    fun getLastLocatioin(){
        // Get LocationManager object from System Service LOCATION_SERVICE
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Create a criteria object to retrieve provider
        val criteria = Criteria()
        // Get the name of the best provider
        val provider = locationManager.getBestProvider(criteria, true)
        // Get Current Location
        mLastLocation = locationManager.getLastKnownLocation(provider)

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        var result = FloatArray(3)
        Location.distanceBetween(marker.position.latitude, marker.position.longitude,
            mLastLocation!!.latitude, mLastLocation!!.longitude, result)
        val distance = result[0]
        // マーカーが現在地の近くにあればFABを表示する
        if (distance <= maxCheckPointDistance){
            checkFab.show()
            Log.d("marker", "marker near me")
        }else{
            checkFab.hide()
            Log.d("marker", "marker not near me")
        }

        mLastMarker = marker

        return false
    }

    override fun onInfoWindowClick(p0: Marker?) {

    }

}
