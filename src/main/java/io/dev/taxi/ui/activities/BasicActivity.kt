package io.dev.taxi.ui.activities

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.dev.taxi.R
import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_login.*


abstract class BasicActivity() : AppCompatActivity(){

    protected var mProgressDialog: ProgressDialog? = null


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
        if (pass != null && pass.length > 6) {
            return true
        }
        return false
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    fun showSnackBar(message: String) {
        Snackbar.make(this.coordinator_layout, message, Snackbar.LENGTH_LONG).show()
    }

}
