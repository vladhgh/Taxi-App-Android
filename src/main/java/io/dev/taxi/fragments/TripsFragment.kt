package io.dev.taxi.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.dev.taxi.R

class TripsFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Мои поездки"
        return inflater!!.inflate(R.layout.fragment_trips, container, false)
    }
}