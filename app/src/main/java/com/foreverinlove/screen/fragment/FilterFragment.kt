package com.foreverinlove.screen.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.Constant
import com.foreverinlove.R
import com.foreverinlove.adapter.AddtionalAdepter
import com.foreverinlove.adapter.AddtionalFilterAdepter
import com.foreverinlove.adapter.PhaseType
import com.foreverinlove.chatmodual.BaseFragment
import com.foreverinlove.databinding.FragmentFilterBinding
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.dialog.HeightMaxMinDialog.openHeightMaxMinDialog
import com.foreverinlove.network.Utility.selectedIdd1
import com.foreverinlove.network.Utility.selectedIdd2
import com.foreverinlove.network.Utility.selectedIdd3
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.network.response.DiscoverTopData
import com.foreverinlove.objects.DiscoverFilterObject
import com.foreverinlove.objects.PhaseListObject
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.screen.activity.SubscriptionPlanActivity
import com.foreverinlove.utility.LocationPickerHelper
import com.foreverinlove.utility.MyListDataHelper
import com.foreverinlove.utility.dataStoreGetUserData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "FilterFragment"

@SuppressLint(
    "UseCompatLoadingForDrawables",
    "NotifyDataSetChanged",
    "ResourceType",
    "InflateParams"
)
@AndroidEntryPoint
class FilterFragment : BaseFragment(R.layout.fragment_filter), AddtionalAdepter.SelectedListener {

    private lateinit var binding: FragmentFilterBinding
    private val locationPickerHelper = LocationPickerHelper()
    var tempBhai: TempUserDataObject? = null


    private var latStr = ""
    private var longStr = ""

    private var rotet = true
    private var suggestions: Array<String> = arrayOf(
        "Cricket",
        "Football",
        "Socker",
        "Cock",
        "Basketball",
        "Drinking",
        "Reading",
        "Panging Jumaping"
    )

    private var suggestions2: Array<String> = arrayOf()
    private var isUserPaid = true

    private var mainDiscoverData: DiscoverFilterObject? = null
    private var planData: DiscoverTopData? = null
    private var tempDiscoverData: DiscoverFilterObject? = null

    var selectedLookingId1 = ""
    var selectedLookingId2 = ""
    var selectedLookingId3 = ""
    private lateinit var chip: Chip

    private val defaultStartDistance = "0"
    private val defaultEndDistance = "100"

    private val defaultStartAge = "18"
    private val defaultEndAge = "70"

    private val defaultStartHeight = "1"
    private val defaultEndHeight = "32"

    private val heightList: List<AddtionalQueObject> by lazy {
        MyListDataHelper.getAllData()?.height ?: listOf()
    }
    private var myDistance = "km"

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFilterBinding.bind(view)
        screenOpened("DiscoverFilter")
        locationPickerHelper.initialize(requireActivity())


        mainDiscoverData = arguments?.getSerializable("discoverData") as? DiscoverFilterObject
        planData = arguments?.getSerializable("planData") as? DiscoverTopData
        Log.d("TAG", "onViewCreatedfgfg: " + planData)
        Log.d("TAG", "onViewCreatedfsfsdfgfg: " + mainDiscoverData?.listedLookingFor)
        binding.edlocation.setText(mainDiscoverData?.address)
        binding.edFilterHeight.setText(mainDiscoverData?.address)




        if (planData?.order == null) {
            isUserPaid = false

            // binding.edlocation.isClickable=false
            binding.edlocation.isFocusable = false
            // binding.edlocation.isEnabled = false*/
            binding.edlooking.isFocusable = false
            binding.edrelation.isFocusable = false
            binding.btnAdtionalFilter.isFocusable = false


        } else {

            isUserPaid = true

            binding.edlocation.isClickable = true
            binding.edlocation.isFocusable = true
            binding.edlocation.isEnabled = true

            binding.edlooking.isClickable = true
            binding.edlooking.isFocusable = true
            binding.edlooking.isEnabled = true

            binding.edrelation.isClickable = true
            binding.edrelation.isFocusable = true
            binding.edrelation.isEnabled = true

            binding.btnAdtionalFilter.isClickable = true
            binding.btnAdtionalFilter.isFocusable = true
            binding.btnAdtionalFilter.isEnabled = true


            binding.edlocation.setCompoundDrawables(null, null, null, null)
            binding.edlooking.setCompoundDrawables(null, null, null, null)
            binding.edrelation.setCompoundDrawables(null, null, null, null)


        }

