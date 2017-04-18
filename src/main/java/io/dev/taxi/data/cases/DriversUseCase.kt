package io.dev.taxi.data.cases

import com.google.gson.JsonParser
import io.dev.taxi.fragments.DriversFragment
import io.dev.taxi.utils.AppConfig
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request

class DriversUseCase : UseCase<ArrayList<DriversFragment.Driver>, DriversUseCase.Parameters>(Schedulers.io()) {
    class Parameters

    override fun buildObservable(parameters: Parameters): Observable<ArrayList<DriversFragment.Driver>> {
        val driversList = ArrayList<DriversFragment.Driver>()
        return Observable.create {
            subscriber ->
            val req = Request.Builder()
                    .url(AppConfig.BASE_URL + "users/drivers")
                    .get()
                    .build()
            val res: String = AppConfig.CLIENT.newCall(req).execute().body().string()

            val driversArray = JsonParser().parse(res).asJsonArray

            driversArray
                    .map { it.asJsonObject }
                    .forEach { driversList.add(DriversFragment.Driver(it.get("name").asString, it.get("avatar").asString, it.get("carModel").asString,
                            it.get("carNumber").asString, it.get("mobileNumber").asString,
                            it.get("email").asString))
                    }

            if (driversArray.count() != 0) {
                subscriber.onNext(driversList)
                subscriber.onComplete()
            } else {
                subscriber.onError(Throwable("No drivers found"))
            }
        }
    }

}