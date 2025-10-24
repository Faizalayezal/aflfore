package com.foreverinlove.screen.fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.Constant
import com.foreverinlove.OnDiscoverFilterListener
import com.foreverinlove.R
import com.foreverinlove.adapter.CardViewAdapter
import com.foreverinlove.adapter.SubScriptionAdapter
import com.foreverinlove.adapter.SubsType
import com.foreverinlove.chatmodual.BaseFragment
import com.foreverinlove.databinding.FragmentNewDiscoverBinding
import com.foreverinlove.helpers.GpsLocationHelper
import com.foreverinlove.network.Utility.hideProgressBar
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.DiscoverData
import com.foreverinlove.network.response.DiscoverTopData
import com.foreverinlove.network.response.SubscriptionPlanItem
import com.foreverinlove.objects.DiscoverFilterObject
import com.foreverinlove.objects.SubscriptionList
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.screen.activity.DetailProfileScreenActivity
import com.foreverinlove.screen.activity.NewMatchingAlgorithmActivity
import com.foreverinlove.screen.activity.NotifitcationActivity
import com.foreverinlove.screen.activity.SubscriptionPlanActivity
import com.foreverinlove.screen.activity.SubscriptonPlan2Activity
import com.foreverinlove.screen.activity.SuperLikeActivity
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.FragmentExt.loadFragment
import com.foreverinlove.utility.dataStoreGetFilterIntent
import com.foreverinlove.utility.dataStoreGetGroupPopup
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetFilterIntent
import com.foreverinlove.utility.dataStoreSetGroupPopup
import com.foreverinlove.utility.dataStoreSetUserData
import com.foreverinlove.viewmodels.DiscoverViewModel
import com.foreverinlove.viewmodels.SubscriptionPlanListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


private const val TAG = "NewDiscoverFragment"

@AndroidEntryPoint
class NewDiscoverFragment : BaseFragment(R.layout.fragment_new_discover) {
    private var tempUserDataObject: TempUserDataObject? = null
    private var datas: DiscoverFilterObject? = null
    private lateinit var cardViewAdapter: CardViewAdapter
    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private var listTop = ArrayList<DiscoverData>()
    private var tempListTop = ArrayList<DiscoverData>()
    private var orderData: DiscoverTopData? = null

    private val viewModel: DiscoverViewModel by viewModels()
    private val subscribeViewmodel: SubscriptionPlanListViewModel by viewModels()

    private var isComplete = false
    private var isFilterLocationSelected = false
    private var binder: FragmentNewDiscoverBinding? = null
    private var locationManager: LocationManager? = null

    private var freePlanData: SubscriptionPlanItem? = null
    private var trialPlanData: SubscriptionPlanItem? = null
    private var isApplay: String? = null

    private lateinit var firabseauth: FirebaseAuth


