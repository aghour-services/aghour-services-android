package com.aghourservices.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FragmentFavoriteBinding
import com.aghourservices.ui.adapters.FirmsAdapter
import com.aghourservices.ui.base.BaseFragment
import com.aghourservices.utils.helper.Event
import io.realm.Realm

class FavoriteFragment : BaseFragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var favoriteAdapter: FirmsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.favorite_fragment)
        initRecyclerView()
        loadFavorites()
        showToolbar()
        showBottomNavigation()
    }

    private fun initRecyclerView() {
        binding.apply {
            favoriteRecyclerView.setHasFixedSize(true)
            favoriteRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun setAdapter(firmsList: ArrayList<Firm>) {
        favoriteAdapter = FirmsAdapter(requireContext(), firmsList) { v, position ->
            onListItemClick(v, position)
        }
        binding.favoriteRecyclerView.adapter = favoriteAdapter
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
        val realm = Realm.getDefaultInstance()
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
        realm?.beginTransaction()
        firm.isFavorite = !firm.isFavorite
        firm = realm!!.createOrUpdateObjectFromJson(Firm::class.java, firm.toJSONObject())
        realm.copyToRealmOrUpdate(firm)
        realm.commitTransaction()
        loadFavorites()
    }

    private fun loadFavorites() {
        val realm = Realm.getDefaultInstance()
        val result = realm?.where(Firm::class.java)?.equalTo("isFavorite", true)?.findAll()!!
        firmsList = ArrayList()
        firmsList.addAll(result)
        setAdapter(firmsList)
        if (firmsList.isEmpty()) {
            binding.noFavItems.isVisible = true
        }
    }
}