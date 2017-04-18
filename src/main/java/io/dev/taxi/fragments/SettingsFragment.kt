package io.dev.taxi.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.auth0.android.jwt.JWT
import com.orhanobut.hawk.Hawk
import io.dev.taxi.R
import io.dev.taxi.activities.TaxiActivity
import kotlinx.android.synthetic.main.activity_taxi.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.nav_header_taxi.view.*

class SettingsFragment: Fragment() {

    var parentActivity: TaxiActivity? = null
    var currentView: View? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Настройки"
        currentView = inflater!!.inflate(R.layout.fragment_settings, container, false)
        parentActivity = activity as TaxiActivity
        initInterface()
        return currentView!!
    }
    private fun initInterface() {
        val jwt = JWT(Hawk.get("jsonToken"))
        currentView!!.user_name.text = jwt.getClaim("name")?.asString()
        currentView!!.user_role.text = jwt.getClaim("role")?.asString()
        if (jwt.getClaim("avatar")?.asString() != "avatar") {
            val encodedString = jwt.getClaim("avatar")?.asString()
            currentView!!.user_image.setImageBitmap(parentActivity!!.getImageIcon(encodedString))
        }
    }
}