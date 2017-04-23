package io.dev.taxi.fragments

import android.app.Fragment
import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import io.dev.taxi.R
import io.dev.taxi.activities.TaxiActivity
import io.dev.taxi.presenters.TaxiPresenter
import io.dev.taxi.presenters.contracts.TaxiContract
import kotlinx.android.synthetic.main.fragment_drivers.view.*
import android.util.Base64
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import io.dev.taxi.adapters.DriversAdapter
import io.dev.taxi.data.models.Driver


class DriversFragment: Fragment(), TaxiContract.DriversView {

    private var driversList = ArrayList<Driver>()
    private lateinit var parentActivity: TaxiActivity
    private lateinit var presenter: TaxiPresenter
    private lateinit var currentView: View
    private lateinit var adapter: DriversAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Водители"
        parentActivity = activity as TaxiActivity
        presenter = TaxiPresenter(parentActivity, this, TripsFragment())
        currentView = inflater!!.inflate(R.layout.fragment_drivers, container, false)
        presenter.loadDrivers(true)
        adapter = DriversAdapter(driversList)
        currentView.recycler.adapter = adapter
        adapter.setItemsOnClickListener(object: DriversAdapter.OnItemClickListener {
            override fun onMessageClick(driver: Driver) {
                val phone = driver.mobileNumber
                phone.replace("(", "")
                phone.replace(")","")
                phone.replace(" ", "")
                val uri = Uri.parse("smsto:$phone")
                val it = Intent(Intent.ACTION_SENDTO, uri)
                it.putExtra("sms_body", "Здравствуй, друг!")
                try {
                    startActivity(it)
                } catch (error: ActivityNotFoundException) {
                    Toast.makeText(activity, "Упс", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPhoneClick(driver: Driver) {
                val intent = Intent(Intent.ACTION_DIAL)
                val phone = driver.mobileNumber
                phone.replace("(", "")
                phone.replace(")","")
                phone.replace(" ", "")
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        })
        currentView.recycler.layoutManager = LinearLayoutManager(activity)
        currentView.drivers_pullToRefresh.setOnRefreshListener {
            presenter.loadDrivers(false)
        }
        return currentView
    }

    override fun onDriversLoadSuccess(drivers: ArrayList<Driver>) {
        parentActivity.hideProgress()
        adapter.setItems(drivers)
        adapter.notifyDataSetChanged()
        currentView.drivers_pullToRefresh.isRefreshing = false
    }
}
