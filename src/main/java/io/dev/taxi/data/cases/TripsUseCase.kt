package io.dev.taxi.data.cases

import com.google.gson.JsonParser
import io.dev.taxi.data.models.Driver
import io.dev.taxi.data.models.Trip
import io.dev.taxi.utils.AppConfig
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request

class TripsUseCase : UseCase<ArrayList<Trip>, TripsUseCase.Parameters>(Schedulers.io()) {
    class Parameters (val userId: String)

    override fun buildObservable(parameters: Parameters): Observable<ArrayList<Trip>> {
        val tripsList = ArrayList<Trip>()
        return Observable.create {
            subscriber ->
            val req = Request.Builder()
                    .url(AppConfig.BASE_URL + "trips/" + parameters.userId)
                    .get()
                    .build()
            val res: String = AppConfig.CLIENT.newCall(req).execute().body().string()

            val tripsArray = JsonParser().parse(res).asJsonArray

            tripsArray
                    .map { it.asJsonObject }
                    .forEach { tripsList.add(Trip(it.get("departure").asString, it.get("destination").asString, it.get("driverId").asString,
                            it.get("cost").asString, it.get("createdAt").asString))
                    }

            if (tripsArray.count() != 0) {
                subscriber.onNext(tripsList)
                subscriber.onComplete()
            } else {
                subscriber.onError(Throwable("No drivers found"))
            }
        }
    }

}
