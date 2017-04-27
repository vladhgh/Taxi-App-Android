package io.dev.taxi.fragments

import android.app.Fragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.dev.taxi.R
import kotlinx.android.synthetic.main.fragment_free_trips.*
import kotlinx.android.synthetic.main.fragment_free_trips.view.*

class FreeTripsFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Бесплатные поездки"
        val view = inflater!!.inflate(R.layout.fragment_free_trips, container, false)
        view.promo.isEnabled = false
        view.copy.setOnClickListener{
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Label", view.promo.text.toString())
            clipboard.primaryClip = clip
            Toast.makeText(activity, "Промокод был скопирован ^_^", Toast.LENGTH_LONG).show()
        }
        return view
    }
}