package io.dev.taxi.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.dev.taxi.R
import io.dev.taxi.activities.TaxiActivity
import io.dev.taxi.presenters.TaxiPresenter
import io.dev.taxi.presenters.contracts.TaxiContract
import io.dev.taxi.adapters.TripsAdapter
import io.dev.taxi.data.models.Trip
import kotlinx.android.synthetic.main.fragment_trips.view.*

class TripsFragment: Fragment(), TaxiContract.TripsView {

    private var tripsList = ArrayList<Trip>()
    private lateinit var parentActivity: TaxiActivity
    private lateinit var presenter: TaxiPresenter
    private lateinit var currentView: View
    private lateinit var adapter: TripsAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Мои поездки"
        parentActivity = activity as TaxiActivity
        presenter = TaxiPresenter(parentActivity, DriversFragment(), this)
        currentView = inflater!!.inflate(R.layout.fragment_trips, container, false)
        presenter.loadTrips(parentActivity.tripModel.userId, true)
        adapter = TripsAdapter(tripsList)
        currentView.recycler.adapter = adapter
        currentView.recycler.layoutManager = LinearLayoutManager(activity)
        currentView.trips_pullToRefresh.setOnRefreshListener {
            presenter.loadTrips(parentActivity.tripModel.userId, false)
        }
        return currentView
    }

    override fun onTripsLoadSuccess(trips: ArrayList<Trip>) {
        parentActivity.hideProgress()
        adapter.setItems(trips)
        adapter.notifyDataSetChanged()
        currentView.trips_pullToRefresh.isRefreshing = false
    }
}
