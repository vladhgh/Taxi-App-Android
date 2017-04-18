package io.dev.taxi.presenters

import io.dev.taxi.data.cases.DriversUseCase
import io.dev.taxi.fragments.DriversFragment
import io.dev.taxi.presenters.contracts.TaxiContract
import io.reactivex.observers.DisposableObserver

class TaxiPresenter(val view: TaxiContract.View, val driversView: TaxiContract.DriversView) : TaxiContract.Presenter {

    private val driversUseCase: DriversUseCase = DriversUseCase()

    private fun createDriversObserver(): DisposableObserver<ArrayList<DriversFragment.Driver>> {
        return object : DisposableObserver<ArrayList<DriversFragment.Driver>>() {
            override fun onNext(drivers: ArrayList<DriversFragment.Driver>) {
                driversView.onDriversLoadSuccess(drivers)
            }
            override fun onError(e: Throwable) {
                view.showSnackBar(e.toString())
            }
            override fun onComplete() {}
        }
    }

    override fun loadDrivers(showProgress: Boolean) {
        if (view.isNetworkOnline()) {
            if (showProgress) {
                view.showProgress()
            }
            driversUseCase.execute(createDriversObserver(), DriversUseCase.Parameters())
        } else {
            view.showSnackBar("Отсутствует соединение с сетью")
        }
    }

    override fun loadTrips() {

    }

}