package io.dev.taxi.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.TextView
import com.arlib.floatingsearchview.util.Util
import io.dev.taxi.R
import io.dev.taxi.data.models.Trip

class TripsAdapter(tripsList: List<Trip>) : RecyclerView.Adapter<TripsAdapter.TripsViewHolder>() {

    private var mTripsList: List<Trip> = tripsList
    private var mLastAnimatedItemPosition = -1


    fun setItems(trips: List<Trip>) {
        mTripsList = trips
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TripsViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.trip_item, viewGroup, false)
        return TripsViewHolder(v)
    }


    override fun onBindViewHolder(holder: TripsViewHolder, i: Int) {
        val trip = mTripsList[i]
        holder.tripDeparture.hint = trip.departure
        holder.tripDeparture.isEnabled = false
        holder.tripDestination.hint = trip.destination
        holder.tripDestination.isEnabled = false
        holder.tripCost.text = trip.cost
        if(mLastAnimatedItemPosition < i){
            animateItem(holder.itemView)
            mLastAnimatedItemPosition = i
        }
    }

    override fun getItemCount() = mTripsList.count()

    fun animateItem(view: View) {
        view.translationY = Util.getScreenHeight(view.context as Activity).toFloat()
        view.animate()
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator(3f))
                .setDuration(700)
                .setStartDelay(200)
                .start()
    }


    class TripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tripDeparture: EditText = itemView.findViewById(R.id.trip_departure) as EditText
        var tripDestination: EditText = itemView.findViewById(R.id.trip_destination) as EditText
        var tripCost: TextView = itemView.findViewById(R.id.trip_cost) as TextView
    }

}

