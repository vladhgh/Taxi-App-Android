package io.dev.taxi.data.cases

import com.google.gson.JsonParser
import io.dev.taxi.utils.AppConfig
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import okhttp3.Request

class RegistrationUseCase : UseCase<String, RegistrationUseCase.Parameters>(Schedulers.io()) {
    class Parameters(val mobileNumber: String, val name: String, val role: String, val email: String,
                     val department: String,
                     val avatar: String,
                     val carModel: String?,
                     val carNumber: String?,
                     val password: String)

    override fun buildObservable(parameters: Parameters): Observable<String> {
        return Observable.create {
            subscriber ->
            var formBody = FormBody.Builder()
                    .add("name", parameters.name)
                    .add("role", parameters.role)
                    .add("avatar", parameters.avatar)
                    .add("email", parameters.email)
                    .add("department", parameters.department)
                    .add("mobileNumber", parameters.mobileNumber)
                    .add("password", parameters.password)
                    .build()
            if (parameters.carModel != null && parameters.carNumber != null) {
                formBody = FormBody.Builder()
                        .add("name", parameters.name)
                        .add("role", parameters.role)
                        .add("avatar", parameters.avatar)
                        .add("email", parameters.email)
                        .add("department", parameters.department)
                        .add("mobileNumber", parameters.mobileNumber)
                        .add("password", parameters.password)
                        .add("carModel", parameters.carModel)
                        .add("carNumber", parameters.carNumber)
                        .build()
            }
            val req = Request.Builder()
                    .url(AppConfig.BASE_URL + "users/register")
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
                    "Registration error" -> subscriber.onError(Throwable(userObj.get("message").asString))
                }
            }
        }
    }

}
