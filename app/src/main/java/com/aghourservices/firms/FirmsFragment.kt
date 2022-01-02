package com.aghourservices.firms

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.ads.Banner
import com.aghourservices.ads.Interstitial
import com.aghourservices.databinding.FragmentFirmsBinding
import com.aghourservices.firms.api.ListFirms
import com.aghourservices.firms.ui.FirmsAdapter
import com.google.android.gms.ads.AdView
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.firebase_analytics.Event
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class FirmsFragment : Fragment() {

    private lateinit var adapter: FirmsAdapter
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var realm: Realm
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var categoryId = 0
    private lateinit var binding: FragmentFirmsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        Realm.init(requireActivity())
        val config = RealmConfiguration
            .Builder()
            .allowWritesOnUiThread(true)
            .name("firm.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.getInstance(config)
        binding.firmsRecyclerview.setHasFixedSize(true)
        binding.firmsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        val bundle = arguments
        if (bundle != null) {
            categoryId = bundle.getInt("category_id", 0)
            val categoryName = bundle.getString("category_name")
            requireActivity().title = categoryName
        }

        runnable = Runnable { loadFirms(categoryId) }
        handler = Handler(Looper.myLooper()!!)
        handler.postDelayed(runnable, 0)
        binding.swipe.setColorSchemeResources(R.color.white)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.blue200)
        binding.swipe.setOnRefreshListener {
            Handler(Looper.myLooper()!!).postDelayed({
                binding.swipe.isRefreshing = false
                loadFirms(categoryId)
            }, 1000)
        }
    }


    private fun loadFirms(categoryId: Int) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ListFirms::class.java)
        val retrofitData = retrofitBuilder.loadFirms(categoryId)
        retrofitData.enqueue(object : Callback<ArrayList<Firm>?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<Firm>?>,
                response: Response<ArrayList<Firm>?>,
            ) {
                firmsList = response.body()!!
                realm.executeTransaction {
                    for (i in firmsList) {
                        try {
                            val firm = realm.createObject(Firm::class.java, i.id)
                            firm.name = i.name
                            firm.address = i.address
                            firm.description = i.description
                            firm.phone_number = i.phone_number
                            firm.category_id = i.category_id
                        } catch (e: Exception) {
                        }
                    }
                }
                setAdapter(firmsList)
                stopShimmerAnimation()
            }

            override fun onFailure(call: Call<ArrayList<Firm>?>, t: Throwable) {
                val result =
                    realm.where(Firm::class.java).equalTo("category_id", categoryId).findAll()
                firmsList = ArrayList()
                firmsList.addAll(result)

                setAdapter(firmsList)
                stopShimmerAnimation()

                if (firmsList.isEmpty()) {
                    noInternetConnection()
                }
            }
        })
    }

    fun noInternetConnection() {
        binding.noInternet.visibility = View.VISIBLE
        binding.firmsRecyclerview.visibility = View.GONE
    }

    private fun stopShimmerAnimation() {
        binding.firmsShimmer.stopShimmer()
        binding.firmsShimmer.visibility = View.GONE
        binding.firmsRecyclerview.visibility = View.VISIBLE
    }

    private fun setAdapter(firmsList: ArrayList<Firm>) {
        adapter = FirmsAdapter(requireContext(), firmsList) { position ->
            onListItemClick(position)
        }
        binding.firmsRecyclerview.adapter = adapter
    }

    private fun onListItemClick(position: Int) {
        val firm = firmsList[position]
        val phoneNumber = firm.phone_number
        var eventName = "call_${firm.name}"
        Event.sendFirebaseEvent(eventName, phoneNumber)
        callPhone(phoneNumber)
    }

    private fun callPhone(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }
}