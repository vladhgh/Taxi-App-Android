package io.dev.taxi.activities


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import io.dev.taxi.R
import kotlinx.android.synthetic.main.activity_register.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class RegisterActivity: BasicActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)).installOn(this.input_phone_reg)
        setUpActionBarWithTitle(this.toolbar)
        val button_register = this.button_register_reg
        button_register.isEnabled = false

        button_register.setOnClickListener(this)

        this.input_phone_reg.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidPhone(s.toString())) {
                    input_phone_layout_reg.error = resources.getString(R.string.error_phone)
                    button_register.isEnabled = false
                } else {
                    input_phone_layout_reg.error = null
                    button_register.isEnabled = (isHaveError(input_password_layout_reg) && isHaveError(input_email_layout_reg) && isHaveError(input_phone_layout_reg))
                            && (!isInputEmpty(input_password_reg) && !isInputEmpty(input_phone_reg) && !isInputEmpty(input_email_reg))
                }
            }
        })

        this.input_password_reg.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidPassword(s.toString())) {
                    input_password_layout_reg.error = resources.getString(R.string.error_password)
                    button_register.isEnabled = false
                } else {
                    input_password_layout_reg.error = null
                    button_register.isEnabled = (isHaveError(input_password_layout_reg) && isHaveError(input_email_layout_reg) && isHaveError(input_phone_layout_reg))
                            && (!isInputEmpty(input_password_reg) && !isInputEmpty(input_phone_reg) && !isInputEmpty(input_email_reg))
                }
            }
        })

        this.input_email_reg.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s)) {
                    input_email_layout_reg.error = resources.getString(R.string.error_email)
                    button_register.isEnabled = false
                } else {
                    input_email_layout_reg.error = null
                    button_register.isEnabled = (isHaveError(input_password_layout_reg) && isHaveError(input_email_layout_reg) && isHaveError(input_phone_layout_reg))
                            && (!isInputEmpty(input_password_reg) && !isInputEmpty(input_phone_reg) && !isInputEmpty(input_email_reg))
                }
            }
        })

        this.input_password_reg.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftKeyboard(this)
                if (button_register.isEnabled) button_register.performClick()
                return@OnEditorActionListener true
            }
            false
        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_register_reg -> {
                this.input_password_reg.clearFocus()
                this.button_register_reg.requestFocus()
                if (isNetworkOnline()) {
                    if (isValidInput(this.input_firstName_reg, this.input_firstName_layout_reg) &&
                            isValidInput(this.input_lastName_reg, this.input_lastName_layout_reg) &&
                            isValidInput(this.input_department_reg, this.input_department_layout_reg)) {
                        showProgress()
                        registerUser(this.input_firstName_reg.text.toString(),
                                this.input_lastName_reg.text.toString(),
                                this.input_email_reg.text.toString(),
                                "avatar",
                                this.input_department_reg.text.toString(),
                                this.input_phone_reg.text.toString(),
                                this.input_password_reg.text.toString())
                    }
                } else {
                    showSnackBar(resources.getString(R.string.error_network))
                }

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> this.finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}
