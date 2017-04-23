package io.dev.taxi.fragments

import android.app.Fragment
import android.content.BroadcastReceiver
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.dev.taxi.R
import kotlinx.android.synthetic.main.activity_taxi.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import com.arlib.floatingsearchview.FloatingSearchView
import io.dev.taxi.activities.TaxiActivity
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import io.dev.taxi.adapters.SearchResultsListAdapter
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import io.dev.taxi.adapters.Address
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class SearchFragment: Fragment(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mSearchView: FloatingSearchView
    private lateinit var mParentActivity: TaxiActivity
    private lateinit var mSearchResultsList: RecyclerView
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLatLngBounds: LatLngBounds
    private lateinit var mSearchResultsAdapter: SearchResultsListAdapter


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflatedView = inflater!!.inflate(R.layout.fragment_search, container, false)
        mParentActivity = activity as TaxiActivity
        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .build()
        mGoogleApiClient.connect()
        if (mParentActivity.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            val recent = mParentActivity.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            mLatLngBounds = toBounds(LatLng(recent.latitude, recent.longitude), 104.2)
        }
        inflatedView.floating_search_view.attachNavigationDrawerToMenuButton(activity.drawer_layout)
        return inflatedView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSearchView = view!!.floating_search_view
        mSearchResultsList = view.search_results_list as RecyclerView
        mSearchResultsAdapter = SearchResultsListAdapter()
        mSearchResultsList.adapter = mSearchResultsAdapter
        mSearchResultsList.layoutManager = LinearLayoutManager(activity)
        mSearchResultsAdapter.setItemsOnClickListener(object: SearchResultsListAdapter.OnItemClickListener {
            override fun onClick(address: Address) {
                mSearchView.clearFocus()
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, address.placeId).setResultCallback {
                    places ->
                    if (places.status.isSuccess && places.count > 0) {
                        val place = places.get(0)
                        mParentActivity.tripModel.destination = place.address.toString()
                        mParentActivity.orderTaxiForLocation(place.latLng)
                    } else {
                        // TODO: Handle failure
                    }
                    places.release()
                }
            }
        })
        mSearchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {
                view.parent_view.setBackgroundResource(android.R.color.transparent)
                view.results_view.visibility = View.VISIBLE
                mParentActivity.hideButtons()
            }

            override fun onFocusCleared() {
                view.parent_view.setBackgroundResource(android.R.color.transparent)
                view.results_view.visibility = View.GONE
                mParentActivity.showButtons()
            }
        })
        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                mSearchView.clearSuggestions()
            } else {
                mSearchView.showProgress()
            }
        }
        getPredictions()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("CONNECTION", "ERROR")
    }

    fun toBounds(center: LatLng, radius: Double): LatLngBounds {
        val southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225.0)
        val northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45.0)
        return LatLngBounds(southwest, northeast)
    }

    private fun predictionObservable(): Observable<String> {
        val subject = PublishSubject.create<String>()
        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            subject.onNext(newQuery)
        }
        return subject
    }
    private fun getPredictions() {
        predictionObservable()
                .doOnNext {
                    mSearchResultsAdapter.clearData()
                    mSearchView.showProgress()
                }
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map { data -> doQuery(data) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { predictionList ->
                    mSearchView.hideProgress()
                    mSearchResultsAdapter.swapData(predictionList)
                }
    }
    private fun doQuery(query: String): ArrayList<Address> {
        val predictionList = ArrayList<Address>()
        val typeFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build()
        val result: PendingResult<AutocompletePredictionBuffer> = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query, mLatLngBounds, typeFilter)
        val autocompletePredictions: AutocompletePredictionBuffer = result.await()
        if (autocompletePredictions.status.isSuccess) {
            val iterator = autocompletePredictions.iterator()
            while (iterator.hasNext()) {
                val prediction: AutocompletePrediction = iterator.next()
                predictionList.add(Address(prediction.getFullText(StyleSpan(Typeface.BOLD)).toString(), "5 минут до этого места, друг", prediction.placeId))
            }
        }
        autocompletePredictions.release()
        return predictionList
    }


}
