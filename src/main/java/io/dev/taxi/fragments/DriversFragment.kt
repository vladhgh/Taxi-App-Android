package io.dev.taxi.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import io.dev.taxi.R
import kotlinx.android.synthetic.main.fragment_drivers.view.*

class DriversFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val driversList = ArrayList<Driver>()
        driversList.add(Driver("Артем Склезнев", "ВАЗ 2109"))
        driversList.add(Driver("Влад Прокопьев", "ВАЗ 2114"))
        driversList.add(Driver("Рома Саттаров", "Лада Приора"))
        driversList.add(Driver("Лева Лазарев", "Alfa Romeo 156"))
        driversList.add(Driver("Игорь Захаров", "Daewoo Nexia"))
        driversList.add(Driver("Батя Темы", "Toyota Carina E"))
        driversList.add(Driver("Санжар Муталович", "Маршрутка"))

        val view = inflater!!.inflate(R.layout.fragment_drivers, container, false)
        val adapter = DriversAdapter(driversList)
        view.recycler.adapter = adapter
        view.recycler.layoutManager = LinearLayoutManager(activity)
        return view
    }
    private class DriversAdapter(driversList: List<Driver>) : RecyclerView.Adapter<DriversAdapter.DriversViewHolder>() {

        private var mDriversList: List<Driver> = driversList

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DriversViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.driver_item, viewGroup, false)
            return DriversViewHolder(v)
        }


        override fun onBindViewHolder(viewHolder: DriversViewHolder, i: Int) {
            val driver = mDriversList[i]
            viewHolder.driverName!!.text = driver.name
            viewHolder.driverCar!!.text = driver.car
        }

        override fun getItemCount() = mDriversList.count()


        class DriversViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            //var driverImage: CircularImageView? = null
            var driverName: TextView? = null
            var driverCar: TextView? = null

            init {
                //driverImage = itemView.findViewById(R.id.driver_image) as CircularImageView
                driverName = itemView.findViewById(R.id.driver_name) as TextView
                driverCar = itemView.findViewById(R.id.driver_car) as TextView
            }


        }

    }
    private class Driver(val name: String, val car: String)
}
