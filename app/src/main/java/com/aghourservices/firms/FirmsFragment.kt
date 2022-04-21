package com.aghourservices.firms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.categories.CategoriesFragmentArgs
import com.aghourservices.constants.Constants.Companion.BASE_URL
import com.aghourservices.constants.RetrofitInstance
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

class FirmsFragment : BaseFragment() {
    private lateinit var adapter: FirmsAdapter
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var realm: Realm
    private lateinit var handler: Handler
    private lateinit var binding: FragmentFirmsBinding

    private val args: CategoriesFragmentArgs by navArgs()
    private var categoryId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav()
        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.hide()

        try {
            init()
            loadFirms(categoryId)
            refresh()
        } catch (e: Exception) {
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
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
        categoryId = args.categoryId
        val categoryName = args.categoryName
        val toolbarTv = binding.toolBarTv
        toolbarTv.text = categoryName
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
        val retrofitBuilder = RetrofitInstance.retrofit.create(ListFirms::class.java)
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
                            val firm = realm.where(Firm::class.java).equalTo("id", i.id).findFirst()
                            if (firm != null) {
                                i.isFavorite = firm.isFavorite
                            }

                            realm.createOrUpdateObjectFromJson(Firm::class.java, i.toJSONObject())
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
            adapter = FirmsAdapter(requireContext(), firmsList) { v, position ->
                onListItemClick(v, position)
            }
            binding.firmsRecyclerview.adapter = adapter
        } catch (e: Exception) {
        }
    }

    private fun onListItemClick(v: View, position: Int) {
        when (v.id) {
            R.id.btnFav -> updateFavorite(position)
            R.id.btnCall -> callPhone(position)
        }
    }

    private fun callPhone(position: Int) {
        val firm = firmsList[position]
        val phoneNumber = firm.phone_number
        val eventName = "call_${firm.name}"
        Event.sendFirebaseEvent(eventName, phoneNumber)
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    private fun updateFavorite(position: Int) {
        var firm = firmsList[position]
        val name = firm.name
        if (!firm.isFavorite) {
            onSNACK(binding.firmsRecyclerview, "تم الإضافة الي المفضلة ❤")
        } else {
            onSNACK(binding.firmsRecyclerview, "تم الإزالة من المفضلة 😕")
        }
        var eventName = "fav_${name}"
        if (firm.isFavorite) {
            eventName = "unFav_${name}"
        }
        Event.sendFirebaseEvent(eventName, name)
        realm.beginTransaction()
        firm.isFavorite = !firm.isFavorite
        firm = realm.createOrUpdateObjectFromJson(Firm::class.java, firm.toJSONObject())
        realm.copyToRealmOrUpdate(firm)
        realm.commitTransaction()
    }
}
