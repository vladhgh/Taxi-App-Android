package io.dev.taxi.adapters

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import io.dev.taxi.R
import io.dev.taxi.data.models.Driver
import io.dev.taxi.fragments.DriversFragment

class DriversAdapter(driversList: List<Driver>) : RecyclerView.Adapter<DriversAdapter.DriversViewHolder>() {

    private var mDriversList: List<Driver> = driversList
    private lateinit var mItemsOnClickListener: OnItemClickListener

    fun setItems(drivers: List<Driver>) {
        this.mDriversList = drivers
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DriversViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.driver_item, viewGroup, false)
        return DriversViewHolder(v)
    }


    override fun onBindViewHolder(holder: DriversViewHolder, i: Int) {
        val driver = mDriversList[i]
        holder.driverName.text = driver.name
        holder.driverCar.text = driver.carModel
        if (driver.avatar != "avatar") {
            val image_data = Base64.decode(driver.avatar, Base64.NO_WRAP)
            val options = BitmapFactory.Options()
            options.outHeight = 128
            options.outWidth = 128
            options.outMimeType = "image/jpeg"
            holder.driverImage.setImageBitmap(BitmapFactory.decodeByteArray(image_data, 0, image_data.size, options))
        }
        holder.callButton.setOnClickListener{
            mItemsOnClickListener.onPhoneClick(mDriversList[i])
        }
        holder.messageButton.setOnClickListener{
            mItemsOnClickListener.onMessageClick(mDriversList[i])
        }
    }

    override fun getItemCount() = mDriversList.count()

    interface OnItemClickListener {
        fun onPhoneClick(driver: Driver)
        fun onMessageClick(driver: Driver)
    }

    fun setItemsOnClickListener(onClickListener: OnItemClickListener){
        mItemsOnClickListener = onClickListener
    }


    class DriversViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverImage: CircularImageView = itemView.findViewById(R.id.driver_image) as CircularImageView
        var driverName: TextView = itemView.findViewById(R.id.driver_name) as TextView
        var driverCar: TextView = itemView.findViewById(R.id.driver_car) as TextView
        var callButton: Button = itemView.findViewById(R.id.driver_call) as Button
        var messageButton: Button = itemView.findViewById(R.id.driver_message) as Button
    }

}