    private val discoverObj: DiscoverFilterObject by lazy {
        val obj = DiscoverFilterObject()
        obj.min_age = "18"
        obj.max_age = "70"
        obj.minDistance = "0"
        obj.maxDistance = "100"
        obj.minHeight = "1"
        obj.maxHeight = "32"
        //obj.userLookingFor = "female"
        obj.latitude = ""
        obj.longitude = ""
        obj.page = "1"
        obj.pageSize = "5"
        obj
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder = FragmentNewDiscoverBinding.bind(view)
        //to prevent user from moving to other screens
        screenOpened("DiscoverTab")
        requireActivity().showProgressBar()



        lifecycleScope.launch {
            requireActivity().dataStoreGetFilterIntent().collect {
                Log.d(TAG, "onViewCreated123: " + it)
                datas = it
                isApplay = it.isApplyFilter

            }
        }

        // anonymusAuth()

        lifecycleScope.launch {

            activity?.dataStoreGetGroupPopup()?.firstOrNull()?.let {
                Log.d(TAG, "onViewCreated13456: " + it)
                if (it == "yes") {
                    //  launch { activity?.dataStoreSetGroupPopup("yes") }.join()
                    delay(1000)
                    setList()
                    // anonymusAuth()
                    binder?.discovershow?.visibility = View.GONE
                    binder?.popUp?.shows?.visibility = View.VISIBLE
                    activity?.dataStoreSetGroupPopup("No")
                } else {
                    binder?.discovershow?.visibility = View.VISIBLE
                    binder?.popUp?.shows?.visibility = View.GONE


                }
            }
            /* if (ISFirstTimeOpen) {
                 setList()
                 binder?.discovershow?.visibility = View.GONE
                 binder?.popUp?.shows?.visibility = View.VISIBLE
                 delay(5000)
                 ISFirstTimeOpen = false
             } else {
                 binder?.discovershow?.visibility = View.VISIBLE
                 binder?.popUp?.shows?.visibility = View.GONE
             }*/
        }

        binder?.popUp?.imgBack?.setOnClickListener {
            binder?.discovershow?.visibility = View.VISIBLE
            binder?.popUp?.shows?.visibility = View.GONE

        }

        binder?.popUp?.btnfreetirl?.setOnClickListener {
            subscribeViewmodel.getFreePlan()

        }
        binder?.popUp?.btnBrowsePlan?.setOnClickListener {

            startActivity(
                Intent(requireContext(), SubscriptonPlan2Activity::class.java)
            )
        }


        subscribeViewmodel.getCurrentUserPlan()


        lifecycleScope.launch {

            viewModel.discoverUserListConversion.collect {
                when (it) {
                    DiscoverViewModel.DiscoverUserListEvent.Empty -> {

                        isComplete = false
                        binder?.cardStackView?.visibility = View.VISIBLE
                    }

                    is DiscoverViewModel.DiscoverUserListEvent.Failure -> {
                        Toast.makeText(requireContext(), it.errorText, Toast.LENGTH_LONG).show()
                        isComplete = false
                        binder?.cardStackView?.visibility = View.VISIBLE
                        hideProgressBar()
                    }

                    DiscoverViewModel.DiscoverUserListEvent.Loading -> {
                        requireActivity().showProgressBar()
                        isComplete = false
                        binder?.cardStackView?.visibility = View.GONE

                    }

                    is DiscoverViewModel.DiscoverUserListEvent.Success -> {
                        hideProgressBar()


                        isComplete = false
                        binder?.cardStackView?.visibility = View.VISIBLE
                        binder?.btnsdiscover?.visibility = View.VISIBLE

                        if (!it.result.data?.users.isNullOrEmpty()) {
                            prepareCardView(
                                it.result.data?.users ?: listOf(),
                                it.result.data?.user_settings?.distance_unit
                            )
                            it.result.data.let { ord ->


                                orderData = ord

                                isFilterLocationSelected = !ord?.params?.latitude.isNullOrEmpty() &&
                                        !ord?.params?.longitude.isNullOrEmpty()

                                discoverObj.distanceUnit = ord?.user_settings?.distance_unit
                                discoverObj.min_age = ord?.params?.min_age
                                discoverObj.max_age = ord?.params?.max_age
                                discoverObj.address = ord?.params?.address
                                discoverObj.latitude = ord?.params?.latitude
                                discoverObj.longitude = ord?.params?.longitude
                                discoverObj.page = ord?.params?.page
                                discoverObj.pageSize = ord?.params?.pageSize
                                discoverObj.education = ord?.params?.education
                                discoverObj.minDistance = ord?.params?.min_distance
                                discoverObj.maxDistance = ord?.params?.max_distance
                                discoverObj.arts = ord?.params?.arts
                                discoverObj.covidVaccine = ord?.params?.covid_vaccine
                                discoverObj.dietaryLifestyle = ord?.params?.dietary_lifestyle
                                discoverObj.drink = ord?.params?.drink
                                discoverObj.drugs = ord?.params?.drugs
                                discoverObj.firstDateIceBreaker =
                                    ord?.params?.first_date_ice_breaker
                                discoverObj.horoscope = ord?.params?.horoscope
                                discoverObj.interests = ord?.params?.interests
                                discoverObj.language = ord?.params?.language
                                //discoverObj.userLookingFor = ord.params?.user_looking_for
                                discoverObj.listedLookingFor = ord?.params?.looking_for
                                discoverObj.pets = ord?.params?.pets
                                discoverObj.politicalLeaning = ord?.params?.political_leaning
                                discoverObj.relationshipStatus = ord?.params?.relationship_status
                                discoverObj.religion = ord?.params?.religion
                                discoverObj.smoking = ord?.params?.smoking
                                discoverObj.minHeight = ord?.params?.min_height
                                discoverObj.maxHeight = ord?.params?.max_height
                                discoverObj.hobbies = ord?.params?.hobbies


                            }
                        } else if (it.result.data?.users.isNullOrEmpty()) {
                            binder?.disimage?.visibility = View.VISIBLE
                            binder?.cardStackView?.visibility = View.GONE
                            binder?.btnsdiscover?.visibility = View.GONE


                            orderData = it.result.data

                        } else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }
                        if (it.result.data?.order == null) {
                            lifecycleScope.launch {

                            }
                        }


                    }

                    DiscoverViewModel.DiscoverUserListEvent.LoadingNext -> Unit
                    is DiscoverViewModel.DiscoverUserListEvent.SuccessNext -> {
                        updateScreenForNextTimeDataCollected(it)
                    }
                }
            }


        }


