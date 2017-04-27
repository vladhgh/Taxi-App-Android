package io.dev.taxi.fragments

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.messaging.FirebaseMessaging
import io.dev.taxi.R
import kotlinx.android.synthetic.main.fragment_payment.view.*
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import io.dev.taxi.activities.TaxiActivity
import kotlinx.android.synthetic.main.activity_register.*

class PaymentFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Оплата"
        val inflatedView = inflater!!.inflate(R.layout.fragment_payment, container, false)
        val data = arrayOf("Наличными", "Картой", "PayPal", "QIWI Wallet")
        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inflatedView.spinner_pyment.adapter = adapter
        inflatedView.spinner_pyment.setSelection(0)
        inflatedView.save_payment.setOnClickListener {
            Hawk.put("paymentMethod", inflatedView.spinner_pyment.selectedItem.toString())
            Toast.makeText(activity, "Метод оплаты был сохранен ^_^", Toast.LENGTH_LONG).show()
        }
        return inflatedView
    }

}