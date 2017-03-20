package io.dev.taxi.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import com.orhanobut.hawk.Hawk
import io.dev.taxi.R

import kotlinx.android.synthetic.main.activity_login.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class MainActivity: BasicActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Hawk.init(this).build()
        if (!Hawk.contains("jsonToken")) {
            setContentView(R.layout.activity_login)
            MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)).installOn(this.input_phone)
            val input_phone_layout = this.input_phone_layout
            val input_phone = this.input_phone
            val button_login = this.button_login
            button_login.isEnabled = false
            val input_password_layout = this.input_password_layout

            this.button_login.setOnClickListener(this)
            this.text_register.setOnClickListener(this)

            this.input_phone.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isValidPhone(s.toString())) {
                        input_phone_layout.error = resources.getString(R.string.error_phone)
                        button_login.isEnabled = false
                    } else {
                        input_phone_layout.error = null
                        button_login.isEnabled = (isHaveError(input_password_layout) && isHaveError(input_phone_layout)) && (!isInputEmpty(input_password) && !isInputEmpty(input_phone))
                    }
                }
            })

            this.input_password.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isValidPassword(s.toString())) {
                        input_password_layout.error = resources.getString(R.string.error_password)
                        button_login.isEnabled = false
                    } else {
                        input_password_layout.error = null
                        button_login.isEnabled = (isHaveError(input_password_layout) && isHaveError(input_phone_layout)) && (!isInputEmpty(input_password) && !isInputEmpty(input_phone))
                    }
                }
            })


            this.input_password.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKeyboard(this)
                    if (button_login.isEnabled) button_login.performClick()
                    return@OnEditorActionListener true
                }
                false
            })

        } else {
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_login -> {
                this.input_password.clearFocus()
                this.button_login.requestFocus()
                if (isNetworkOnline()) {
                    loginUser(input_phone.text.toString(), input_password.text.toString())
                } else {
                    showSnackBar(resources.getString(R.string.error_network))
                }
            }
            R.id.text_register -> startActivity(Intent(this, RegisterActivity::class.java))

        }
    }



}