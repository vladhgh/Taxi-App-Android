package io.dev.taxi.data.cases

import com.google.gson.JsonParser
import io.dev.taxi.utils.AppConfig
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import okhttp3.Request

class LoginUseCase : UseCase<String, LoginUseCase.Parameters>(Schedulers.io()) {
    class Parameters(val mobileNumber: String, val password: String)

    override fun buildObservable(parameters: Parameters): Observable<String> {
        return Observable.create {
            subscriber ->
            val formBody = FormBody.Builder()
                    .add("mobileNumber", parameters.mobileNumber)
                    .add("password", parameters.password)
                    .build()
            val req = Request.Builder()
                    .url(AppConfig.BASE_URL + "auth/login")
                    .post(formBody)
                    .build()
            val res: String = AppConfig.CLIENT.newCall(req).execute().body().string()

            val userObj = JsonParser().parse(res).asJsonObject

            if (!userObj.isJsonNull) {
                when (userObj.get("message").asString) {
                    "Success" -> {
                        subscriber.onNext(userObj.get("token").asString)
                        subscriber.onComplete()
                    }
                    "Authentication error" -> subscriber.onError(Throwable(userObj.get("message").asString))
                }
            }
        }
    }

}