        Glide.with(requireContext()).load(R.mipmap.discovermpty).into(binder!!.disphoto)



        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binder?.loactionAccess?.btnContinue?.setOnClickListener {

            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )


        }



        binder?.filter?.setOnClickListener {


            val filterFrag = FilterFragment()
            val bundle = Bundle()
            // bundle.putSerializable("discoverData", discoverObj)
            Log.d(TAG, "onViewCreated132456: " + datas)
            bundle.putSerializable("discoverData", datas)
            bundle.putSerializable("planData", orderData)
            filterFrag.arguments = bundle
            childFragmentManager.loadFragment(filterFrag, binder!!.frameLayout.id)

        }
        binder?.likebtn?.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            binder?.cardStackView?.swipe()
        }
        binder?.dislikebtn?.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            binder?.cardStackView?.swipe()
        }
        binder?.superlike?.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Top)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            binder?.cardStackView?.swipe()


        }
        binder?.sild?.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            binder?.cardStackView?.swipe()
        }
        binder?.notificaton?.setOnClickListener {
            startActivity(Intent(requireContext(), NotifitcationActivity::class.java))
        }


        lifecycleScope.launch {
            requireContext().dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject = it

                }
        }

        lifecycleScope.launch {
            viewModel.swipeConversion.collect {
                when (it) {
                    is DiscoverViewModel.SwipeEvent.Empty -> {

                    }

                    is DiscoverViewModel.SwipeEvent.Failure -> {
                        hideProgressBar()

                        requireActivity().showToast(it.errorText)
                    }

                    is DiscoverViewModel.SwipeEvent.Loading -> {
                        hideProgressBar()
                    }

                    is DiscoverViewModel.SwipeEvent.Success -> {
                        hideProgressBar()

                        it.result.data?.let { data ->
                            if ((data.match_status ?: "") == "match") {
                                requireActivity().showToast("You And ${data.match_user_name} Liked Each Other")
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        NewMatchingAlgorithmActivity::class.java
                                    ).putExtra("matchData", data)
                                )
                            }
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.reviewConversion.collect {
                when (it) {
                    is DiscoverViewModel.ReviewEvent.Empty -> {

                    }

                    is DiscoverViewModel.ReviewEvent.Failure -> {
                        hideProgressBar()

                        requireActivity().showToast(it.errorText)
                    }

                    is DiscoverViewModel.ReviewEvent.Loading -> {
                        hideProgressBar()
                    }

                    is DiscoverViewModel.ReviewEvent.Success -> {
                        hideProgressBar()
                    }
                }
            }
        }

        lifecycleScope.launch {
            subscribeViewmodel.subscriptionPlanReason.collect { it ->
                when (it) {
                    SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Empty -> {
                    }

                    is SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Failure -> {
                    }

                    SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Loading -> {
                    }

                    is SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Success -> {
                        if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        } else if (it.result.status == 1) {

                            it.result.data?.find { freePlan -> freePlan.title == "Free Membership" }
                                ?.let { data ->
                                    freePlanData = data
                                }
                            it.result.data?.find { trialPlan -> trialPlan.title == "Trial" }
                                ?.let { it ->
                                    trialPlanData = it
                                }


                        }

                    }
                }
            }
        }

        if (isGranted && !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requireActivity().showToast("Please Enable To GPS To Continue")

        }

        freePlanListner()

    }

    private val multiListener = object : CardViewAdapter.OnClick {
        override fun openDetail(data: DiscoverData) {
            val intent = Intent(requireContext(), DetailProfileScreenActivity::class.java)
            intent.putExtra("userdata", data)
            intent.putExtra("userSuperlike", orderData?.remaining_super_likes_count?.toInt())
            intent.putExtra("userLikes", orderData?.remaining_likes_count?.toInt())

            startForProfileeResult.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Constant.discoverListener = object : OnDiscoverFilterListener {

            override fun onFiltersSelected(data: DiscoverFilterObject) {
                isFilterLocationSelected = !data.latitude.isNullOrEmpty() &&
                        !data.longitude.isNullOrEmpty()

                discoverObj.min_age = data.min_age
                discoverObj.max_age = data.max_age
                discoverObj.address = data.address
                discoverObj.latitude = data.latitude
                discoverObj.longitude = data.longitude
                discoverObj.page = data.page
                discoverObj.pageSize = data.pageSize
                discoverObj.education = data.education
                discoverObj.minDistance = data.minDistance
                discoverObj.maxDistance = data.maxDistance
                discoverObj.arts = data.arts
                discoverObj.covidVaccine = data.covidVaccine
                discoverObj.dietaryLifestyle = data.dietaryLifestyle
                discoverObj.drink = data.drink
                discoverObj.drugs = data.drugs
                discoverObj.firstDateIceBreaker = data.firstDateIceBreaker
                discoverObj.horoscope = data.horoscope
                discoverObj.interests = data.interests
                discoverObj.language = data.language
                //discoverObj.userLookingFor = data.userLookingFor
                discoverObj.listedLookingFor = data.listedLookingFor
                discoverObj.pets = data.pets
                discoverObj.politicalLeaning = data.politicalLeaning
                discoverObj.relationshipStatus = data.relationshipStatus
                discoverObj.religion = data.religion
                discoverObj.smoking = data.smoking
                discoverObj.isApplyFilter = data.isApplyFilter
                discoverObj.minHeight = data.minHeight
                discoverObj.maxHeight = data.maxHeight
                discoverObj.hobbies = data.hobbies
                lifecycleScope.launch {
                    Log.d(TAG, "callApi132: " + "store data530" + data + "hgh")
                    requireContext().dataStoreSetFilterIntent(data)
                }
                if (data.isApplyFilter == "1") {

                    lifecycleScope.launch {
                        requireActivity().dataStoreGetFilterIntent().collect {
                            Log.d(TAG, "callApi132: " + 5)
                            viewModel.getDiscoverUserList(
                                discoverObj, tempUserDataObject?.token ?: "", 436
                            )
                            true
                        }
                    }

                } else {
                    lifecycleScope.launch {
                        activity?.dataStoreGetFilterIntent()?.firstOrNull {
                            Log.d(TAG, "callApi132: " + 6)
                            viewModel.getDiscoverUserList(
                                discoverObj, tempUserDataObject?.token ?: "", 436
                            )
                            true
                        }
                    }
                }
            }
        }

        if (!isGranted) {
            Constant.isDiscoverOpen = true
            hideProgressBar()
            binder?.loactionAccess?.loactionAccess?.visibility = View.VISIBLE
            binder?.discovershow?.visibility = View.GONE
            isGranted = true
        } else {
            binder?.loactionAccess?.loactionAccess?.visibility = View.GONE
            binder?.discovershow?.visibility = View.VISIBLE

        }

        if (isGranted && !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requireActivity().showToast("Please Enable To GPS To Continue")

        }

        if ((System.currentTimeMillis() - lastTimeResultCalled) < 2000) return

        Constant.isDiscoverOpen = true

        if (childFragmentManager.backStackEntryCount == 1) return

        if (currentLat != "" && currentLong != "") {

            lifecycleScope.launch {
                activity?.dataStoreGetUserData()?.firstOrNull {
                    Log.d(TAG, "callApi132: " + 7)

                    viewModel.getDiscoverUserList(discoverObj, it.token, 377)
                    true
                }
            }
        }

        if (permissionDeniedCount == 0) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private var gpsLocationHelper: GpsLocationHelper? = null
    private var isLocationReceiveFirstTime = true

    private var isGranted = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { list ->

        list.forEach {
            if (!it.value) isGranted = false
        }

        if (isGranted) {
            binder?.loactionAccess?.loactionAccess?.visibility = View.GONE
            binder?.discovershow?.visibility = View.VISIBLE

            gpsLocationHelper = GpsLocationHelper(requireActivity()) { lat, long ->
                currentLat = lat
                currentLong = long
                lifecycleScope.launch {
                    val userObj =
                        context?.dataStoreGetUserData()?.firstOrNull() ?: return@launch
                    context?.dataStoreSetUserData(
                        userObj.copy(latitude = lat, longitude = long))
                }
                Log.d(TAG, "locationbaidaf:637 "+lat)
                Log.d(TAG, "locationbaidaf:638 "+long)
                if (!isFilterLocationSelected) {
                    discoverObj.latitude = lat
                    discoverObj.longitude = long
                    if (isLocationReceiveFirstTime) {
                        /*if(ISFirstTimeSave) {
                            lifecycleScope.launch {
                                Log.d(TAG, "callApi132: " + "store data631" + discoverObj)
                                requireContext().dataStoreSetFilterIntent(discoverObj)
                            }
                            Log.d(TAG, "dfjhkfdjhdf: "+312)
                            ISFirstTimeSave=false
                        }*/

                        Log.d(TAG, ":sdghsdgds " + isApplay)
                        if (isApplay == "1") {
                            lifecycleScope.launch {
                                Log.d(TAG, "callApi132: " + 10)
                                requireActivity().dataStoreGetFilterIntent().collect {
                                    viewModel.getDiscoverUserList(
                                        it, tempUserDataObject?.token ?: "", 436
                                    )
                                    true
                                }
                            }

                        } else if (isApplay == "0" || isApplay.isNullOrEmpty()) {
                            lifecycleScope.launch {
                                Log.d(TAG, "callApi132: " + 15)
                                Log.d(TAG, "callApi132: " + "store data631" + discoverObj)
                                requireContext().dataStoreSetFilterIntent(discoverObj)
                                activity?.dataStoreGetFilterIntent()?.firstOrNull {
                                    viewModel.getDiscoverUserList(
                                        discoverObj,
                                        tempUserDataObject?.token ?: "",
                                        494
                                    )
                                    true
                                }
                            }

                        }

                    }
                } else if (isLocationReceiveFirstTime) {
                    lifecycleScope.launch {
                        Log.d(TAG, "callApi132: " + 3)

                        activity?.dataStoreGetUserData()?.firstOrNull {
                            viewModel.getDiscoverUserList(discoverObj, it.token, 507)
                            true
                        }

                    }
                }
                isLocationReceiveFirstTime = false
            }
        } else if (permissionDeniedCount > 0) {

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri: Uri = Uri.fromParts("package", "com.foreverinlove", null)
            intent.data = uri
            startActivity(intent)

        } else {
            hideProgressBar()
            binder?.loactionAccess?.loactionAccess?.visibility = View.VISIBLE
            binder?.discovershow?.visibility = View.GONE
            permissionDeniedCount += 1

        }
    }

    private var permissionDeniedCount = 0

    //lastTimeResultCalled=0L time aapelo chhe resume and like details mathi bey sathe call thatu tu
    private var lastTimeResultCalled = 0L
    private var currentLat = ""
    private var currentLong = ""
    private val startForProfileeResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d("TAG", "resultcode: " + it.resultCode)
            lastTimeResultCalled = System.currentTimeMillis()
            when (it.resultCode) {
                5 -> binder?.dislikebtn?.performClick()
                6 -> binder?.likebtn?.performClick()
                7 -> binder?.superlike?.performClick()
            }
        }

    private var currSize = 0

    private fun prepareCardView(list: List<DiscoverData>, distanceUnit: String?) {
        listTop.clear()
        tempListTop.clear()

        listTop.addAll(list)
        tempListTop.addAll(list)
        currSize = tempListTop.size

        val directions: MutableList<Direction> = ArrayList()
        directions.add(Direction.Left)
        directions.add(Direction.Right)
        directions.add(Direction.Bottom)
        directions.add(Direction.Top)

        listTop = calculateDistance(listTop, distanceUnit)

        cardViewAdapter = CardViewAdapter(requireActivity(), multiListener, listTop)
        cardStackLayoutManager = CardStackLayoutManager(activity, cardListener)
        cardStackLayoutManager.setStackFrom(StackFrom.None)
        cardStackLayoutManager.setVisibleCount(2)
        cardStackLayoutManager.setTranslationInterval(8f)
        cardStackLayoutManager.setDirections(directions)
        cardStackLayoutManager.setCanScrollHorizontal(true)
        cardStackLayoutManager.setCanScrollVertical(true)
        cardStackLayoutManager.setOverlayInterpolator(LinearInterpolator())
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        cardStackLayoutManager.setMaxDegree(-20.0f)


        binder?.cardStackView?.layoutManager = cardStackLayoutManager
        binder?.cardStackView?.adapter = cardViewAdapter
    }

    private fun calculateDistance(
        listTop: ArrayList<DiscoverData>,
        distanceUnit: String?
    ): ArrayList<DiscoverData> {

        listTop.forEachIndexed { index, discoverData ->
            val curLat = currentLat.toDoubleOrNull()
            val curLong = currentLong.toDoubleOrNull()


            val otherLat = discoverData.latitude?.toDoubleOrNull()
            val otherLong = discoverData.longitude?.toDoubleOrNull()


            val startPoint = Location("locationA")
            startPoint.latitude = curLat ?: 0.0
            startPoint.longitude = curLong ?: 0.0

            val endPoint = Location("locationA")
            endPoint.latitude = otherLat ?: 0.0
            endPoint.longitude = otherLong ?: 0.0

            val distance: Float = startPoint.distanceTo(endPoint)

            if (distanceUnit.equals("0", true) || distanceUnit.equals("Km", true)) {
                val calculatedKm = String.format("%.2f", (distance / 1000)).toDouble()
                listTop[index].calculatedDistance = "$calculatedKm Km away"
            } else if (distanceUnit.equals("1", true) || distanceUnit.equals("Mile", true)) {
                val calculatedMi = String.format("%.2f", (distance / 1609.34)).toDouble()
                listTop[index].calculatedDistance = "$calculatedMi Miles away"


            }
        }

        return listTop
    }


    private val cardListener = object : CardStackListener {
        override fun onCardDragging(direction: Direction?, ratio: Float) {

        }

        override fun onCardSwiped(direction: Direction?) {
            orderData?.remaining_likes_count
            var isLikeAllowed = false
            var isSuperLikeAllowed = false
            val firstData = tempListTop.firstOrNull()

            orderData?.remaining_super_likes_count?.let {

                if (direction == Direction.Top) {
                    orderData?.remaining_super_likes_count =
                        (orderData?.remaining_super_likes_count ?: 0) - 1

                    isSuperLikeAllowed = (orderData?.remaining_super_likes_count ?: 0) >= 0
                    //successfulApplied()

                }

            }

            orderData?.remaining_likes_count?.let {

                if (direction == Direction.Right) {
                    orderData?.remaining_likes_count =
                        (orderData?.remaining_likes_count ?: 0) - 1

                    isLikeAllowed = (orderData?.remaining_likes_count ?: 0) >= 0

                }

            }


            if (direction == Direction.Left) {

                viewModel.swipeProfile(
                    "nope",
                    (firstData?.id ?: 0).toString()
                )


            } else if (direction == Direction.Right) {


                if (isLikeAllowed) {
                    viewModel.swipeProfile(
                        "like",
                        (firstData?.id ?: 0).toString()
                    )
                } else {

                    lifecycleScope.launch {
                        delay(500)
                        binder?.cardStackView?.rewind()
                        delay(500)
                        successfulApplied()
                    }
                    return

                }


            } else if (direction == Direction.Top) {

                if (isSuperLikeAllowed) {
                    viewModel.swipeProfile(
                        "super_like",
                        (firstData?.id ?: 0).toString()
                    )
                } else {
                    startActivity(Intent(requireContext(), SuperLikeActivity::class.java))
                }
                return


            } else if (direction == Direction.Bottom) {
                viewModel.reviewProfile(
                    (firstData?.id ?: 0).toString()
                )


            }

            tempListTop.removeFirst()
            Log.d("TAG", "sdffsdfgewe:887 " + currSize)
            currSize -= 1
            if (currSize == 1) {
                Log.d("TAG", "sdffsdfgewe:890 " + currSize)

                viewModel.getNextPage()
            }

            if (tempListTop.isEmpty() || firstData == null) {
                binder?.disimage?.visibility = View.VISIBLE
                binder?.cardStackView?.visibility = View.GONE
                binder?.btnsdiscover?.visibility = View.GONE
            }


        }

        override fun onCardRewound() {

        }

        override fun onCardCanceled() {

        }

        override fun onCardAppeared(view: View?, position: Int) {

        }

        override fun onCardDisappeared(view: View?, position: Int) {

        }


    }


    private fun successfulApplied() {
        val dialog = Dialog(requireContext(), R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogdiscover)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.sucessaplaytransperent
                )
            )
        )



        Glide.with(requireContext()).load(R.mipmap.browseplansempty)
            .into(dialog.findViewById(R.id.imageView13))

        dialog.findViewById<AppCompatButton>(R.id.btnBrowsePlan).setOnClickListener {
            startActivity(Intent(requireContext(), SubscriptionPlanActivity::class.java))
            dialog.dismiss()
        }

        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()

    }


    //screen update mate new data profile aave a mate
    private fun updateScreenForNextTimeDataCollected(it: DiscoverViewModel.DiscoverUserListEvent.SuccessNext) {
        if (it.result.status == -2) requireActivity().handleSessionExpired()
        else {
            isComplete = false

            it.result.data?.let { ord ->
                orderData = ord

            }

            if (!it.result.data?.users.isNullOrEmpty()) {
                tempListTop.addAll(it.result.data?.users ?: listOf())
                tempListTop = ArrayList(tempListTop.distinct())
                currSize = tempListTop.size

                listTop.clear()
                listTop.addAll(tempListTop)

                listTop = calculateDistance(listTop, it.result.data?.user_settings?.distance_unit)
                cardViewAdapter.addItems(listTop)
            }
            //(true)
        }
    }


    override fun onPause() {
        super.onPause()
        Constant.isDiscoverOpen = false
        Log.d(TAG, "onPause: testflowPAUSED${childFragmentManager.backStackEntryCount}")

        if (childFragmentManager.backStackEntryCount == 0)
            Constant.discoverListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressBar()
        binder = null
    }


    private fun setList() {
        val adapterList = ArrayList<SubscriptionList>()

        adapterList.add(
            SubscriptionList(
                true,
                true,
                getCustomString(
                    "Unlimited Likes & \n" +
                            "Dislikes ",
                    "",
                    "",
                    R.color.black
                )
            )
        )
        adapterList.add(
            SubscriptionList(
                true,
                true,
                getCustomString(
                    "Super Likes Per Week",
                    "",
                    "",
                    R.color.black
                )
            )
        )
        adapterList.add(
            SubscriptionList(
                true,
                true,
                getCustomString(
                    "My Likes",
                    "",
                    "",
                    R.color.black
                )
            )
        )

        adapterList.add(
            SubscriptionList(
                true,
                true,
                getCustomString(
                    "My Saved Likes",
                    " ",
                    "",
                    R.color.black
                )
            )
        )

        adapterList.add(
            SubscriptionList(
                subsymbol = true,
                presubsymbol = true,
                desc1 = getCustomString(
                    "InApp Chat & \n" +
                            "Messaging ",
                    " ",
                    "",
                    R.color.black
                )
            )
        )

        adapterList.add(
            SubscriptionList(
                false,
                true,
                getCustomString(
                    "Advanced Search Filters",
                    " ",
                    "",
                    R.color.black
                )
            )
        )

        adapterList.add(
            SubscriptionList(
                false,
                true,
                getCustomString(
                    "Who Has Viewed Me",
                    " ",
                    "",
                    R.color.black
                )
            )
        )
        //(it?.profile_views_limit?:0).toString()
        adapterList.add(
            SubscriptionList(
                false,
                true,
                getCustomString(
                    "Who Likes Me",
                    "",
                    "",
                    R.color.black
                )
            )
        )
        adapterList.add(
            SubscriptionList(
                false,
                true,
                getCustomString(
                    "InApp Live Video Chat",
                    "",
                    "",
                    R.color.black
                )
            )
        )
        adapterList.add(
            SubscriptionList(
                false,
                true,
                getCustomString(
                    "Virtual Social Hours/ \n" +
                            "Group Video Calls",
                    "",
                    "",
                    R.color.black
                )
            )
        )
        adapterList.add(
            SubscriptionList(
                false,
                true,
                getCustomString(
                    "Send Private Chat \n" +
                            "Request From Group \n" +
                            "Video Calls",
                    "",
                    "",
                    R.color.black
                )
            )
        )
        subsctiptplan(
            listData = adapterList,
            SubsType.SubscriptionDesign
        )


    }

    private fun subsctiptplan(listData: ArrayList<SubscriptionList>, type: SubsType) {
        binder?.popUp?.subrecy.apply {
            this?.adapter = SubScriptionAdapter(requireActivity(), listData, type)

        }
    }

    private fun getCustomString(
        desc1: String,
        desc2: String,
        desc3: String,
        color: Int
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        if (desc1 != "") {
            val redSpannable1 = SpannableString(desc1)
            redSpannable1.setSpan(ForegroundColorSpan(Color.BLACK), 0, desc1.length, 0)
            builder.append(redSpannable1)
        }

        if (desc2 != "") {
            val redSpannable1 = SpannableString(desc2)
            redSpannable1.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), color)),
                0,
                desc2.length,
                0
            )
            builder.append(redSpannable1)
        }
        if (desc3 != "") {
            val redSpannable1 = SpannableString(desc3)
            redSpannable1.setSpan(ForegroundColorSpan(Color.BLACK), 0, desc3.length, 0)
            builder.append(redSpannable1)
        }
        return builder
    }

    private fun freePlanListner() {
        lifecycleScope.launch {
            subscribeViewmodel.getFreePlanFlow.collect {
                when (it) {
                    is SubscriptionPlanListViewModel.GetFreePlan.Empty -> {
                        hideProgressBar()
                    }

                    is SubscriptionPlanListViewModel.GetFreePlan.Failure -> {

                    }

                    is SubscriptionPlanListViewModel.GetFreePlan.Loading -> {
                        requireActivity().showProgressBar()

                    }

                    is SubscriptionPlanListViewModel.GetFreePlan.Success -> {
                        hideProgressBar()
                        requireActivity().showToast(it.result.message)
                        if (it.result.status == 1) {


                        } else if (it.result.status == -2) {
                        }
                    }
                }
            }
        }

    }


}


