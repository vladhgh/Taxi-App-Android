package io.dev.taxi.presenters.contracts

import io.dev.taxi.data.models.Driver
import io.dev.taxi.data.models.Trip
import io.dev.taxi.data.models.TripModel
import io.dev.taxi.fragments.DriversFragment

interface TaxiContract {
    interface Presenter {
        fun loadDrivers(showProgress: Boolean)
        fun loadTrips(userId: String, showProgress: Boolean)
        fun orderTrip(departure: String, destination: String, userId: String, driverId: String, cost: String)
    }
    interface View {
        fun isNetworkOnline(): Boolean
        fun initUserInterface()
        fun onOrderSuccess(isSuccess: Boolean)
        fun showButtons()
        fun hideButtons()
        fun showSnackBar(message: String)
        fun showToast(message: String)
        fun showProgress()
        fun hideProgress()
    }
    interface DriversView {
        fun onDriversLoadSuccess(drivers: ArrayList<Driver>)
    }
    interface TripsView {
        fun onTripsLoadSuccess(trips: ArrayList<Trip>)
    }
}
