package io.dev.taxi.presenters.contracts

import android.support.design.widget.TextInputLayout
import android.widget.EditText

interface LoginContract {
    interface Presenter {
        fun onPhoneChangedListener(input: EditText)
        fun onPasswordChangedListener(input: EditText)
        fun isTokenExpired(): Boolean
        fun validateCredentials(inputPhone: EditText, inputPassword: EditText)
        fun onLogin(mobileNumber: String, password: String)
    }
    interface View {
        fun shake(input: TextInputLayout)
        fun showSnackBar(message: String)
        fun initPhoneMask()
        fun onPhoneValidationFailure()
        fun onPhoneValidationSuccess()
        fun onPasswordValidationFailure()
        fun onPasswordValidationSuccess()
        fun isNetworkOnline(): Boolean
        fun onLoginSuccess(result: String)
        fun onLoginFailure(message: String)
        fun enableButton()
        fun disableButton()
        fun showProgress()
        fun hideProgress()
    }
}