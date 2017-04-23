package io.dev.taxi.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.dev.taxi.R
import io.dev.taxi.data.models.Trip

class TripsAdapter(tripsList: List<Trip>) : RecyclerView.Adapter<TripsAdapter.TripsViewHolder>() {

    private var mTripsList: List<Trip> = tripsList

    fun setItems(trips: List<Trip>) {
        mTripsList = trips
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TripsViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.trip_item, viewGroup, false)
        return TripsViewHolder(v)
    }


    override fun onBindViewHolder(holder: TripsViewHolder, i: Int) {
        val trip = mTripsList[i]
        holder.tripDeparture.text = trip.departure
        holder.tripDestination.text = trip.destination
        holder.tripCost.text = trip.cost
    }

    override fun getItemCount() = mTripsList.count()


    class TripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tripDeparture: TextView = itemView.findViewById(R.id.trip_departure) as TextView
        var tripDestination: TextView = itemView.findViewById(R.id.trip_destination) as TextView
        var tripCost: TextView = itemView.findViewById(R.id.trip_cost) as TextView
    }

}

