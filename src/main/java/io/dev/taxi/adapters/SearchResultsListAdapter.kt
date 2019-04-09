package io.dev.taxi.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.arlib.floatingsearchview.util.Util
import io.dev.taxi.R

class SearchResultsListAdapter: RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder>() {

    private lateinit var mItemsOnClickListener: OnItemClickListener

    override fun getItemCount() = mDataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressSuggestion: Address = mDataSet[position]
        holder.mAddress.text = addressSuggestion.address

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView)
            mLastAnimatedItemPosition = position
        }
        holder.itemView.setOnClickListener {
            mItemsOnClickListener.onClick(mDataSet[position])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.search_results, parent, false)
        return ViewHolder(view)
    }

    private var mDataSet: List<Address> = ArrayList()

    private var mLastAnimatedItemPosition = -1

    interface OnItemClickListener {
        fun onClick(address: Address)
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var mAddress: TextView = itemView.findViewById(R.id.address) as TextView
    }

    fun swapData(mNewDataSet: List<Address>) {
        mDataSet = mNewDataSet
        notifyDataSetChanged()
    }

    fun clearData() {
        mDataSet = ArrayList<Address>()
        notifyDataSetChanged()
    }

    fun setItemsOnClickListener(onClickListener: OnItemClickListener){
        mItemsOnClickListener = onClickListener
    }

    fun animateItem(view: View) {
        view.translationY = Util.getScreenHeight(view.context as Activity).toFloat()
        view.animate()
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator(3f))
                .setDuration(700)
                .start()
    }
}

class Address(val address: String, val placeId: String?)