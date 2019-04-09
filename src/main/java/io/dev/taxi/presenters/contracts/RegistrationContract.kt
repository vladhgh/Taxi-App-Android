package io.dev.taxi.presenters.contracts

import android.support.design.widget.TextInputLayout
import android.widget.EditText
import android.widget.Toolbar
import io.dev.taxi.R


interface RegistrationContract {
    interface Presenter {
        fun onPhoneChangedListener(input: EditText)
        fun onPasswordChangedListener(input: EditText)
        fun onNameChangedListener(input: EditText)
        fun onEmailChangedListener(input: EditText)
        fun onRegister(mobileNumber: String, name: String, role: String, email: String,
                       department: String,
                       avatar: String,
                       carModel: String?,
                       carNumber: String?,
                       password: String)
        fun validateCredentials(inputPhone: EditText, inputPassword: EditText, inputEmail: EditText, inputName: EditText)
    }
    interface View {
        fun isNetworkOnline(): Boolean
        fun shake(input: TextInputLayout)
        fun showSnackBar(message: String)
        fun enableButton()
        fun disableButton()
        fun showProgress()
        fun hideProgress()
        fun setUpActionBarWithTitle(toolbar: android.support.v7.widget.Toolbar)
        fun onValidationSuccess(input: String)
        fun onValidationFailure(input: String, message: String)
        fun onRegistrationSuccess(result: String)
        fun onRegistrationFailed(error: String)
        fun initPhoneMask()
    }
}