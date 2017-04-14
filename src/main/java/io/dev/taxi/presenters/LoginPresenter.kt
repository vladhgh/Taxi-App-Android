package io.dev.taxi.presenters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.auth0.android.jwt.JWT
import com.orhanobut.hawk.Hawk
import io.dev.taxi.data.cases.LoginUseCase
import io.dev.taxi.data.cases.ValidationUseCase
import io.dev.taxi.presenters.contracts.LoginContract
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import java.util.concurrent.TimeUnit
import io.reactivex.subjects.PublishSubject

class LoginPresenter(val view: LoginContract.View) : LoginContract.Presenter {

    private val loginUseCase: LoginUseCase = LoginUseCase()
    private val validationUseCase: ValidationUseCase = ValidationUseCase()
    private var isValidPhone = false
    private var isValidPassword = false


    private fun createLoginObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {
            override fun onNext(token: String) {
                view.onLoginSuccess(token)
            }
            override fun onError(e: Throwable) {
                view.showSnackBar("Отсутствует соединение с сервером")
            }
            override fun onComplete() {}
        }
    }
    private fun createValidationObserver(): DisposableObserver<Boolean> {
        return object : DisposableObserver<Boolean>() {
            override fun onNext(result: Boolean) {
                if (result) {
                    isValidPhone = true
                    view.onPhoneValidationSuccess()
                } else {
                    isValidPhone = false
                    view.onPhoneValidationFailure()
                }
            }
            override fun onError(e: Throwable) {
                view.showSnackBar("Отсутствует соединение с сервером")
            }
            override fun onComplete() {}
        }
    }

    private fun editTextObservable(input: EditText): Observable<String> {
        val subject = PublishSubject.create<String>()

        input.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                subject.onNext(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        return subject
    }

    private fun validatePassword(password: String) {
        if (password.length < 6) {
            isValidPassword = false
            view.onPasswordValidationFailure()
        } else {
            isValidPassword = true
            view.onPasswordValidationSuccess()
        }
    }
    private fun isValidInputs(password: String):Boolean {
        return  isValidPhone && password.length >= 6
    }


    override fun onPasswordChangedListener(input: EditText) {
        editTextObservable(input)
                .doOnNext { view.onPasswordValidationSuccess() }
                .debounce(800, TimeUnit.MILLISECONDS)
                .filter { data -> return@filter !data.isEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .map { data -> validatePassword(data) }
                .subscribe()
    }

    override fun onPhoneChangedListener(input: EditText) {
        editTextObservable(input)
                .doOnNext { view.onPhoneValidationSuccess() }
                .debounce(800, TimeUnit.MILLISECONDS)
                .filter { data -> return@filter !data.isEmpty() && data.length == 18 }
                .map { data -> validationUseCase.execute(createValidationObserver(), ValidationUseCase.Parameters(data)) }
                .subscribe()
    }

    override fun validateCredentials(inputPhone: EditText, inputPassword: EditText) {
        Observable.combineLatest(editTextObservable(inputPhone), editTextObservable(inputPassword), BiFunction {
            phone: String, password: String ->
            return@BiFunction isValidInputs(password)
        }).debounce(800, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe { status ->
            if (status) {
                view.enableButton()
            } else {
                view.disableButton()
            }
        }
    }

    override fun isTokenExpired(): Boolean {
         return !(Hawk.contains("jsonToken") && JWT(Hawk.get("jsonToken")).getClaim("expire")?.asInt() != 0)
    }

    override fun onLogin(mobileNumber: String, password: String) {
        if (view.isNetworkOnline()) {
            view.showProgress()
            loginUseCase.execute(createLoginObserver(), LoginUseCase.Parameters(mobileNumber, password))
        } else {
            view.showSnackBar("Отсутствует соединение с сетью")
        }
    }

}
