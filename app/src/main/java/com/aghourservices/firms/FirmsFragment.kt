package com.aghourservices.firms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.databinding.FragmentFirmsBinding
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.firms.api.ListFirms
import com.aghourservices.firms.ui.FirmsAdapter
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class FirmsFragment : BaseFragment() {
    private lateinit var adapter: FirmsAdapter
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var realm: Realm
    private lateinit var handler: Handler
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

        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.show()
        try {
            init()
            loadFirms(categoryId)
            refresh()
        } catch (e: Exception) {
            Log.e("Exception: ", e.message!!)
        }
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
    }

    private fun refresh() {
        try {
            handler = Handler(Looper.getMainLooper()!!)
            binding.swipe.setColorSchemeResources(R.color.swipeColor)
            binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
            binding.swipe.setOnRefreshListener {
                handler.postDelayed({
                    binding.swipe.isRefreshing = false
                    loadFirms(categoryId)
                }, 1000)
            }
        } catch (e: Exception) {
            Log.e("Exception: ", e.message!!)
        }
    }

    private fun loadFirms(categoryId: Int) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ListFirms::class.java)
        val retrofitData = retrofitBuilder.loadFirms(categoryId)
        retrofitData.enqueue(object : Callback<ArrayList<Firm>?> {
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
        try {
            adapter = FirmsAdapter(requireContext(), firmsList) { position ->
                onListItemClick(position)
            }
            binding.firmsRecyclerview.adapter = adapter
        } catch (e: Exception) {

        }
    }

    private fun onListItemClick(position: Int) {
        val firm = firmsList[position]
        val phoneNumber = firm.phone_number
        val eventName = "call_${firm.name}"
        Event.sendFirebaseEvent(eventName, phoneNumber)
        callPhone(phoneNumber)
    }

    private fun callPhone(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    override fun onBackPressed(): Boolean {
        val layoutManager = binding.firmsRecyclerview.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            super.onBackPressed()
            return false
        } else {
            binding.swipe.isRefreshing = true
            binding.firmsRecyclerview.smoothScrollToPosition(0)
            handler.postDelayed({
                loadFirms(categoryId)
                binding.swipe.isRefreshing = false
            }, 1000)
        }
        return true
    }
}