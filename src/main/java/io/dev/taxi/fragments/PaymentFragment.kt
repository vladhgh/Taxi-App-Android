package io.dev.taxi.fragments

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.messaging.FirebaseMessaging
import io.dev.taxi.R
import kotlinx.android.synthetic.main.fragment_payment.view.*
import android.widget.Toast
import io.dev.taxi.activities.TaxiActivity

class PaymentFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Оплата"


        val inflatedView = inflater!!.inflate(R.layout.fragment_payment, container, false)
        inflatedView.button_pay.setOnClickListener {
            FirebaseMessaging.getInstance().subscribeToTopic("news")
            val msg = "Subscribed"
            Log.d("Payment fragment", msg)
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
        return inflatedView
    }

}