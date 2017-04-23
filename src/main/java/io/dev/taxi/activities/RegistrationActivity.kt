package io.dev.taxi.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.orhanobut.hawk.Hawk
import io.dev.taxi.R
import io.dev.taxi.presenters.RegistrationPresenter
import io.dev.taxi.presenters.contracts.RegistrationContract
import kotlinx.android.synthetic.main.activity_register.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import android.widget.ArrayAdapter
import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.design.widget.TextInputLayout
import android.util.Base64
import android.util.Log
import android.widget.AdapterView
import android.widget.EditText
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.android.synthetic.main.nav_header_taxi.*
import java.io.ByteArrayOutputStream
import java.io.File


class RegistrationActivity : AppCompatActivity(), RegistrationContract.View, View.OnClickListener {

    val presenter = RegistrationPresenter(this)
    var mProgressDialog: ProgressDialog? = null
    var base64Image: String = "avatar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setUpActionBarWithTitle(this.toolbar)
        initPhoneMask()
        this.button_register_reg.isEnabled = false

        presenter.onPhoneChangedListener(this.input_phone_reg)
        presenter.onPasswordChangedListener(this.input_password_reg)
        presenter.onEmailChangedListener(this.input_email_reg)
        presenter.onNameChangedListener(this.input_name_reg)

        presenter.validateCredentials(this.input_phone_reg, this.input_password_reg, this.input_email_reg, this.input_name_reg)
        this.button_register_reg.setOnClickListener(this)
        this.image_upload.setOnClickListener(this)

        val data = arrayOf("Водитель", "Пассажир")
        val dataDep = arrayOf("Юридический", "ВМИ", "Энерга")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        val adapterDep = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataDep)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterDep.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.spinner_department.adapter = adapterDep
        this.spinner_department.setSelection(0)
        this.spinner_role.adapter = adapter
        this.spinner_role.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, v: View, position: Int, id: Long) {
                when (spinner_role.selectedItem.toString()) {
                    "Водитель" -> {
                        input_carModel_layout_reg.visibility = View.VISIBLE
                        input_carNumber_layout_reg.visibility = View.VISIBLE
                    }
                    "Пассажир" -> {
                        input_carModel_layout_reg.visibility = View.GONE
                        input_carNumber_layout_reg.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                Log.v("routes", "nothing selected")
            }
        }
        this.spinner_role.setSelection(1)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_register_reg -> {
                this.input_password_reg.clearFocus()
                this.button_register_reg.requestFocus()
                if(this.spinner_role.selectedItem == "Водитель") {
                    presenter.onRegister(this.input_phone_reg.text.toString(),
                            this.input_name_reg.text.toString(),
                            this.spinner_role.selectedItem.toString(),
                            this.input_email_reg.text.toString(),
                            this.spinner_department.selectedItem.toString(),
                            base64Image,
                            this.input_carModel_reg.text.toString(),
                            this.input_carNumber_reg.text.toString(),
                            this.input_password_reg.text.toString())
                } else {
                    presenter.onRegister(this.input_phone_reg.text.toString(),
                            this.input_name_reg.text.toString(),
                            this.spinner_role.selectedItem.toString(),
                            this.input_email_reg.text.toString(),
                            this.spinner_department.selectedItem.toString(),
                            base64Image,
                            null,
                            null,
                            this.input_password_reg.text.toString())
                }
            }
            R.id.image_upload -> {
                openGallery(1)
            }
        }
    }

    override fun showSnackBar(message: String) {
        Snackbar.make(this.coordinator_layout_reg, message, Snackbar.LENGTH_LONG).show()
    }

    fun openGallery(req_code: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,
                "Select file to upload "), req_code)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data.data
            val imageStream = contentResolver.openInputStream(selectedImageUri)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            this.image_upload.setImageBitmap(selectedImage)
            base64Image = BitMapToString(selectedImage)
        }
    }

    fun BitMapToString(bitmap: Bitmap): String {
        System.gc()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val b = baos.toByteArray()
        val temp = Base64.encodeToString(b, Base64.DEFAULT)
        return temp
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> this.finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initPhoneMask() {
        MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)).installOn(this.input_phone_reg)
    }

    override fun setUpActionBarWithTitle(toolbar: android.support.v7.widget.Toolbar) {
        setSupportActionBar(toolbar)
        val title = "Регистрация"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = title
    }

    override fun onValidationSuccess(input: String) {
        when (input) {
            "phone" -> {
                this.input_phone_layout_reg.error = null
            }
            "password" -> {
                this.input_password_layout_reg.error = null
            }
            "email" -> {
                this.input_email_layout_reg.error = null
            }
            "name" -> {
                this.input_name_layout_reg.error = null
            }
        }
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

    override fun onValidationFailure(input: String, message: String) {
        when (input) {
            "phone" -> {
                shake(this.input_phone_layout_reg)
                this.input_phone_layout_reg.error = resources.getString(R.string.error_userExist)
            }
            "password" -> {
                shake(this.input_password_layout_reg)
                this.input_password_layout_reg.error = resources.getString(R.string.error_password)
            }
            "email" -> {
                shake(this.input_email_layout_reg)
                this.input_email_layout_reg.error = resources.getString(R.string.error_email)
            }
            "name" -> {
                shake(this.input_name_layout_reg)
                this.input_name_layout_reg.error = resources.getString(R.string.error_empty)
            }
        }
    }

    override fun enableButton() {
        this.button_register_reg.isEnabled = true
    }

    override fun disableButton() {
        this.button_register_reg.isEnabled = false
    }

    override fun shake(input: TextInputLayout) {
        YoYo.with(Techniques.Shake).duration(500).playOn(input)
    }

    override fun onRegistrationSuccess(result: String) {
        Hawk.init(this).build()
        Hawk.put("jsonToken", result)
        startActivity(Intent(this, TaxiActivity::class.java))
        hideProgress()
        finish()
    }

    override fun onRegistrationFailed(error: String) {
        //TODO: Create error factory to generate error message based on error type
        hideProgress()
        showSnackBar("Ошибка регистрации")
    }


}