        tempDiscoverData = mainDiscoverData


        applyDefault(false)

        Log.d(TAG, "onViewCreated-->1: " + tempDiscoverData)
        Log.d(
            TAG,
            "onViewCreated2-->3: " + tempDiscoverData?.max_age + "--->" + tempDiscoverData?.min_age
        )
        lifecycleScope.launch {
            requireActivity().dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempBhai = it
                }
        }


        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        myDistance = mainDiscoverData?.distanceUnit ?: "km"
        Log.d("TAG", "onViewCreadfdfted: " + myDistance)
        if (planData?.user_settings?.distance_unit == "Km" || planData?.user_settings?.distance_visible == 0 || myDistance == "0") {
            myDistance = "Km"
        }
        if (planData?.user_settings?.distance_unit == "Mile") {
            myDistance = "Miles"

        }

        binding.rsbDistance.leftSeekBar.setIndicatorText("0$myDistance ")
        binding.rsbDistance.rightSeekBar.setIndicatorText(" 17$myDistance")

        binding.rsbDistance.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                tempDiscoverData?.minDistance = leftValue.toInt().toString()
                tempDiscoverData?.maxDistance = rightValue.toInt().toString()
                Log.d(TAG, "onRangeChanged201: " + tempDiscoverData?.minDistance)
                view?.leftSeekBar?.setIndicatorText(leftValue.toInt().toString() + " " + myDistance)
                view?.rightSeekBar?.setIndicatorText(
                    rightValue.toInt().toString() + " " + myDistance
                )

            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }
        })


        binding.rsbAge.leftSeekBar.setIndicatorText("18 ")
        binding.rsbAge.rightSeekBar.setIndicatorText("28 ")
        binding.rsbAge.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {

                //18
                //85
                val calculationLeft = (((67 * leftValue) / 100) + 18).toInt().toString()
                val calculationRight = (((67 * rightValue) / 100) + 18).toInt().toString()

                tempDiscoverData?.min_age = calculationLeft
                tempDiscoverData?.max_age = calculationRight

                view?.leftSeekBar?.setIndicatorText(calculationLeft + " Age")
                view?.rightSeekBar?.setIndicatorText(calculationRight + " Age")
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }
        })

        binding.resent.setOnClickListener {
            selectedIdd1 = ""
            selectedIdd2 = ""
            selectedIdd3 = ""

            binding.rsbDistance.setProgress(0f, 17f).toString()
            binding.rsbAge.setProgress(0f, 15f).toString()
            // binding.rsbHeight.setProgress(0f, 15f).toString()
            CoroutineScope(Dispatchers.IO).launch {
                requireActivity().dataStoreGetUserData()
                    .catch { it.printStackTrace() }
                    .collect {
                        tempBhai = it
                    }
            }
            Log.d(
                TAG,
                "onViewCreated123456: 256 " + tempBhai?.latitude + "--" + tempBhai?.longitude
            )
            Log.d(
                TAG,
                "onViewCreated123456: 259" + tempDiscoverData?.latitude + "--->" + tempDiscoverData?.longitude
            )


            applyDefault(true)

            tempDiscoverData?.minDistance = "0"
            tempDiscoverData?.maxDistance = "100"
            tempDiscoverData?.min_age = "18"
            tempDiscoverData?.max_age = "70"
            tempDiscoverData?.minHeight = "1"
            tempDiscoverData?.maxHeight = "32"
            tempDiscoverData?.isApplyFilter = "0"
            tempDiscoverData?.latitude = tempDiscoverData?.latitude
            tempDiscoverData?.longitude = tempDiscoverData?.longitude

            tempDiscoverData?.let {

                Log.d(TAG, "onViewCreated123456:285 " + it)
                Log.d(TAG, "onViewCreated123456:287 " + tempDiscoverData?.longitude)
                Log.d(TAG, "onViewCreated123456:288 " + tempDiscoverData?.longitude)

                if (tempDiscoverData?.latitude != "" && tempDiscoverData?.longitude != "") {
                    it.latitude = tempDiscoverData?.latitude
                    it.longitude = tempDiscoverData?.longitude
                }

                Constant.discoverListener?.onFiltersSelected(it)
            }
            requireActivity().onBackPressed()
        }

        binding.btnApplyFilter.setOnClickListener {

            Log.d(TAG, "onViewCresfddated: " + selectedIdd1)
            Log.d(TAG, "onViewCresfddated: " + selectedIdd2)
            Log.d(TAG, "onViewCresfddated: " + selectedIdd3)

            tempDiscoverData?.let {
                it.isApplyFilter = "1"

                Log.d(TAG, "onViewCreated132: " + longStr)
                Log.d(TAG, "onViewCreated1323: " + it.latitude)
                Log.d(TAG, "onViewCreated1323: " + it.longitude)

                if (latStr != "" && longStr != "") {
                    it.latitude = latStr
                    it.longitude = longStr
                }

                Constant.discoverListener?.onFiltersSelected(it)
            }
            requireActivity().onBackPressed()

            // sucessfullApplyed()
        }

        val adapterList = ArrayList<PhaseListObject>()

        MyListDataHelper.getAllData()?.let {
            adapterList.add(
                PhaseListObject(
                    "Smoking",
                    it.smoking,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.smoking ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Drinking",
                    it.drink,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.drink ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Drugs",
                    it.drugs,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.drugs ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Dietary Lifestyle",
                    it.dietary_life_style,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.dietaryLifestyle ?: "",
                    4
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Interests",
                    it.interests,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.interests ?: "",
                    4
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Pets",
                    it.pets,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.pets ?: "",
                    4
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Horoscope",
                    it.horoscope,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.horoscope ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Political Views",
                    it.political_leaning,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.politicalLeaning ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Religion",
                    it.religion,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.religion ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Vaccinated",
                    it.covid_vaccine,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.covidVaccine ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "First Date Ice Breaker",
                    it.first_date_ice_breaker,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.firstDateIceBreaker ?: ""
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Art",
                    it.arts,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.arts ?: "",
                    4
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Education",
                    it.education,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.education ?: "",
                    4
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Language",
                    it.language,
                    ChipGroupHelper.StyleTypes.PhaseAddtional,
                    tempDiscoverData?.language ?: "",
                    4
                )
            )
        }


        Log.d(
            "TAG",
            "onViewCreated: test301>>" + tempDiscoverData?.relationshipStatus + ">>" + tempDiscoverData?.listedLookingFor
        )
        MyListDataHelper.getAllData()?.height?.find {
            it.id.toString() == tempDiscoverData?.minHeight.toString()

        }.let {
            binding.chip1.text = it?.title

        }

        MyListDataHelper.getAllData()?.height?.find {
            it.id.toString() == tempDiscoverData?.maxHeight.toString()

        }.let {
            binding.chip2.text = it?.title

        }

        // binding.chip2.text=tempDiscoverData?.maxHeight

        binding.edFilterHeight.setOnClickListener {
            requireActivity().openHeightMaxMinDialog(
                binding.chip1,
                binding.chip2,
                binding.edFilterHeight,
                tempDiscoverData
            )
        }

        if (planData?.order == null || !isUserPaid) {
            binding.edrelation.setOnClickListener {
                nav("Unlock Relationship Status")

            }

        } else {
            MyListDataHelper.getAllData()?.relationship_status?.let {
                setChipLayout(
                    binding.relationchip,
                    it,
                    2,
                    binding.edrelation,
                    "Relationship Status",
                    tempDiscoverData?.relationshipStatus ?: ""
                )
            }

        }

        Log.d(TAG, "onViewCreated312: " + tempDiscoverData?.listedLookingFor)


        if (planData?.order == null || !isUserPaid) {
            binding.edlooking.setOnClickListener {
                nav("Unlock Looking For")

            }

        } else {
            // hgjg
            MyListDataHelper.getAllData()?.looking_for?.let {
                setChipLayout2(
                    binding.lookingchip, it, 4,
                    binding.edlooking, "Looking For", tempDiscoverData?.listedLookingFor ?: ""
                )

            }
        }



        if (mainDiscoverData?.maxDistance == "0") {

            mainDiscoverData?.maxDistance = "17"
        }

        binding.rsbDistance.setProgress(
            mainDiscoverData?.minDistance?.toFloatOrNull() ?: 0f,
            mainDiscoverData?.maxDistance?.toFloatOrNull() ?: 17f
        ).toString()

        if ((mainDiscoverData?.min_age?.toIntOrNull() ?: 0) < 18) {

            mainDiscoverData?.min_age = "18"
        }
        if ((mainDiscoverData?.max_age?.toIntOrNull() ?: 0) < 35) {
            mainDiscoverData?.max_age = "35"

        }
        if ((mainDiscoverData?.max_age?.toIntOrNull() ?: 0) > 85) {
            mainDiscoverData?.max_age = "85"

        }
        val calculationLeft =
            ((((mainDiscoverData?.min_age?.toIntOrNull() ?: 18) - 18) * 100) / 67).toFloat()
        val calculationRight =
            ((((mainDiscoverData?.max_age?.toIntOrNull() ?: 18) - 18) * 100) / 67).toFloat()

        Log.d(
            TAG,
            "onViewCreatedage: " + mainDiscoverData?.min_age + "max" + mainDiscoverData?.max_age
        )

        binding.rsbAge.setProgress(calculationLeft, calculationRight).toString()


        val minHeight = mainDiscoverData?.minHeight
        val maxHeight = mainDiscoverData?.maxHeight


        val foundItem1 = heightList.find { it.id == minHeight?.toIntOrNull() }
        val heightMin = foundItem1?.title

        val foundItem2 = heightList.find { it.id == maxHeight?.toIntOrNull() }
        val heightMax = foundItem2?.title
        binding.edFilterHeight.setText("$heightMin $heightMax")

        noteData(adapterList)

        binding.btnAdtionalFilter.setOnClickListener {
            if (planData?.order == null || !isUserPaid) {
                nav("Unlock Additional Filters")

            } else {
                if (rotet) {
                    binding.recyAddtional.visibility = View.VISIBLE
                  //  binding.hobby.visibility = View.GONE
                    rotet = false


                } else {
                    binding.recyAddtional.visibility = View.GONE
                 //   binding.hobby.visibility = View.GONE

                    rotet = true

                }
            }


        }




       /* binding.etTag.addChipTerminator(';', BEHAVIOR_CHIPIFY_ALL)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions
        )
        binding.etTag.setAdapter(adapter)
        binding.etTag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val adapter11: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    suggestions2
                )
                binding.etTag.setAdapter(adapter11)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }*/

        binding.edlocation.setOnClickListener {
            Log.d(TAG, "onViewCdfdfdfreated: " + planData?.order + "pain==" + isUserPaid)
            if (planData?.order == null || !isUserPaid) {
                nav("Unlock Location")
            } else {
                locationPickerHelper.openLocationPicker(resultLauncher)
            }


        }


    }


    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        if (it.resultCode == AppCompatActivity.RESULT_OK) {

            locationPickerHelper.getDataFromResult(it) { address, lat, long ->
                latStr = lat
                longStr = long
                // val text = address
                //  val commaIndex = text.indexOf(',')
                // val clienAddress = text.substring(0, commaIndex)
                /* val newString = StringBuilder()
                 for (char in address) {
                     if (char != ',' || char != '-') {
                         newString.append(char)
                     }
                 }*/
                val newStr = address.replace(",", " ").replace("-", " ").replace(" ", " ")
                Log.d(TAG, "sdfsdfsdfsd:660 " + newStr)
                try{
                    val text = newStr
                    val commaIndex = newStr.indexOf(' ')
                    val clienAddress = text.substring(0, commaIndex)
                    Log.d(TAG, "sdfsdfsdfsd:6664 " + clienAddress)
                    binding.edlocation.setText(address)
                    mainDiscoverData?.address = address
                }catch (e: Exception) {
                    Log.d(TAG, "sdfsdfsdfsd:667 "+newStr)
                    binding.edlocation.setText(address)
                    mainDiscoverData?.address = address
                    e.printStackTrace()
                }


               /* binding.edlocation.setText(address)
                mainDiscoverData?.address = address*/

            }

        }

    }


    private fun applyDefault(forceApplyDefault: Boolean) {

        if (forceApplyDefault) {
            Log.d(TAG, "applyDefault123: " + "1")
            Log.d(TAG, "applyDefault123:66616  " + tempBhai?.longitude)
            Log.d(TAG, "applyDefault123:662  " + tempBhai?.latitude)
            tempDiscoverData?.apply {
                minDistance = defaultStartDistance
                maxDistance = defaultEndDistance
                min_age = defaultStartAge
                max_age = defaultEndAge
                minHeight = defaultStartHeight
                maxHeight = defaultEndHeight
                latitude = tempBhai?.latitude
                longitude = tempBhai?.longitude
                page = "1"
                pageSize = "5"
                education = ""
                arts = ""
                covidVaccine = ""
                dietaryLifestyle = ""
                drink = ""
                drugs = ""
                firstDateIceBreaker = ""
                horoscope = ""
                interests = ""
                language = ""
                //userLookingFor
                listedLookingFor = ""
                pets = ""
                politicalLeaning = ""
                relationshipStatus = ""
                religion = ""
                smoking = ""
                isApplyFilter = "0"
                hobbies = ""
                address = ""
            }
        } else {
            Log.d(TAG, "applyDefault123: " + "2")

            //setting screen default
            tempDiscoverData?.apply {
                if (minDistance.isNullOrEmpty()) minDistance = defaultStartDistance
                if (maxDistance.isNullOrEmpty()) maxDistance = defaultEndDistance
                if (min_age.isNullOrEmpty()) min_age = defaultStartAge
                if (max_age.isNullOrEmpty()) max_age = defaultEndAge
                if (minHeight.isNullOrEmpty()) minHeight = defaultStartHeight
                if (maxHeight.isNullOrEmpty()) maxHeight = defaultEndHeight
            }
        }
    }

    private fun setChipLayout(
        chipGroup: ChipGroup,
        listTop: List<AddtionalQueObject>,
        maxCount: Int,
        edlooking: EditText,
        title: String,
        selectedIds: String
    ) {

        val listener = object : ChipGroupHelper.ChipSelectedListener {
            override fun onSelectedListChange(list: ArrayList<String>) {
                when (title) {
                    "Relationship Status" -> tempDiscoverData?.relationshipStatus = ""
                    // "Looking For" -> tempDiscoverData?.listedLookingFor = ""
                }
                Log.d(TAG, "onSelectedListChange312: " + listTop)
                for (i in list.indices) {
                    for (j in listTop.indices) {
                        if (title == "Relationship Status") {
                            if (list[i] == listTop[j].title) {
                                tempDiscoverData?.relationshipStatus = listTop[j].id.toString()
                                if (tempDiscoverData?.relationshipStatus == "") list[i]
                                else tempDiscoverData?.relationshipStatus + "," + listTop[i].id
                            }
                        } /*else if (title == "Looking For") {
                            Log.d(
                                TAG,
                                "onSelectedListChange231: " + listTop[j].id + "---->" + listTop[i].id
                            )
                            Log.d(
                                TAG,
                                "onSelectedListChange231: " + listTop[j].id + "---->" + listTop[i].id
                            )
                            if (list[i] == listTop[j].title) {
                                tempDiscoverData?.listedLookingFor = listTop[j].id.toString()
                                if (tempDiscoverData?.listedLookingFor == "") list[i]
                                else tempDiscoverData?.listedLookingFor + "," + listTop[i].id
                            }
                        }*/
                    }
                }
            }
        }

        var tempSelectedId1 = ""
        var tempSelectedId2 = ""
        var tempSelectedId3 = ""
        selectedIds.split(",").let {
            tempSelectedId1 = it.getOrNull(0) ?: ""
            tempSelectedId2 = it.getOrNull(1) ?: ""
            tempSelectedId3 = it.getOrNull(2) ?: ""
        }


        ChipGroupHelper.Builder(requireActivity())
            .setChipLayout(chipGroup)
            .setSelectedListener(listener)
            .setMaxSelected(maxCount)
            .setList(ChipGroupHelper.ListType.ChildList(listTop))
            .setAlreadySelectedIds(tempSelectedId1, tempSelectedId2, tempSelectedId3)
            // .setAlreadySelectedIds(selectedIds)
            .setBottomSheet(false)
            .setClickable(false)
            .setCloseIconVisible(false)
            .setAllShowOnFirst(false)
            .setViewsForClickedNewInterestPicker(edlooking, title)
            .setStyleColor(ChipGroupHelper.StyleTypes.PhaseAddtional)
            .setRemoveListener(null)
            .build()
    }


    fun getLookingString(): String {
        var str = if (selectedIdd1 != "") selectedIdd1 else ""
        str += if (selectedIdd2 != "") ",$selectedIdd2" else ""
        str += if (selectedIdd3 != "") ",$selectedIdd3" else ""

        Log.d(TAG, "getLookingString: " + str)

        return str
    }

    private var myLookingForBuilder: ChipGroupHelper.Builder? = null

    private fun setChipLayout2(
        chipGroup: ChipGroup,
        listTop: List<AddtionalQueObject>,
        maxCount: Int,
        edlooking: EditText,
        title: String,
        selectedIds: String
    ) {

        val listener = object : ChipGroupHelper.ChipSelectedListener {
            override fun onSelectedListChange(list: ArrayList<String>) {
                when (title) {
                    "Looking For" -> tempDiscoverData?.listedLookingFor = ""
                }
                for (i in list.indices) {
                    for (j in listTop.indices) {
                        if (title == "Looking For") {
                            selectedLookingId1 = ""
                            selectedLookingId2 = ""
                            selectedLookingId3 = ""

                            for (i in list.indices) {
                                for (j in listTop.indices) {
                                    if (list[i] == listTop[j].title) {
                                        tempDiscoverData?.listedLookingFor =
                                            listTop[j].id.toString()
                                        if (tempDiscoverData?.listedLookingFor == "") list[i]
                                        else tempDiscoverData?.listedLookingFor + "," + listTop[i].id

                                        tempDiscoverData?.listedLookingFor = getLookingString()
                                    }
                                }
                            }

                            /* myLookingForBuilder?.setAlreadySelectedIds(
                                 selectedLookingId1,
                                 selectedLookingId2,
                                 selectedLookingId3
                             )*/

                            /* if (selectedLookingId1 == "") selectedLookingId1 =
                                 listTop[j].id.toString()
                             else if (selectedLookingId2 == "") selectedLookingId2 =
                                 listTop[j].id.toString()
                             else if (selectedLookingId3 == "") selectedLookingId3 =
                                 listTop[j].id.toString()
                             Log.d(TAG, "onSelectedListChangeasas:816 "+selectedLookingId1)
                             Log.d(TAG, "onSelectedListChangeasas:817 "+selectedLookingId2)
                             Log.d(TAG, "onSelectedListChangeasas:820 "+selectedLookingId3)

                            // tempDiscoverData?.listedLookingFor = getLookingString()

                             if (list[i] == listTop[j].title) {
                                  tempDiscoverData?.listedLookingFor = listTop[j].id.toString()
                                  if (tempDiscoverData?.listedLookingFor == "") list[i]
                                  else tempDiscoverData?.listedLookingFor + "," + listTop[i].id
                              }
                             myLookingForBuilder?.setAlreadySelectedIds(
                                 selectedLookingId1,
                                 selectedLookingId2,
                                 selectedLookingId3
                             )*/
                        }
                    }

                }
            }
        }
        var tempSelectedId1 = ""
        var tempSelectedId2 = ""
        var tempSelectedId3 = ""
        selectedIds.split(",").let {
            tempSelectedId1 = it.getOrNull(0) ?: ""
            tempSelectedId2 = it.getOrNull(1) ?: ""
            tempSelectedId3 = it.getOrNull(2) ?: ""
        }

        /* selectedIds.split(",").let {
             Log.d(TAG, "setChipLayout2sfd: " + it)
             selectedLookingId1 = it.getOrNull(0) ?: ""
             selectedLookingId2 = it.getOrNull(1) ?: ""
             selectedLookingId3 = it.getOrNull(2) ?: ""
         }*/

        ChipGroupHelper.Builder(requireActivity())
            .setChipLayout(chipGroup)
            .setSelectedListener(listener)
            .setMaxSelected(maxCount)
            .setList(ChipGroupHelper.ListType.ChildList(listTop))
            .setAlreadySelectedIds(selectedIdd1, selectedIdd2, selectedIdd3)
            //.setAlreadySelectedIds("", "", "")
            .setBottomSheet(false)
            .setClickable(false)
            .setCloseIconVisible(false)
            .setAllShowOnFirst(false)
            .setViewsForClickedNewInterestPicker(edlooking, title)
            .setStyleColor(ChipGroupHelper.StyleTypes.PhaseAddtional)
            .setRemoveListener(null)
            .build()
    }


    override fun onPause() {
        super.onPause()

        Constant.lastFilterFragCloseTime = System.currentTimeMillis()
    }

    private val listUser = ArrayList<PhaseListObject>()
    private lateinit var addtionaListAdapter: AddtionalAdepter

    private fun noteData(adapterList: java.util.ArrayList<PhaseListObject>) {
        listUser.addAll(adapterList)
        addtionaListAdapter =
            AddtionalAdepter(requireActivity(), this, listUser, PhaseType.AddtionalFilter)
        binding.recyAddtional.adapter = addtionaListAdapter
    }

    override fun onSelected(
        selectedIdList: ArrayList<String>,
        title: String,
        allDataList: List<AddtionalQueObject>
    ) {
        when (title) {
            "Education" -> tempDiscoverData?.education =
                getSelectedIdsString(selectedIdList, allDataList)

            "Language" -> tempDiscoverData?.language =
                getSelectedIdsString(selectedIdList, allDataList)

            "Smoking" -> tempDiscoverData?.smoking =
                getSelectedIdsString(selectedIdList, allDataList)

            "Drink" -> tempDiscoverData?.drink = getSelectedIdsString(selectedIdList, allDataList)
            "Drugs" -> tempDiscoverData?.drugs = getSelectedIdsString(selectedIdList, allDataList)
            "Dietary Lifestyle" -> tempDiscoverData?.dietaryLifestyle =
                getSelectedIdsString(selectedIdList, allDataList)

            "Interests" -> tempDiscoverData?.interests =
                getSelectedIdsString(selectedIdList, allDataList)

            "Pets" -> tempDiscoverData?.pets = getSelectedIdsString(selectedIdList, allDataList)
            "Horoscope" -> tempDiscoverData?.horoscope =
                getSelectedIdsString(selectedIdList, allDataList)

            "Political Views" -> tempDiscoverData?.politicalLeaning =
                getSelectedIdsString(selectedIdList, allDataList)

            "Religion" -> tempDiscoverData?.religion =
                getSelectedIdsString(selectedIdList, allDataList)

            "Vaccinated" -> tempDiscoverData?.covidVaccine =
                getSelectedIdsString(selectedIdList, allDataList)

            "First Date Ice Breaker" -> tempDiscoverData?.firstDateIceBreaker =
                getSelectedIdsString(selectedIdList, allDataList)

            "Art" -> tempDiscoverData?.arts = getSelectedIdsString(selectedIdList, allDataList)
        }
    }

    private fun getSelectedIdsString(
        selectedIdList: java.util.ArrayList<String>,
        allDataList: List<AddtionalQueObject>
    ): String {
        var selectedSmoking = ""
        selectedIdList.forEach { oneItemSelected ->
            allDataList.find { it.title == oneItemSelected }?.id?.let {
                selectedSmoking = if (selectedSmoking == "") it.toString()
                else "$selectedSmoking,$it"
            }
        }
        return selectedSmoking
    }


    private fun nav(txtlabel: String) {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            override fun onStateChanged(p0: View, p1: Int) {}
        }
        val bottomSheetView =
            this.layoutInflater.inflate(R.layout.dailoglocation, null)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)

        //val unlockdisc = bottomSheetView.findViewById<TextView>(R.id.unlockdisc)
        val btnpremium = bottomSheetView.findViewById<AppCompatButton>(R.id.btnpremium)
        val txtLabel = bottomSheetView.findViewById<TextView>(R.id.unlock)
        txtLabel.setText(txtlabel)

        btnpremium.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(
                Intent(requireContext(), SubscriptionPlanActivity::class.java)
            )
        }
        bottomSheetDialog.show()
    }

    private val multiListener = object : AddtionalFilterAdepter.OnListScroll {
        override fun onClick(position: Int) {

            val y = binding.recyAddtional.y + binding.recyAddtional.getChildAt(position).y
            binding.nested.smoothScrollTo(0, y.toInt())

        }

    }


}




