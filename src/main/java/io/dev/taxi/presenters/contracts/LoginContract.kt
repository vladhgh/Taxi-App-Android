package io.dev.taxi.presenters.contracts

interface LoginContract {
    interface Presenter {
        fun onLogin(mobileNumber: String, password: String)
    }
    interface View {
        fun onLoginSuccess(token: String)
        fun onLoginFailure(errorMessage: String)
    }
}