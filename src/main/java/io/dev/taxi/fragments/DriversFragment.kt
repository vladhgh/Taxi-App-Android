package io.dev.taxi.fragments

import android.app.Fragment
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import io.dev.taxi.R
import io.dev.taxi.activities.TaxiActivity
import io.dev.taxi.presenters.TaxiPresenter
import io.dev.taxi.presenters.contracts.TaxiContract
import kotlinx.android.synthetic.main.fragment_drivers.view.*
import android.support.v7.widget.CardView
import android.util.Base64
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class DriversFragment: Fragment(), TaxiContract.DriversView {

    var driversList = ArrayList<DriversFragment.Driver>()
    var parentActivity: TaxiActivity? = null
    var presenter: TaxiPresenter? = null
    var currentView: View? = null
    private var adapter: DriversFragment.DriversAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity.title = "Водители"
        parentActivity = activity as TaxiActivity
        presenter = TaxiPresenter(parentActivity!!, this)
        currentView = inflater!!.inflate(R.layout.fragment_drivers, container, false)
        presenter!!.loadDrivers(true)
        adapter = DriversAdapter(driversList)
        currentView!!.recycler.adapter = adapter
        currentView!!.recycler.layoutManager = LinearLayoutManager(activity)
        currentView!!.drivers_pullToRefresh.setOnRefreshListener {
            presenter!!.loadDrivers(false)
        }
        return currentView!!
    }
    private class DriversAdapter(driversList: List<Driver>) : RecyclerView.Adapter<DriversAdapter.DriversViewHolder>() {

        private var mDriversList: List<Driver> = driversList

        fun setItems(drivers: List<Driver>) {
            this.mDriversList = drivers
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DriversViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.driver_item, viewGroup, false)
            return DriversViewHolder(v)
        }


        override fun onBindViewHolder(viewHolder: DriversViewHolder, i: Int) {
            YoYo.with(Techniques.FadeInDown).duration(300).playOn(viewHolder.cardView)
            val driver = mDriversList[i]
            viewHolder.driverName!!.text = driver.name
            viewHolder.driverCar!!.text = driver.carModel
            if (driver.avatar != "avatar") {
                val image_data = Base64.decode(driver.avatar, Base64.NO_WRAP)
                val options = BitmapFactory.Options()
                options.outHeight = 128
                options.outWidth = 128
                options.outMimeType = "image/jpeg"
                viewHolder.driverImage!!.setImageBitmap(BitmapFactory.decodeByteArray(image_data, 0, image_data.size, options))
            }
        }

        override fun getItemCount() = mDriversList.count()


        class DriversViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var driverImage: CircularImageView? = null
            var driverName: TextView? = null
            var driverCar: TextView? = null
            var cardView: CardView? = null

            init {
                driverImage = itemView.findViewById(R.id.driver_image) as CircularImageView
                driverName = itemView.findViewById(R.id.driver_name) as TextView
                driverCar = itemView.findViewById(R.id.driver_car) as TextView
                cardView = itemView.findViewById(R.id.driver_cardView) as CardView
            }


        }

    }
    class Driver(val name: String, val avatar: String, val carModel: String, val carNumber: String, val mobileNumber: String, val email: String)

    override fun onDriversLoadSuccess(drivers: ArrayList<Driver>) {
        parentActivity!!.hideProgress()
        adapter!!.setItems(drivers)
        adapter!!.notifyDataSetChanged()
        currentView!!.drivers_pullToRefresh.isRefreshing = false
    }
}
