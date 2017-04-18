package io.dev.taxi.presenters.contracts

import io.dev.taxi.fragments.DriversFragment

interface TaxiContract {
    interface Presenter {
        fun loadDrivers(showProgress: Boolean)
        fun loadTrips()
    }
    interface View {
        fun isNetworkOnline(): Boolean
        fun initUserInterface()
        fun showButtons()
        fun hideButtons()
        fun showSnackBar(message: String)
        fun showToast(message: String)
        fun showProgress()
        fun hideProgress()
    }
    interface DriversView {
        fun onDriversLoadSuccess(drivers: ArrayList<DriversFragment.Driver>)
    }
}
