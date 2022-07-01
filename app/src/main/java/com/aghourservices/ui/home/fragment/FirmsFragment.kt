package com.aghourservices.ui.home.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Firm
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentFirmsBinding
import com.aghourservices.utils.helper.Event
import com.aghourservices.ui.app.main.fragment.BaseFragment
import com.aghourservices.ui.home.adapter.FirmsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirmsFragment : BaseFragment() {
    private lateinit var adapter: FirmsAdapter
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var binding: FragmentFirmsBinding
    private val handler = Handler(Looper.getMainLooper()!!)
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
        init()
        loadFirms(categoryId)
        refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.show()
    }

    private fun init() {
        binding.firmsRecyclerview.setHasFixedSize(true)
        binding.firmsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        categoryId = args.categoryId
        val categoryName = args.categoryName
        requireActivity().title = categoryName
    }

    private fun refresh() {
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            handler.postDelayed({
                binding.swipe.isRefreshing = false
                loadFirms(categoryId)
            }, 1000)
        }
    }

    private fun loadFirms(categoryId: Int) {
        val realm = com.aghourservices.data.db.RealmConfiguration(requireContext()).realm

        val retrofitBuilder =
            activity?.let { RetrofitInstance(it).retrofit.create(com.aghourservices.data.api.FirmApi::class.java) }
        val retrofitData = retrofitBuilder?.loadFirms(categoryId)
        retrofitData?.enqueue(object : Callback<ArrayList<Firm>?> {
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
        binding.firmsShimmer.isVisible = false
        binding.firmsRecyclerview.isVisible = true
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
        val realm = com.aghourservices.data.db.RealmConfiguration(requireContext()).realm
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