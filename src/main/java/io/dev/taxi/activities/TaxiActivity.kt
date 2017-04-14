package io.dev.taxi.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.auth0.android.jwt.JWT
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.orhanobut.hawk.Hawk
import io.dev.taxi.R
import io.dev.taxi.fragments.*
import io.dev.taxi.presenters.contracts.TaxiContract
import kotlinx.android.synthetic.main.activity_taxi.*
import kotlinx.android.synthetic.main.app_bar_taxi.*
import kotlinx.android.synthetic.main.nav_header_taxi.view.*

class TaxiActivity : AppCompatActivity(), TaxiContract.View, NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    var googleMap: GoogleMap? = null
    var uiSettings: UiSettings? = null
    var sMapFragment: SupportMapFragment? = null
    val locationManager: LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taxi)

        initUserInfo()
        setSupportActionBar(this.toolbar)
        this.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val toggle = ActionBarDrawerToggle(
                this, this.drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        this.drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        this.nav_view.setNavigationItemSelectedListener(this)


        sMapFragment = SupportMapFragment.newInstance()
        sMapFragment!!.getMapAsync(this)
        supportFragmentManager.beginTransaction().add(R.id.map, sMapFragment).commit()

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
    fun setupMap() {
        uiSettings!!.isZoomControlsEnabled = true
        googleMap!!.isMyLocationEnabled = true
        googleMap!!.setMinZoomPreference(10.0f)
        googleMap!!.setMaxZoomPreference(20.0f)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupMap()
        } else {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if (this.drawer_layout.isDrawerOpen(GravityCompat.START)) {
            this.drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.taxi, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_exit) {
            Hawk.delete("jsonToken")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (sMapFragment!!.isAdded) {
            supportFragmentManager.beginTransaction().hide(sMapFragment).commit()
        }
        when (item.itemId) {
            R.id.nav_map -> {
                if (!sMapFragment!!.isAdded) {
                    supportFragmentManager.beginTransaction().add(R.id.map, sMapFragment).commit()
                    fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.content)).commit()
                } else {
                    supportFragmentManager.beginTransaction().show(sMapFragment).commit()
                    if (fragmentManager.findFragmentById(R.id.content) != null) {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.content)).commit()
                    }
                }
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

        }

        this.drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun initUserInfo() {
        val jwt = JWT(Hawk.get("jsonToken"))
        val header = this.nav_view.getHeaderView(0)
        header.username.text = jwt.getClaim("name")?.asString()
        header.email.text = jwt.getClaim("role")?.asString()
    }
}
