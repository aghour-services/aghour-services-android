package com.aghourservices.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.db.RealmConfiguration
import com.aghourservices.data.model.Firm
import com.aghourservices.data.model.Tag
import com.aghourservices.databinding.FragmentFirmsBinding
import com.aghourservices.ui.adapter.FirmsAdapter
import com.aghourservices.ui.adapter.TagsAdapter
import com.aghourservices.ui.viewModel.FirmsViewModel
import com.aghourservices.ui.viewModel.TagsViewModel
import com.aghourservices.utils.helper.Event

class FirmsFragment : BaseFragment() {
    private lateinit var firmsAdapter: FirmsAdapter
    private lateinit var tagsAdapter: TagsAdapter
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var tagsList: ArrayList<Tag>
    private lateinit var binding: FragmentFirmsBinding
    private lateinit var firmsViewModel: FirmsViewModel
    private lateinit var tagsViewModel: TagsViewModel
    private val handler = Handler(Looper.getMainLooper()!!)
    private val args: CategoriesFragmentArgs by navArgs()
    private var selectedTags = ArrayList<String>()
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
        hideBottomNavigation()
        initViews()
        setupFirmsViewModel()
        setupTagsViewModel()
        refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigation()
    }


    private fun tagsAsParameter(): String {
        return if (selectedTags.isEmpty()) {
            return ""
        } else {
            selectedTags.joinToString(",")
        }.toString()
    }

    private fun setupTagsViewModel() {
        tagsViewModel = ViewModelProvider(this)[TagsViewModel::class.java]

        activity?.let { tagsViewModel.loadTags(it, categoryId) }

        tagsViewModel.tagsLiveData.observe(viewLifecycleOwner) {
            tagsList = it
            tagsAdapter = TagsAdapter(
                requireContext(),
                tagsList
            ) { v, position -> onTagsItemClick(v as CheckBox, position) }

            binding.apply {
                tagsRecyclerView.setHasFixedSize(true)
                tagsRecyclerView.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                tagsRecyclerView.adapter = tagsAdapter

                if (tagsList.isNotEmpty()) {
                    tagsRecyclerView.isVisible = true
                    lineView.isVisible = true
//                    smoothScrolling()
                }
                stopShimmerAnimation()
            }
        }
    }

//    private fun smoothScrolling() {
//        handler.postDelayed({
//            binding.tagsRecyclerView.smoothScrollToPosition(tagsList.size - 1)
//        }, 1000)
//
//        handler.postDelayed({
//            binding.tagsRecyclerView.smoothScrollToPosition(0)
//        }, 4000)
//    }

    private fun setupFirmsViewModel() {
        firmsViewModel = ViewModelProvider(this)[FirmsViewModel::class.java]

        activity?.let { firmsViewModel.loadFirms(it, categoryId, tagsAsParameter()) }

        firmsViewModel.firmsLiveData.observe(viewLifecycleOwner) {
            firmsList = it

            firmsAdapter = FirmsAdapter(requireContext(), it) { v, position ->
                onFirmsItemClick(v, position)
            }

            binding.apply {
                firmsRecyclerView.setHasFixedSize(true)

                firmsRecyclerView.layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

                firmsRecyclerView.adapter = firmsAdapter
            }
            if (firmsList.isEmpty()) {
                noInternetConnection()
            }
            stopShimmerAnimation()
        }
    }

    private fun initViews() {
        categoryId = args.categoryId
        val categoryName = args.categoryName
        requireActivity().title = categoryName
    }

    private fun refresh() {
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            handler.postDelayed({
                animationTagsLoading()
                binding.swipe.isRefreshing = false
                activity?.let { firmsViewModel.loadFirms(it, categoryId, tagsAsParameter()) }
            }, 1000)
        }
    }

    private fun noInternetConnection() {
        binding.apply {
            noInternet.isVisible = true
            tagsRecyclerView.isVisible = false
            firmsRecyclerView.isVisible = false
        }
    }

    private fun stopShimmerAnimation() {
        binding.apply {
            firmsShimmer.stopShimmer()
            tagsShimmer.stopShimmer()
            tagsShimmer.isVisible = false
            line.isVisible = false
            firmsShimmer.isVisible = false
            firmsRecyclerView.isVisible = true
        }
    }

    private fun onTagsItemClick(v: CheckBox, position: Int) {
        when (v.id) {
            R.id.tagTv -> loadTags(position)
        }
    }

    private fun onFirmsItemClick(v: View, position: Int) {
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

    private fun loadTags(position: Int) {
        animationTagsLoading()
        val tag = tagsList[position]

        if (!tag.isChecked) {
            selectedTags.add(tagsList[position].tag)
        } else {
            selectedTags.remove(tagsList[position].tag)
        }
        tag.isChecked = !tag.isChecked
        activity?.let { firmsViewModel.loadFirms(it, categoryId, tagsAsParameter()) }
    }

    private fun animationTagsLoading() {
        binding.apply {
            firmsShimmer.isVisible = true
            firmsShimmer.startShimmer()
        }
    }

    private fun updateFavorite(position: Int) {
        val realm = RealmConfiguration(requireContext()).realm
        var firm = firmsList[position]
        val name = firm.name
        if (!firm.isFavorite) {
            onSNACK(binding.firmsRecyclerView, "تم الإضافة الي المفضلة ❤")
        } else {
            onSNACK(binding.firmsRecyclerView, "تم الإزالة من المفضلة 😕")
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