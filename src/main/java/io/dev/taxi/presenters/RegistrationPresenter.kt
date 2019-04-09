package io.dev.taxi.presenters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import io.dev.taxi.data.cases.RegistrationUseCase
import io.dev.taxi.data.cases.ValidationUseCase
import io.dev.taxi.observables.EditTextObservable
import io.dev.taxi.presenters.contracts.RegistrationContract
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function4
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import org.reactivestreams.Subscriber
import java.util.concurrent.TimeUnit

class RegistrationPresenter(val view: RegistrationContract.View) : RegistrationContract.Presenter {

    private val registrationUseCase = RegistrationUseCase()
    private val validationUseCase = ValidationUseCase()
    private var isValidPhone = false
    private var isValidPassword = false
    private var isValidEmail= false
    private var isValidName = false


    private fun createRegistrationObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {
            override fun onNext(result: String) {
                view.onRegistrationSuccess(result)
            }
            override fun onError(e: Throwable) {
                view.showSnackBar("connection time out")
            }
            override fun onComplete() {

            }
        }
    }
    private fun createPhoneValidationObserver(): DisposableObserver<Boolean> {
        return object : DisposableObserver<Boolean>() {
            override fun onNext(result: Boolean) {
                if (result) {
                    isValidPhone = false
                    Log.d("isValidPhone is", isValidPhone.toString())
                    view.onValidationFailure("phone", "user already exist")
                } else {
                    isValidPhone = true
                    Log.d("isValidPhone is", isValidPhone.toString())
                    view.onValidationSuccess("phone")
                }
            }
            override fun onError(e: Throwable) {
                view.showSnackBar("connection time out")
            }
            override fun onComplete() {

            }
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 6) {
            isValidPassword = false
            Log.d("isValidPassword is", isValidPassword.toString())
            view.onValidationFailure("password", "length is too short")
        } else {
            isValidPassword = true
            Log.d("isValidPassword is", isValidPassword.toString())
            view.onValidationSuccess("password")
        }
    }
    private fun validateEmail(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValidEmail = false
            view.onValidationFailure("email", "do not match email pattern")
        } else {
            isValidEmail = true
            view.onValidationSuccess("email")
        }
    }
    private fun validateName(name: String) {
        if (name.length < 2) {
            isValidName = false
            view.onValidationFailure("name", "name is too short")
        } else {
            isValidName = true
            view.onValidationSuccess("name")
        }
    }
    private fun isValidInputs(password: String):Boolean {
        return password.length >= 6 && isValidPhone && isValidName && isValidEmail
    }



    override fun onPhoneChangedListener(input: EditText) {
        EditTextObservable().from(input)
                .doOnNext { view.onValidationSuccess("phone") }
                .debounce(800, TimeUnit.MILLISECONDS)
                .filter { data -> return@filter !data.isEmpty() && data.length == 18 }
                .map { data -> validationUseCase.execute(createPhoneValidationObserver(), ValidationUseCase.Parameters(data)) }
                .subscribe()
    }

    override fun onPasswordChangedListener(input: EditText) {
        EditTextObservable().from(input)
                .doOnNext { view.onValidationSuccess("password") }
                .debounce(800, TimeUnit.MILLISECONDS)
                .filter { data -> return@filter !data.isEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .map { data -> validatePassword(data) }
                .subscribe()
    }
    override fun onEmailChangedListener(input: EditText) {
        EditTextObservable().from(input)
                .doOnNext { view.onValidationSuccess("email") }
                .debounce(1200, TimeUnit.MILLISECONDS)
                .filter { data -> return@filter !data.isEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .map { data -> validateEmail(data) }
                .subscribe()
    }
    override fun onNameChangedListener(input: EditText) {
        EditTextObservable().from(input)
                .doOnNext { view.onValidationSuccess("name") }
                .debounce(800, TimeUnit.MILLISECONDS)
                .filter { data -> return@filter !data.isEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .map { data -> validateName(data) }
                .subscribe()
    }
    override fun validateCredentials(inputPhone: EditText, inputPassword: EditText, inputEmail: EditText, inputName: EditText) {
        Observable.combineLatest(
                EditTextObservable().from(inputPhone),
                EditTextObservable().from(inputPassword),
                EditTextObservable().from(inputEmail),
                EditTextObservable().from(inputName),
                Function4 {
                    phone: String, password: String, email: String, name: String ->
                        return@Function4 isValidInputs(password)
        }).debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
            status ->
            if (status) {
                view.enableButton()
            } else {
                view.disableButton()
            }
        }
    }

    override fun onRegister(mobileNumber: String, name: String, role: String, email: String, department: String, avatar: String, carModel: String?, carNumber: String?, password: String) {
        if (view.isNetworkOnline()) {
            view.showProgress()
            if (carModel != null && carNumber != null) {
                registrationUseCase.execute(createRegistrationObserver(), RegistrationUseCase.Parameters(mobileNumber, name, role, email, department, avatar, carModel, carModel, password))
            } else {
                registrationUseCase.execute(createRegistrationObserver(), RegistrationUseCase.Parameters(mobileNumber, name, role, email, department, avatar, null, null, password))
            }
        } else {
            view.onRegistrationFailed("Отсутствует соединение с сетью")
        }
    }

}
