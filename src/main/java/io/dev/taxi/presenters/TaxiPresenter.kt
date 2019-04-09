package io.dev.taxi.presenters

import io.dev.taxi.data.cases.DriversUseCase
import io.dev.taxi.data.cases.OrderUseCase
import io.dev.taxi.data.cases.TripsUseCase
import io.dev.taxi.data.models.Driver
import io.dev.taxi.data.models.Trip
import io.dev.taxi.fragments.DriversFragment
import io.dev.taxi.fragments.TripsFragment
import io.dev.taxi.presenters.contracts.TaxiContract
import io.reactivex.observers.DisposableObserver

class TaxiPresenter(val view: TaxiContract.View, val driversView: TaxiContract.DriversView, val tripsView: TaxiContract.TripsView) : TaxiContract.Presenter {

    private val driversUseCase = DriversUseCase()
    private val tripsUseCase = TripsUseCase()
    private val orderUseCase = OrderUseCase()

    private fun createOrdersObserver(): DisposableObserver<Boolean> {
        return object : DisposableObserver<Boolean>() {
            override fun onNext(isSuccess: Boolean) {
                view.onOrderSuccess(isSuccess)
            }
            override fun onError(e: Throwable) {
                view.showSnackBar(e.toString())
            }
            override fun onComplete() {}
        }
    }

    private fun createDriversObserver(): DisposableObserver<ArrayList<Driver>> {
        return object : DisposableObserver<ArrayList<Driver>>() {
            override fun onNext(drivers: ArrayList<Driver>) {
                driversView.onDriversLoadSuccess(drivers)
            }
            override fun onError(e: Throwable) {
                view.showSnackBar(e.toString())
            }
            override fun onComplete() {}
        }
    }

    private fun createTripsObserver(): DisposableObserver<ArrayList<Trip>> {
        return object : DisposableObserver<ArrayList<Trip>>() {
            override fun onNext(trips: ArrayList<Trip>) {
                tripsView.onTripsLoadSuccess(trips)
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

    override fun loadTrips(userId: String, showProgress: Boolean) {
        if (view.isNetworkOnline()) {
            tripsUseCase.execute(createTripsObserver(), TripsUseCase.Parameters(userId))
        } else {
            view.showSnackBar("Отсутствует соединение с сетью")
        }
    }

    override fun orderTrip(departure: String, destination: String, userId: String, driverId: String, cost: String) {
        if (view.isNetworkOnline()) {
            orderUseCase.execute(createOrdersObserver(), OrderUseCase.Parameters(departure, destination, userId, driverId, cost))
        } else {
            view.showSnackBar("Отсутствует соединение с сетью")
        }
    }
}