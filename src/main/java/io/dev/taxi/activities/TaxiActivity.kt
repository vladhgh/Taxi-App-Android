package io.dev.taxi.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.auth0.android.jwt.JWT
import com.orhanobut.hawk.Hawk
import io.dev.taxi.R
import io.dev.taxi.fragments.*
import io.dev.taxi.presenters.contracts.TaxiContract
import kotlinx.android.synthetic.main.activity_taxi.*
import kotlinx.android.synthetic.main.app_bar_taxi.*
import kotlinx.android.synthetic.main.nav_header_taxi.view.*
import android.view.animation.OvershootInterpolator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.app.FragmentManager
import android.app.PendingIntent.getActivity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.text.TextUtils.indexOf
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import io.dev.taxi.utils.DirectionsJSONParser
import kotlinx.android.synthetic.main.content_taxi.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class TaxiActivity : AppCompatActivity(), TaxiContract.View, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val MINIMUM_TIME_BETWEEN_LOCATION_UPDATES = 5000L
    private val MINIMUM_DISTANCE_BETWEEN_LOCATION_UPDATES = 1.0f

    private var googleMap: GoogleMap? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var mProgressDialog: ProgressDialog? = null
    private var uiSettings: UiSettings? = null
    private var sMapFragment: SupportMapFragment? = null
    private var mLocationManager: LocationManager? = null
    internal var markerPoints = ArrayList<LatLng>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taxi)
        initUserInterface()
        this.fab_zoomIn.setOnClickListener(this)
        this.fab_zoomOut.setOnClickListener(this)
        this.fab_location.setOnClickListener(this)
        this.fab_home.setOnClickListener(this)
        this.fab_work.setOnClickListener(this)

        if (savedInstanceState == null) {
            val item = this.nav_view.menu.getItem(0).setChecked(true)
            onNavigationItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d("OnLocationChanged", "begin")
        requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        Log.d("OnLocationChanged", "stop")
        stopLocationUpdates()
    }

    private fun requestLocationUpdates() {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_LOCATION_UPDATES,
                    MINIMUM_DISTANCE_BETWEEN_LOCATION_UPDATES, this)
        }
        if (mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager!!.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null)
        }
    }

    private fun stopLocationUpdates() {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager!!.removeUpdates(this)
    }

    override fun onBackPressed() {
        if (this.drawer_layout.isDrawerOpen(GravityCompat.START)) {
            this.drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (sMapFragment!!.isAdded) {
            supportFragmentManager.beginTransaction().hide(sMapFragment).commit()
        }
        if (!this.fab_location.isHidden){
            hideButtons()
        }
        when (item.itemId) {
            R.id.nav_map -> {
                if (!sMapFragment!!.isAdded) {
                    supportFragmentManager.beginTransaction().add(R.id.map, sMapFragment).commit()
                    if (fragmentManager.findFragmentById(R.id.content) != null) {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.content)).commit()
                    }
                } else {
                    supportFragmentManager.beginTransaction().show(sMapFragment).commit()
                    if (fragmentManager.findFragmentById(R.id.content) != null) {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.content)).commit()
                    }
                }
                this.title = "Карта"
                showButtons()
            }
            R.id.nav_trips -> fragmentManager.beginTransaction().replace(R.id.content, TripsFragment()).commit()
            R.id.nav_free_trips -> fragmentManager.beginTransaction().replace(R.id.content, FreeTripsFragment()).commit()
            R.id.nav_payment -> fragmentManager.beginTransaction().replace(R.id.content, PaymentFragment()).commit()
            R.id.nav_settings -> fragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
            R.id.nav_drivers -> fragmentManager.beginTransaction().replace(R.id.content, DriversFragment()).commit()
            R.id.nav_exit -> {
                Hawk.delete("jsonToken")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            android.R.id.home -> {
                Log.d("Home", "clicked")
                fragmentManager.beginTransaction().remove(OrderFragment()).commit()
            }

        }

        this.drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupMap()
        } else {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        uiSettings = googleMap?.uiSettings
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            }
        } else {
            setupMap()
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun setupMap() {
        uiSettings!!.isMyLocationButtonEnabled = false
        googleMap!!.isMyLocationEnabled = true
        googleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map))
    }

    private fun zoomToLocation() {
        val location = this.googleMap!!.myLocation
        if (location != null) {
            val target = LatLng(location.latitude, location.longitude)
            val builder = CameraPosition.Builder()
            builder.zoom(17f)
            builder.target(target)
            this.googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()))

        }
    }

    private fun createCustomAnimation() {
        val set = AnimatorSet()

        val scaleOutX = ObjectAnimator.ofFloat(fab.menuIconView, "scaleX", 1.0f, 0.2f)
        val scaleOutY = ObjectAnimator.ofFloat(fab.menuIconView, "scaleY", 1.0f, 0.2f)

        val scaleInX = ObjectAnimator.ofFloat(fab.menuIconView, "scaleX", 0.2f, 1.0f)
        val scaleInY = ObjectAnimator.ofFloat(fab.menuIconView, "scaleY", 0.2f, 1.0f)

        scaleOutX.duration = 50
        scaleOutY.duration = 50

        scaleInX.duration = 150
        scaleInY.duration = 150

        scaleInX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                fab.menuIconView.setImageResource(if (!fab.isOpened)
                    R.drawable.ic_local_taxi_white_24dp
                else
                    R.drawable.ic_close_white_24dp
                )
            }
        })

        set.play(scaleOutX).with(scaleOutY)
        set.play(scaleInX).with(scaleInY).after(scaleOutX)
        set.interpolator = OvershootInterpolator(2f)

        this.fab.iconToggleAnimatorSet = set
    }

    override fun initUserInterface() {
        createCustomAnimation()
        setSupportActionBar(this.toolbar)
        toggle = ActionBarDrawerToggle(
                this, this.drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        this.drawer_layout.addDrawerListener(toggle!!)
        this.nav_view.setNavigationItemSelectedListener(this)
        toggle!!.syncState()
        sMapFragment = SupportMapFragment.newInstance()
        sMapFragment!!.getMapAsync(this)
        val jwt = JWT(Hawk.get("jsonToken"))
        val header = this.nav_view.getHeaderView(0)
        header.username.text = jwt.getClaim("name")?.asString()
        header.email.text = jwt.getClaim("role")?.asString()
        if (jwt.getClaim("avatar")?.asString() != "avatar") {
            val encodedString = jwt.getClaim("avatar")?.asString()
            header.imageView.setImageBitmap(getImageIcon(encodedString))
        }

    }

    fun getImageIcon(image: String?): Bitmap {
        val image_data = Base64.decode(image, Base64.NO_WRAP)
        val options = BitmapFactory.Options()
        options.outMimeType = "image/jpeg"
        return BitmapFactory.decodeByteArray(image_data, 0, image_data.size, options)
    }

    override fun isNetworkOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
    override fun showSnackBar(message: String) {
        Snackbar.make(this.taxi_coordinator, message, Snackbar.LENGTH_LONG).show()
    }
    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    override fun showButtons() {
        this.fab.showMenuButton(true)
        this.fab_location.show(true)
        this.fab_zoomIn.show(true)
        this.fab_zoomOut.show(true)
    }
    override fun hideButtons() {
        this.fab.hideMenuButton(true)
        this.fab_location.hide(true)
        this.fab_zoomIn.hide(true)
        this.fab_zoomOut.hide(true)
    }
    override fun showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this, R.style.custom_dialog_no_background)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mProgressDialog!!.show()
            mProgressDialog!!.setContentView(R.layout.progress_splash)
        } else {
            mProgressDialog!!.show()
            mProgressDialog!!.setContentView(R.layout.progress_splash)
        }
    }
    override fun hideProgress() {
        if (mProgressDialog != null) {
            if(mProgressDialog!!.isShowing) {
                mProgressDialog!!.hide()
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.d("OnLocationChanged", "fired")
        zoomToLocation()
        stopLocationUpdates()
    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        when (status) {
            LocationProvider.OUT_OF_SERVICE -> stopLocationUpdates()
            LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.d("Location Provider", "temporary unavailable")
            LocationProvider.AVAILABLE -> requestLocationUpdates()
        }
    }
    override fun onProviderEnabled(provider: String?) {
    }
    override fun onProviderDisabled(provider: String?) {
    }
    override fun onConnected(p0: Bundle?) {
    }
    override fun onConnectionSuspended(p0: Int) {
    }
    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    internal inner class DownloadTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg url: String): String = downloadUrl(url[0])

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            ParserTask().execute(result)
        }
    }

    internal inner class ParserTask: AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        override fun doInBackground(vararg params: String?): List<List<HashMap<String, String>>> {
            val jObject: JSONObject = JSONObject(params[0])
            val routes: List<List<HashMap<String, String>>>
            val parser =  DirectionsJSONParser()
            routes = parser.parse(jObject)
            return routes
        }
        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            var points: ArrayList<LatLng>
            var lineOptions: PolylineOptions? = null

            for (i in result) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                val path = result[result.indexOf(i)]

                for (j in path) {
                    val point = path[path.indexOf(j)]
                    val lat: Double = point["lat"]!!.toDouble()
                    val lng: Double = point["lng"]!!.toDouble()
                    val position: LatLng = LatLng(lat, lng)
                    points.add(position)
                }

                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(R.color.yellow800)
                lineOptions.geodesic(true)
            }
            googleMap!!.addPolyline(lineOptions)
        }
    }

    internal fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service
        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
        return url
    }

    @Throws(IOException::class)
    internal fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            urlConnection = url.openConnection() as HttpURLConnection

            urlConnection.connect()

            iStream = urlConnection.inputStream

            val br = BufferedReader(InputStreamReader(iStream!!))

            val sb = StringBuffer()

            var line: String
            while (true) {
                line = br.readLine() ?: "null"
                if ( line != "null") {
                    sb.append(line)
                } else {
                    break
                }
            }
            data = sb.toString()
            br.close()

        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.fab_location -> {
                zoomToLocation()
            }
            R.id.fab_home -> {
                this.fab.close(true)
                this.fab.hideMenu(true)
                this.fab_zoomOut.hide(true)
                this.fab_zoomIn.hide(true)
                val myLocation = googleMap!!.myLocation
                markerPoints.add(LatLng(myLocation.latitude, myLocation.longitude))
                val firstMarker = googleMap!!.addMarker(MarkerOptions().position(LatLng(myLocation.latitude, myLocation.longitude)).title("Вы"))
                markerPoints.add(LatLng(55.1737565,61.4502218))
                val secondMarker = googleMap!!.addMarker(MarkerOptions().position(LatLng(55.1599743, 61.3710326)).title("Дом"))
                val url = getDirectionsUrl(markerPoints[0], markerPoints[1])
                val downloadTask = DownloadTask()
                downloadTask.execute(url)
                val markers = arrayOf(firstMarker, secondMarker)
                val b: LatLngBounds.Builder = LatLngBounds.Builder()
                for (m: Marker in markers) {
                    b.include(m.position)
                }
                val bounds: LatLngBounds = b.build()
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                fragmentManager.beginTransaction().add(R.id.map, OrderFragment()).commit()
                zoomToLocation()
            }
            R.id.fab_work -> {
                hideButtons()
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowHomeEnabled(true)
                this.toolbar.setNavigationOnClickListener {
                    showButtons()
                    googleMap!!.clear()
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    supportActionBar!!.setDisplayShowHomeEnabled(false)
                    toggle = ActionBarDrawerToggle(
                            this, this.drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
                    this.drawer_layout.addDrawerListener(toggle!!)
                    toggle!!.syncState()
                    fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag("Order")).commit()
                }
                val myLocation = googleMap!!.myLocation
                markerPoints.add(LatLng(myLocation.latitude, myLocation.longitude))
                val firstMarker = googleMap!!.addMarker(MarkerOptions().position(LatLng(myLocation.latitude, myLocation.longitude)).title("Вы"))
                markerPoints.add(LatLng(55.1599743, 61.3710326))
                val secondMarker = googleMap!!.addMarker(MarkerOptions().position(LatLng(55.1599743, 61.3710326)).title("Работа"))
                val url = getDirectionsUrl(markerPoints[0], markerPoints[1])
                val downloadTask = DownloadTask()
                downloadTask.execute(url)
                val markers = arrayOf(firstMarker, secondMarker)
                val b: LatLngBounds.Builder = LatLngBounds.Builder()
                for (m: Marker in markers) {
                    b.include(m.position)
                }
                val bounds: LatLngBounds = b.build()
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                fragmentManager.beginTransaction().add(R.id.map, OrderFragment(), "Order").commit()
            }
            R.id.fab_zoomIn -> {
                val target = googleMap!!.cameraPosition.target
                val zoom = googleMap!!.cameraPosition.zoom + 0.5f
                val builder = CameraPosition.Builder()
                builder.zoom(zoom)
                builder.target(target)
                this.googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()))
            }
            R.id.fab_zoomOut -> {
                val target = googleMap!!.cameraPosition.target
                val zoom = googleMap!!.cameraPosition.zoom - 0.5f
                val builder = CameraPosition.Builder()
                builder.zoom(zoom)
                builder.target(target)
                this.googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()))
            }

        }
    }
}
