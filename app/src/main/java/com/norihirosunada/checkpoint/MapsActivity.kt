package com.norihirosunada.checkpoint

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import android.location.Criteria
import android.location.LocationManager
import android.view.MenuItem
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlin.collections.ArrayList

//import sun.jvm.hotspot.utilities.IntArray

const val REQUEST_CODE_LOCATION = 123
const val LOCATION_PERMISSION = 1

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var mLastLocation: Location? = null
    private var mLastMarker: Marker? = null

    private val defaultZoom: Float = 15.0F

    //チェックポイントにチェックできる最大距離（m）
    private val maxCheckPointDistance: Float = 100.0F

//    lateinit var markerList: ArrayList<RowData>
    private lateinit var markerMap: Map<String, CheckPoint>

    private val resultIntent = Intent()
    private var checkedResultList = ArrayList<CheckPoint>()

    private var checkedArray = ArrayList<String>()

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private var showPermissionDeniedDialog = false

    private var mLastTappedMarker: Marker? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // MainActivityから遷移時の処理
        // intentからチェックイン済みのマーカー情報取得　Map型に整形
        checkedArray = intent.getStringArrayListExtra("checked markers")
        markerMap = ilumiList.associateBy({it.title}, {it})
        // FABを押すとマーカーの情報をcheckedMarkerListに追加する
        // MainActivityにcheckedMarkerListを返す
        checkFab.setOnClickListener {
            val mLastMarker = this.mLastMarker
            if (mLastMarker != null){
                checkedResultList.add(markerMap.getValue(mLastMarker.title))
                Log.d("fab", "Clicked checkFab")

                resultIntent.putExtra("checkedMarkers", checkedResultList)
//            resultIntent.flags = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
                setResult(Activity.RESULT_OK, resultIntent)
                val icon = BitmapDescriptorFactory.fromResource(R.drawable.checked_markermdpi)
                mLastMarker.setIcon(icon)
                checkedArray.add(mLastMarker.title)

                checkFab.hide()
            }
        }
        checkFab.hide()

        // EventDetailActivityから遷移時の処理
        // 地図上タップでマーカー設置


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

        getLastLocation()
        if (hasLocationPermission()) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        mLastLocation!!.latitude,
                        mLastLocation!!.longitude
                    )
                )
            )
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(defaultZoom))

        // チェックポイントのマーカーを設置する
        ilumiList.forEach {
            if (checkedArray.contains(it.title)){
                val icon = BitmapDescriptorFactory.fromResource(R.drawable.checked_markermdpi)
                mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lng)).title(it.title).icon(icon))
            }else {
                mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lng)).title(it.title))
            }
        }

        mMap.setOnMapClickListener { point ->
            val lastTappedMarker = mLastTappedMarker
            if (lastTappedMarker != null){
                lastTappedMarker.remove()
            }
            mLastTappedMarker = mMap.addMarker(MarkerOptions().position(point))

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
    fun getLastLocation(){
        if (hasLocationPermission()){
            // Get LocationManager object from System Service LOCATION_SERVICE
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // Create a criteria object to retrieve provider
            val criteria = Criteria()
            // Get the name of the best provider
            val provider = locationManager.getBestProvider(criteria, true)
            // Get Current Location
            mLastLocation = locationManager.getLastKnownLocation(provider)
        }else{
            EasyPermissions.requestPermissions(this, "", LOCATION_PERMISSION, ACCESS_FINE_LOCATION)
        }

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        getLastLocation()
        if (!checkedArray.contains(marker.title)) {
            val result = FloatArray(3)
            Location.distanceBetween(
                marker.position.latitude, marker.position.longitude,
                mLastLocation!!.latitude, mLastLocation!!.longitude, result
            )
            val distance = result[0]
            // マーカーが現在地の近くにあればFABを表示する
            if (distance <= maxCheckPointDistance) {
                checkFab.show()
                Log.d("marker", "marker near me")
            } else {
                checkFab.hide()
                Log.d("marker", "marker not near me")
            }
        }else{
            checkFab.hide()
        }

        mLastMarker = marker

        return false
    }

    override fun onInfoWindowClick(p0: Marker?) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

//    fun getCurrentPlace(){
//        // Use fields to define the data types to return.
//        val placeFields: List<Place.Field> = Collections.singletonList(Place.Field.NAME);
//
//        // Use the builder to create a FindCurrentPlaceRequest.
//        val request: FindCurrentPlaceRequest =
//                FindCurrentPlaceRequest.newInstance(placeFields);
//
//        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
//        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            val placeResponse: Task<FindCurrentPlaceResponse> = placesClient.findCurrentPlace(request);
//            placeResponse.addOnCompleteListener(task -> {
//                if (task.isSuccessful()){
//                    val response: FindCurrentPlaceResponse = task.getResult();
//                    for (val placeLikelihood: PlaceLikelihood in response.getPlaceLikelihoods()) {
//                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
//                                placeLikelihood.getPlace().getName(),
//                                placeLikelihood.getLikelihood()));
//                    }
//                } else {
//                    val exception: Exception = task.getException();
//                    if (exception instanceof ApiException) {
//                        ApiException apiException = (ApiException) exception;
//                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//                    }
//                }
//            });
//        } else {
//            // A local method to request required permissions;
//            // See https://developer.android.com/training/permissions/requesting
//            getLocationPermission();
//        }
//    }

}
