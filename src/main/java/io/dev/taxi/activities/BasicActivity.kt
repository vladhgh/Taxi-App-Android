package io.dev.taxi.activities

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.dev.taxi.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.view.inputmethod.InputMethodManager
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

import android.net.ConnectivityManager
import android.support.design.widget.TextInputLayout
import android.util.Log
import com.auth0.android.jwt.JWT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.ObservableEmitter

import io.reactivex.ObservableOnSubscribe


abstract class BasicActivity : AppCompatActivity(){

    protected var mProgressDialog: ProgressDialog? = null
    protected val client = OkHttpClient.Builder().build()


    fun showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this, R.style.custom_dialog)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mProgressDialog!!.show()
            mProgressDialog!!.setContentView(R.layout.progress_splash)
        } else {
            mProgressDialog!!.show()
            mProgressDialog!!.setContentView(R.layout.progress_splash)
        }

    }
    fun hideProgress() {
        if (mProgressDialog != null) {
            if(mProgressDialog!!.isShowing) {
                mProgressDialog!!.hide()
            }
        }
    }

    fun getUserTokenOnAuth(mobileNumber: String, password: String): Observable<String> {
        return Observable.create {
            subscriber ->
                val formBody = FormBody.Builder()
                        .add("mobileNumber", mobileNumber)
                        .add("password", password)
                        .build()
                val req = Request.Builder()
                        .url("http://192.168.1.183:8080/api/auth/login")
                        .post(formBody)
                        .build()
                val res: String = client.newCall(req).execute().body().string()

                val userObj = JsonParser().parse(res).asJsonObject

                if (!userObj.isJsonNull && userObj.get("message").asString != "Authentication error") {
                    subscriber.onNext(userObj.get("token").asString)
                    subscriber.onComplete()
                } else {
                    subscriber.onError(Throwable(userObj.get("message").asString))
                }
        }
    }
    fun loginUser(mobileNumber: String, password: String) {
        getUserTokenOnAuth(mobileNumber, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { token ->
                            Hawk.put("jsonToken", token)
                            startActivity(Intent(this, TaxiActivity::class.java))
                            hideProgress()
                            finish()
                        },
                        { error ->
                            Log.e("loginUser error:", error.toString())
                            showSnackBar(resources.getString(R.string.error_userDontExist))
                            hideProgress()
                        }
                )
    }

    fun getUserTokenOnReg(firstName: String, secondName: String, email: String, avatar: String, department: String,
                          mobileNumber: String,
                          password: String): Observable<String> {
        return Observable.create {
            subscriber ->
                val formBody = FormBody.Builder()
                        .add("firstName", firstName)
                        .add("secondName", secondName)
                        .add("email", email)
                        .add("avatar", avatar)
                        .add("department", department)
                        .add("mobileNumber", mobileNumber)
                        .add("password", password)
                        .build()
                val req = Request.Builder()
                        .url("http://192.168.1.183:8080/api/users/register")
                        .post(formBody)
                        .build()
                val res: String = client.newCall(req).execute().body().string()

                val jsonObj = JsonParser().parse(res).asJsonObject
                if (!jsonObj.isJsonNull && jsonObj.get("message").asString != "Registration error") {
                    subscriber.onNext(jsonObj.get("token").asString)
                    subscriber.onComplete()
                } else {
                    when (jsonObj.get("error").asString) {
                        "User already registered" -> subscriber.onError(Throwable(resources.getString(R.string.error_userExist)))
                    }
                }

        }
    }

    fun registerUser(firstName: String,
                     secondName: String,
                     email: String,
                     avatar: String,
                     department: String,
                     mobileNumber: String,
                     password: String) {
        getUserTokenOnReg(firstName, secondName, email, avatar, department, mobileNumber, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { token ->
                            Hawk.put("jsonToken", token)
                            startActivity(Intent(this, TaxiActivity::class.java))
                            hideProgress()
                            finish()
                        },
                        { error ->
                            Log.e("registerUser error:", error.toString())
                            showSnackBar(resources.getString(R.string.error_userExist))
                            hideProgress()
                        }
                )
    }


    fun setUpActionBarWithTitle(toolbar: android.support.v7.widget.Toolbar ,title: String = resources.getString(R.string.app_name)) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = title
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        if (target == null) {
            return false
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun isHaveError(input_layout: TextInputLayout): Boolean {
        return input_layout.error == null
    }
    fun isInputEmpty(input: TextInputEditText): Boolean {
        return input.text.toString().trim().isEmpty()
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0)
    }
    fun isValidPhone(phone: String?): Boolean {
        if (phone!!.length < 18) {
            return false
        }
        return true
    }

    fun isValidPassword(pass: String?): Boolean {
        if (pass != null && pass.length >= 6) {
            return true
        }
        return false
    }

    fun isValidInput(input: TextInputEditText, inputLayout: TextInputLayout): Boolean {
        if (input.text == null || input.text.isEmpty()) {
            inputLayout.error = resources.getString(R.string.error_empty)
            return false
        } else {
            inputLayout.error = null
            return true
        }
    }

    fun isNetworkOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun isJwtExpired(jwt: JWT): Boolean {
        val time = jwt.getClaim("expire")?.asInt()
        return false
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    fun showSnackBar(message: String) {
        Snackbar.make(this.coordinator_layout, message, Snackbar.LENGTH_LONG).show()
    }

}
