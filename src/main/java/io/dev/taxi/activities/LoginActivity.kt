package io.dev.taxi.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import com.orhanobut.hawk.Hawk
import io.dev.taxi.R
import io.dev.taxi.presenters.LoginPresenter
import io.dev.taxi.presenters.contracts.LoginContract
import kotlinx.android.synthetic.main.activity_login.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class LoginActivity: AppCompatActivity(), View.OnClickListener, LoginContract.View {

    val presenter: LoginPresenter = LoginPresenter(this)
    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.button_login.isEnabled = false
        initPhoneMask()
        presenter.onPhoneChangedListener(this.input_phone)
        presenter.onPasswordChangedListener(this.input_password)
        presenter.validateCredentials(this.input_phone, this.input_password)
        this.button_login.setOnClickListener(this)
        this.text_register.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_login -> {
                this.input_password.clearFocus()
                this.button_login.requestFocus()
                presenter.onLogin(input_phone.text.toString(), input_password.text.toString())
            }
            R.id.text_register -> startActivity(Intent(this, RegistrationActivity::class.java))

        }
    }

    override fun onPhoneValidationFailure() {
        this.button_login.isEnabled = false
        this.input_phone_layout.error = resources.getString(R.string.error_userDontExist)
        shake(this.input_phone_layout)
    }

    override fun shake(input: TextInputLayout) {
        input.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
    }

    override fun onPhoneValidationSuccess() {
        this.input_phone_layout.error = null
    }

    override fun onPasswordValidationFailure() {
        this.input_password_layout.error = resources.getString(R.string.error_password)
        shake(this.input_password_layout)
    }

    override fun onPasswordValidationSuccess() {
        this.input_password_layout.error = null
    }

    override fun isNetworkOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    override fun showProgress() {
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

    override fun hideProgress() {
        if (mProgressDialog != null) {
            if(mProgressDialog!!.isShowing) {
                mProgressDialog!!.hide()
            }
        }
    }

    override fun enableButton() {
        this.button_login.isEnabled = true
    }

    override fun disableButton() {
        this.button_login.isEnabled = false
    }

    override fun onLoginSuccess(result: String) {
        Hawk.put("jsonToken", result)
        startActivity(Intent(this, TaxiActivity::class.java))
        hideProgress()
        finish()
    }

    override fun onLoginFailure(message: String) {
        //TODO: Create error factory to generate error message based on error type
        showSnackBar(resources.getString(R.string.error_userDontExist))
        hideProgress()
    }

    override fun initPhoneMask() {
        MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)).installOn(this.input_phone)
    }

    override fun showSnackBar(message: String) {
        Snackbar.make(this.coordinator_layout, message, Snackbar.LENGTH_LONG).show()
    }
}