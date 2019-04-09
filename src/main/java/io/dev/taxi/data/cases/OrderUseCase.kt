package io.dev.taxi.data.cases

import com.google.gson.JsonParser
import io.dev.taxi.utils.AppConfig
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import okhttp3.Request

class OrderUseCase : UseCase<Boolean, OrderUseCase.Parameters>(Schedulers.io()) {
    class Parameters(val departure: String, val destination: String, val userId: String, val driverId: String, val cost: String)

    override fun buildObservable(parameters: Parameters): Observable<Boolean> {
        return Observable.create {
            subscriber ->
            val formBody = FormBody.Builder()
                    .add("departure", parameters.departure)
                    .add("destination", parameters.destination)
                    .add("userId", parameters.userId)
                    .add("driverId", parameters.driverId)
                    .add("cost", parameters.cost)
                    .build()
            val req = Request.Builder()
                    .url(AppConfig.BASE_URL + "trips/add")
                    .post(formBody)
                    .build()
            val res: String = AppConfig.CLIENT.newCall(req).execute().body().string()

            val responseObject = JsonParser().parse(res).asJsonObject

            if (!responseObject.isJsonNull) {
                when (responseObject.get("message").asString) {
                    "Success" -> {
                        subscriber.onNext(true)
                        subscriber.onComplete()
                    }
                    "Order error" -> subscriber.onError(Throwable(responseObject.get("message").asString))
                }
            }
        }
    }

}
