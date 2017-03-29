package io.dev.taxi.presenters

import io.dev.taxi.data.cases.LoginUseCase
import io.dev.taxi.presenters.contracts.LoginContract
import io.reactivex.observers.DisposableObserver

open class LoginPresenter(val view: LoginContract.View) : LoginContract.Presenter {

    private val loginUseCase: LoginUseCase = LoginUseCase()

    private fun createLoginObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {
            override fun onNext(token: String) {
                view.onLoginSuccess(token)
            }

            override fun onError(e: Throwable) {
                view.onLoginFailure(e.toString())
            }

            override fun onComplete() {

            }
        }
    }

    override fun onLogin(mobileNumber: String, password: String) {
        loginUseCase.execute(createLoginObserver(), LoginUseCase.Parameters(mobileNumber, password))
    }

}
