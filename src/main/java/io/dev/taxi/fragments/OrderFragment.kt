package io.dev.taxi.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.dev.taxi.R
import io.dev.taxi.activities.TaxiActivity
import io.dev.taxi.presenters.TaxiPresenter
import kotlinx.android.synthetic.main.fragment_order.view.*

class OrderFragment: Fragment(), View.OnClickListener {

    private lateinit var mParentActivity: TaxiActivity
    private lateinit var presenter: TaxiPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mParentActivity = activity as TaxiActivity
        presenter = TaxiPresenter(mParentActivity, DriversFragment(), TripsFragment())
        return inflater!!.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.order_cancel.setOnClickListener(this)
        view.order_button.setOnClickListener(this)
        view.edit_from.isEnabled = false
        view.edit_to.isEnabled = false
        val bundleArgs = this.arguments
        if (bundleArgs != null) {
            view.edit_from.hint = bundleArgs.getString("departure")
            view.edit_to.hint = bundleArgs.getString("destination")
            view.order_price.text = "Цена поездки: " + bundleArgs.getDouble("price").toInt().toString() + "RUB"
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.order_cancel -> {
                mParentActivity.onBackPressed()
            }
            R.id.order_button -> {
                presenter.orderTrip(mParentActivity.tripModel.departure,
                        mParentActivity.tripModel.destination,
                        mParentActivity.tripModel.userId,
                        mParentActivity.tripModel.driverId,
                        mParentActivity.tripModel.cost)
            }
        }
    }
}