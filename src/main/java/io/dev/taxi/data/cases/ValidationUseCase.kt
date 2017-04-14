package io.dev.taxi.data.cases

import com.google.gson.JsonParser
import io.dev.taxi.utils.AppConfig
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request


class ValidationUseCase: UseCase<Boolean, ValidationUseCase.Parameters>(Schedulers.io()){
    class Parameters(val input: String)

    override fun buildObservable(parameters: Parameters): Observable<Boolean> {
        return Observable.create {
            subscriber ->
            val req = Request.Builder()
                    .url(AppConfig.BASE_URL + "users/" + parameters.input)
                    .get()
                    .build()
            val res: String = AppConfig.CLIENT.newCall(req).execute().body().string()

            val userObj = JsonParser().parse(res).asJsonObject

            if (!userObj.isJsonNull) {
                when (userObj.get("message").asString) {
                    "Success" -> {
                        subscriber.onNext(true)
                        subscriber.onComplete()
                    }
                    "Not Found" -> {
                        subscriber.onNext(false)
                        subscriber.onComplete()
                    }
                    else -> subscriber.onError(Throwable(userObj.get("message").asString))
                }
            }
        }
    }
}