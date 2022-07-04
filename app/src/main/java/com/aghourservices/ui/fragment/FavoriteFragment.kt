package com.aghourservices.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FragmentFavoriteBinding
import com.aghourservices.ui.adapter.FirmsAdapter
import com.aghourservices.utils.helper.Event
import io.realm.Realm
import io.realm.RealmConfiguration

class FavoriteFragment : BaseFragment() {
    private lateinit var realm: Realm
    lateinit var binding: FragmentFavoriteBinding
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var adapter: FirmsAdapter
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        load()
        refresh()
    }

    private fun refresh() {
        handler = Handler(Looper.getMainLooper()!!)
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            handler.postDelayed({
                binding.swipe.isRefreshing = false
                load()
            }, 1000)
        }
    }

    private fun init() {
        Realm.init(requireActivity())
        val config = RealmConfiguration
            .Builder()
            .allowWritesOnUiThread(true)
            .name("offline.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.getInstance(config)


        requireActivity().title = getString(R.string.favorite_fragment)
        binding.favoriteRecyclerView.setHasFixedSize(true)
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun setAdapter(firmsList: ArrayList<Firm>) {
        try {
            adapter = FirmsAdapter(requireContext(), firmsList) { v, position ->
                onListItemClick(v, position)
            }
            binding.favoriteRecyclerView.adapter = adapter
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
            onSNACK(binding.favoriteRecyclerView, "تم الإضافة الي المفضلة ❤")
        } else {
            onSNACK(binding.favoriteRecyclerView, "تم الإزالة من المفضلة 🙁")
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

    private fun load() {
        val result = realm.where(Firm::class.java).equalTo("isFavorite", true).findAll()
        firmsList = ArrayList()
        firmsList.addAll(result)
        setAdapter(firmsList)
        if (firmsList.isEmpty()) {
            binding.noFavItems.visibility = View.VISIBLE
        }
    }
}