package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.*
import com.foreverinlove.Constant.writeBitmap
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityEditProfileNewBinding
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.dialog.DateOfBirthDailog.openDateOfBirthDailog
import com.foreverinlove.dialog.HeightDialog.openHeightPicker
import com.foreverinlove.network.GetUriFileSizeUseCase
import com.foreverinlove.network.LongToMbSizeUseCase
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.ImagePickerHelper
import com.foreverinlove.utility.LocationPickerHelper
import com.foreverinlove.utility.MyListDataHelper
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.stateProgress.StateProgressHelper
import com.foreverinlove.viewmodels.CreateProfileViewModel
import com.foreverinlove.viewmodels.EditprofileSetDataViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.makeramen.roundedimageview.RoundedImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File


private const val TAG = "EditProfileActivity"

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityEditProfileNewBinding
    private var tempUserDataObject: TempEditProfileObject? = null
    private var tempUserDataEditObject: TempUserDataObject? = null
    private val locationPickerHelper = LocationPickerHelper()


    var postiontop: Int = 0
    var imguri1: String = ""
    var imguri2: String = ""
    var imguri3: String = ""
    var imguri4: String = ""
    var imguri5: String = ""
    var imguri6: String = ""
    var emailInValid: Boolean? = false
    var currentEmailText: String? = null

    private var latStr = ""
    private var longStr = ""


    private var videoPath: String? = null
    var isimg1Selected = false
    var isimg2Selected = false
    var isimg3Selected = false
    var isimg4Selected = false
    var isimg5Selected = false
    var isimg6Selected = false

    var selectedLangId1 = ""
    var selectedLangId2 = ""
    var selectedLangId3 = ""

    var selectedLookingId1 = ""
    var selectedLookingId2 = ""
    var selectedLookingId3 = ""


    private lateinit var pemissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissonGranted = false
    private var isCameraPermissonGranted = false

    private val viewModel: EditprofileSetDataViewModel by viewModels()
    private val viewModelcraet: CreateProfileViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("EditProfile")
        viewModelcraet.start()
        viewModel.start()
        locationPickerHelper.initialize(this)


        // on the sport data show mate observe use thay
        viewModel.tempUserDataObject.observe(this) {

            it?.let {
                setData(it)
            }
        }
        lifecycleScope.launch {
            this@EditProfileActivity.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataEditObject = it
                    Log.d("TAG", "onCrfddfseate: " + it)

                }
        }


        pemissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                // isReadPermissonGranted = permission[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissonGranted
                isCameraPermissonGranted = permission[android.Manifest.permission.READ_MEDIA_VIDEO]
                    ?: isCameraPermissonGranted

            }

        /*
         binding.edemail.addTextChangedListener(object : TextWatcher {
             override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
             }

             override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
             }

             override fun afterTextChanged(p0: Editable?) {

                 apieditcall = binding.edemail.text.toString()
                 viewModelcraet.verifyEmail(binding.edemail.text.toString())
                 binding.edemail.setCompoundDrawables(null, null, null, null)

             }

         })
 */




        IntrestSelected()

        /*
                setChipLayout(
                    binding.statuschip2, listOf(
                        ChildHobbyData(
                            34, 45, "Single", listOf(), 3
                        ),
                        ChildHobbyData(
                            23, 45, "Separated", listOf(), 3
                        ),
                        ChildHobbyData(
                            344, 45, "Widowed", listOf(), 3
                        ),
                        ChildHobbyData(
                            443, 45, "It’s complicated", listOf(), 3
                        ),
                    ), 2,
                    binding.edStatus,"Relationship status"
                )*/

        viewModelListeners()

        openPhaseActivities()
        stateProgressBar()

        onClicks()

        binding.edemail.setOnClickListener {
            EmailPop()
        }
    }


    private fun viewModelListeners() {
        lifecycleScope.launch {

            viewModel.removeConversion.collect {
                when (it) {
                    EditprofileSetDataViewModel.GetLoginEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Failure -> {
                        Utility.hideProgressBar()
                    }

                    EditprofileSetDataViewModel.GetLoginEvent.Loading -> {
                        showProgressBar()
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Success -> {
                        Utility.hideProgressBar()

                        /*   tempUserDataObject?.let { it1 ->
                               viewModel.RemoveImage()*/
                        Log.d("tdtdt", "onCreate: " + it.result.message)

                    }
                }
            }

        }


        lifecycleScope.launch {

            viewModel.registerConversion.collect {
                when (it) {
                    EditprofileSetDataViewModel.GetLoginEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Failure -> {
                        showToast(it.errorText)

                        Utility.hideProgressBar()
                    }

                    EditprofileSetDataViewModel.GetLoginEvent.Loading -> {
                        showProgressBar()
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Success -> {
                        Utility.hideProgressBar()
                        //   showToast(it.result.data?.email_verified_otp ?: "")
                        if (it.result.message == "Email OTP Sent") {
                            /* showToast(it.result.data?.user?.email_verified_otp ?: "")
                             startActivity(
                                 Intent(
                                     this@EditProfileActivity,
                                     EmailOtpActivity::class.java
                                 )
                                     .putExtra("IsForm", "EditProfileFlow")
                                     .putExtra("IsFormEmail", binding.edemail.text.toString())
                                     .putExtra("tempEmail", tempUserDataObject?.email ?: "")
                             )*/
                            showToast(it.result.message)
                            finish()

                        } else {
                            onBackPressed()
                        }

                    }
                }
            }
        }


    }

    private fun onClicks() {

        binding.btnAdtionalinfo.setOnClickListener {

            startActivity(Intent(this@EditProfileActivity, Phase1Activity::class.java))

            /* binding.llOpenAdditional.visibility = View.VISIBLE
             if (rotet) {
                 rotet = false
             } else {
                 binding.llOpenAdditional.visibility = View.GONE
                 rotet = true
             }*/
        }

        binding.txtMale.setOnClickListener {
            setStateOfGender(1)
        }

        binding.txtFemale.setOnClickListener {
            setStateOfGender(2)
        }

        /* binding.txtTransgender.setOnClickListener {
             setStateOfGender(3)
         }*/
        /* binding.txtTransexual.setOnClickListener {
             setStateOfGender(4)
         }*/
        binding.txtNonBinary.setOnClickListener {
            setStateOfGender(5)
        }

        binding.btnSave.setOnClickListener {
            if (isDataValid()) {

                Log.d(TAG, "onClicks: test228>>${getLanguageString()}")
                tempUserDataObject?.let { it1 ->
                    Log.d(TAG, "onClickslosfdo: test228>>${it1.image1}")

                    viewModel.updateEditUserData(
                        first_name = it1.fnm,
                        last_name = it1.lnm,
                        email = it1.email,
                        gender = it1.gender,
                        interested = it1.intrested,

                        ukeylooking_for = getLookingString(),
                        ukeylanguage = getLanguageString(),
                        ukeyrelationstatus = selectedRelationshipStatus,

                        ukeyeducation = selectedEducationshipStatus,
                        lat = latStr,
                        long = longStr,

                        image1 = it1.image1,
                        image2 = it1.image2,
                        image3 = it1.image3,
                        image4 = it1.image4,
                        image5 = it1.image5,
                        image6 = it1.image6,
                        about = it1.about,
                        job_title = it1.jobtitle,
                        address = it1.address,
                        dob = it1.dob,
                        profile_video = it1.video,
                        height = it1.height,

                        )
                }

                // startActivity(Intent(this@EditProfileActivity, MainActivity::class.java))
            }


        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        Log.d(TAG, "onClicksasad: " + tempUserDataObject?.height)

        binding.heightchip.text = tempUserDataObject?.height


        binding.edHeight.setOnClickListener {
            openHeightPicker(binding.heightchip, binding.edHeight)
        }
        binding.edDateofBirth.setOnClickListener {
            openDateOfBirthDailog(binding.edDateofBirth)
        }

        binding.pls1.setOnClickListener { if (binding.img1.drawable == null) imagepickeropen(1) }
        binding.pls2.setOnClickListener { if (binding.img2.drawable == null) imagepickeropen(2) }
        binding.pls3.setOnClickListener { if (binding.img3.drawable == null) imagepickeropen(3) }
        binding.pls4.setOnClickListener { if (binding.img4.drawable == null) imagepickeropen(4) }
        binding.pls5.setOnClickListener { if (binding.img5.drawable == null) imagepickeropen(5) }
        binding.pls6.setOnClickListener { if (binding.img6.drawable == null) imagepickeropen(6) }

        binding.img1.setOnClickListener { imagepickeropen(1) }
        binding.img2.setOnClickListener { imagepickeropen(2) }
        binding.img3.setOnClickListener { imagepickeropen(3) }
        binding.img4.setOnClickListener { imagepickeropen(4) }
        binding.img5.setOnClickListener { imagepickeropen(5) }
        binding.img6.setOnClickListener { imagepickeropen(6) }

        setCloseIconFunction(
            closeImg = binding.close1, imgMainView = binding.img1,
            imgPlus = binding.pls1, cl = binding.cons1, pos = 1
        )
        setCloseIconFunction(
            closeImg = binding.close2, imgMainView = binding.img2,
            imgPlus = binding.pls2, cl = binding.cons2, pos = 2
        )
        setCloseIconFunction(
            closeImg = binding.close3, imgMainView = binding.img3,
            imgPlus = binding.pls3, cl = binding.cons3, pos = 3
        )
        setCloseIconFunction(
            closeImg = binding.close4, imgMainView = binding.img4,
            imgPlus = binding.pls4, cl = binding.cons4, pos = 4
        )
        setCloseIconFunction(
            closeImg = binding.close5, imgMainView = binding.img5,
            imgPlus = binding.pls5, cl = binding.cons5, pos = 5
        )
        setCloseIconFunction(
            closeImg = binding.close6, imgMainView = binding.img6,
            imgPlus = binding.pls6, cl = binding.cons6, pos = 6
        )



        binding.changevd1.setOnClickListener {
            requestPermissionvd1()
        }
        binding.changevd2.setOnClickListener {
            requestPermission2()
        }
        binding.play1.setOnClickListener {
            val intent = Intent(this, WatchActivity::class.java)
            intent.putExtra("videoplayer", videoPath)
            startActivity(intent)
        }
        binding.play2.setOnClickListener {
            val intent = Intent(this, WatchActivity::class.java)
            intent.putExtra("videoplayer", videoPath)
            startActivity(intent)
        }
        binding.edLocation.setOnClickListener {
            locationPickerHelper.openLocationPicker(resultLauncher)

        }
        MyListDataHelper.getAllData()?.education?.let {
            setChipLayout(
                binding.educationchip, it, 2,
                binding.edEducation, "Education"
            )
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        if (it.resultCode == RESULT_OK) {

            locationPickerHelper.getDataFromResult(it) { address, lat, long ->
                latStr = lat
                longStr = long
                binding.edLocation.setText(address)

                Log.d(TAG, "latbhaibhai:+$lat" + "log" + long)
            }

        }

    }

    private fun setCloseIconFunction(
        closeImg: ImageView,
        imgMainView: RoundedImageView,
        imgPlus: ImageView,
        cl: ConstraintLayout,
        pos: Int
    ) {
        closeImg.setOnClickListener {

            var count = 0

            if (binding.img1.tag.toString().toIntOrNull() != null) count += 1
            if (binding.img2.tag.toString().toIntOrNull() != null) count += 1
            if (binding.img3.tag.toString().toIntOrNull() != null) count += 1
            if (binding.img4.tag.toString().toIntOrNull() != null) count += 1
            if (binding.img5.tag.toString().toIntOrNull() != null) count += 1
            if (binding.img6.tag.toString().toIntOrNull() != null) count += 1

            if (count < 4) {
                showToast("Minimum 3 images is required")
            } else {
                viewModel.removeImage((imgMainView.tag as? String) ?: "")
                imgMainView.setImageBitmap(null)
                imgPlus.visibility = View.VISIBLE
                cl.background = ContextCompat.getDrawable(this, R.drawable.editimgebg)

                imgMainView.tag = ""
                when (pos) {
                    1 -> imguri1 = ""
                    2 -> imguri2 = ""
                    3 -> imguri3 = ""
                    4 -> imguri4 = ""
                    5 -> imguri5 = ""
                    6 -> imguri6 = ""
                }
                swapNextImageViews(pos)
            }
            Log.d(
                TAG,
                "setCloseIcofgffgnFunction: " + binding.img1.tag + "img2=" + binding.img2.tag + "img3=" + binding.img3.tag + "img4=" + binding.img4.tag + "img5=" + binding.img5.tag + "img6=" + binding.img6.tag
            )
        }
    }

    private fun swapNextImageViews(pos: Int) {
        if (pos == 1) {
            imguri1 = imguri2
            imguri2 = imguri3
            imguri3 = imguri4
            imguri4 = imguri5
            imguri5 = imguri6
            imguri6 = ""

            binding.img1.setImageDrawable(binding.img2.drawable)
            binding.img2.setImageDrawable(binding.img3.drawable)
            binding.img3.setImageDrawable(binding.img4.drawable)
            binding.img4.setImageDrawable(binding.img5.drawable)
            binding.img5.setImageDrawable(binding.img6.drawable)
            binding.img6.setImageDrawable(null)

            binding.img1.tag = binding.img2.tag
            binding.img2.tag = binding.img3.tag
            binding.img3.tag = binding.img4.tag
            binding.img4.tag = binding.img5.tag
            binding.img5.tag = binding.img6.tag
            binding.img6.tag = ""
        }
    }

    private fun getLanguageString(): String {
        var str = if (selectedLangId1 != "") selectedLangId1 else ""
        str += if (selectedLangId2 != "") ",$selectedLangId2" else ""
        str += if (selectedLangId3 != "") ",$selectedLangId3" else ""

        return str
    }

    private fun getLookingString(): String {
        var str = if (selectedLookingId1 != "") selectedLookingId1 else ""
        str += if (selectedLookingId2 != "") ",$selectedLookingId2" else ""
        str += if (selectedLookingId3 != "") ",$selectedLookingId3" else ""

        return str
    }

    private var selectedRelationshipStatus = ""
    private var selectedEducationshipStatus = ""
    private fun noteData(adapterList: ArrayList<AddtionalQueObject>, data: TempUserDataObject) {

    }

    private fun setChipLayout(
        chipGroup: ChipGroup,
        listTop: List<AddtionalQueObject>,
        maxCount: Int,
        edlooking: EditText,
        title: String,
    ) {

        val listener = object : ChipGroupHelper.ChipSelectedListener {
            override fun onSelectedListChange(list: ArrayList<String>) {

                Log.d(TAG, "onSelectedListChange: test438>>${title}")

                when (title) {
                    "Language" -> {
                        selectedLangId1 = ""
                        selectedLangId2 = ""
                        selectedLangId3 = ""

                        for (i in list.indices) {
                            for (j in listTop.indices) {
                                if (list[i] == listTop[j].title) {

                                    if (selectedLangId1 == "") selectedLangId1 =
                                        listTop[j].id.toString()
                                    else if (selectedLangId2 == "") selectedLangId2 =
                                        listTop[j].id.toString()
                                    else if (selectedLangId3 == "") selectedLangId3 =
                                        listTop[j].id.toString()

                                }
                            }
                        }

                        myLanguageBuilder?.setAlreadySelectedIds(
                            selectedLangId1,
                            selectedLangId2,
                            selectedLangId3
                        )
                        Log.d(
                            TAG,
                            "onSelectedListChange: test46900>>$selectedLangId1>>$selectedLangId2>>$selectedLangId3"
                        )
                    }

                    "Relationship status" -> {
                        selectedRelationshipStatus = ""

                        for (i in list.indices) {
                            for (j in listTop.indices) {
                                if (list[i] == listTop[j].title) {
                                    selectedRelationshipStatus = listTop[j].id.toString()
                                }
                            }
                        }
                        myRelationshipBuilder?.setAlreadySelectedIds(
                            selectedRelationshipStatus,
                            "",
                            ""
                        )
                    }
                    /* "Looking For" -> {
                         selectedLookingForStatus = ""

                         for (i in list.indices) {
                             for (j in listTop.indices) {
                                 if (list[i] == listTop[j].title) {
                                     selectedLookingForStatus = listTop[j].id.toString()
                                 }
                             }
                         }
                         myLookingForBuilder?.setAlreadySelectedIds(
                             selectedLookingForStatus,
                             "",
                             ""
                         )
                     }*/

                    "Looking For" -> {
                        selectedLookingId1 = ""
                        selectedLookingId2 = ""
                        selectedLookingId3 = ""

                        for (i in list.indices) {
                            for (j in listTop.indices) {
                                if (list[i] == listTop[j].title) {

                                    if (selectedLookingId1 == "") selectedLookingId1 =
                                        listTop[j].id.toString()
                                    else if (selectedLookingId2 == "") selectedLookingId2 =
                                        listTop[j].id.toString()
                                    else if (selectedLookingId3 == "") selectedLookingId3 =
                                        listTop[j].id.toString()

                                }
                            }
                        }

                        myLookingForBuilder?.setAlreadySelectedIds(
                            selectedLookingId1,
                            selectedLookingId2,
                            selectedLookingId3
                        )
                        Log.d(
                            TAG,
                            "onSelectedListChange: test46900>>$selectedLookingId1>>$selectedLookingId2>>$selectedLookingId3"
                        )
                    }

                    "Education" -> {
                        selectedEducationshipStatus = ""

                        for (i in list.indices) {
                            for (j in listTop.indices) {
                                if (list[i] == listTop[j].title) {
                                    selectedEducationshipStatus = listTop[j].id.toString()
                                }
                            }
                        }
                        myEducationshipBuilder?.setAlreadySelectedIds(
                            selectedEducationshipStatus,
                            "",
                            ""
                        )
                    }
                }
            }
        }

        when (title) {
            "Language" -> {
                myLanguageBuilder = ChipGroupHelper.Builder(this@EditProfileActivity)
                    .setChipLayout(chipGroup)
                    .setSelectedListener(listener)
                    .setMaxSelected(maxCount)
                    .setList(ChipGroupHelper.ListType.ChildList(listTop))
                    .setAlreadySelectedIds(selectedLangId1, selectedLangId2, selectedLangId3)
                    .setBottomSheet(false)
                    .setClickable(false)
                    .setIsEditProfileFlow(true)
                    .setCloseIconVisible(false)
                    .setAllShowOnFirst(false)
                    .setViewsForClickedNewInterestPicker(
                        edlooking,
                        title
                    )
                    .setStyleColor(ChipGroupHelper.StyleTypes.PhaseAddtional)
                    .setRemoveListener(null)
                    .build()
            }

            "Relationship status" -> {
                myRelationshipBuilder = ChipGroupHelper.Builder(this@EditProfileActivity)
                    .setChipLayout(chipGroup)
                    .setSelectedListener(listener)
                    .setMaxSelected(maxCount)
                    .setList(ChipGroupHelper.ListType.ChildList(listTop))
                    .setAlreadySelectedIds(selectedRelationshipStatus, "", "")
                    .setBottomSheet(false)
                    .setClickable(false)
                    .setIsEditProfileFlow(true)
                    .setCloseIconVisible(false)
                    .setAllShowOnFirst(false)
                    .setViewsForClickedNewInterestPicker(
                        edlooking,
                        title
                    )
                    .setStyleColor(ChipGroupHelper.StyleTypes.PhaseAddtional)
                    .setRemoveListener(null)
                    .build()
            }

            "Education" -> {
                myEducationshipBuilder = ChipGroupHelper.Builder(this@EditProfileActivity)
                    .setChipLayout(chipGroup)
                    .setSelectedListener(listener)
                    .setMaxSelected(maxCount)
                    .setList(ChipGroupHelper.ListType.ChildList(listTop))
                    .setAlreadySelectedIds(selectedEducationshipStatus, "", "")
                    .setBottomSheet(false)
                    .setClickable(false)
                    .setIsEditProfileFlow(true)
                    .setCloseIconVisible(false)
                    .setAllShowOnFirst(false)
                    .setViewsForClickedNewInterestPicker(
                        edlooking,
                        title
                    )
                    .setStyleColor(ChipGroupHelper.StyleTypes.PhaseAddtional)
                    .setRemoveListener(null)
                    .build()
            }

            "Looking For" -> {
                myLookingForBuilder = ChipGroupHelper.Builder(this@EditProfileActivity)
                    .setChipLayout(chipGroup)
                    .setSelectedListener(listener)
                    .setMaxSelected(maxCount)
                    .setList(ChipGroupHelper.ListType.ChildList(listTop))
                    .setAlreadySelectedIds(
                        selectedLookingId1,
                        selectedLookingId2,
                        selectedLookingId3
                    )
                    .setBottomSheet(false)
                    .setClickable(false)
                    .setIsEditProfileFlow(true)
                    .setCloseIconVisible(false)
                    .setAllShowOnFirst(false)
                    .setViewsForClickedNewInterestPicker(
                        edlooking,
                        title
                    )
                    .setStyleColor(ChipGroupHelper.StyleTypes.PhaseAddtional)
                    .setRemoveListener(null)
                    .build()
            }
        }

    }

    private var myLanguageBuilder: ChipGroupHelper.Builder? = null
    private var myRelationshipBuilder: ChipGroupHelper.Builder? = null
    private var myEducationshipBuilder: ChipGroupHelper.Builder? = null
    private var myLookingForBuilder: ChipGroupHelper.Builder? = null

    private fun openPhaseActivities() {
        binding.apply {
            phase1.setOnClickListener {
                startActivity(Intent(this@EditProfileActivity, Phase1Activity::class.java))
            }
            phase2.setOnClickListener {
                startActivity(Intent(this@EditProfileActivity, Phase2Activity::class.java))
            }
            phase3.setOnClickListener {
                startActivity(Intent(this@EditProfileActivity, Phase3Activity::class.java))
            }
            phase4.setOnClickListener {
                startActivity(Intent(this@EditProfileActivity, Phase4Activity::class.java))
            }
        }
    }


    private fun stateProgressBar() {

    }


    private fun setData(data: TempUserDataObject) {
        binding.edfirstname.setText(data.first_name)
        binding.edlastname.setText(data.last_name)
        binding.edemail.setText(data.email)
        binding.edLocation.setText(data.address)

        latStr = data.latitude
        longStr = data.longitude

        binding.edjob.setText(data.job_title)
        binding.edDateofBirth.setText(data.dob)
        binding.edbio.setText(data.about)

        val listHeightTop = MyListDataHelper.getAllData()?.height
        val heightTitle = listHeightTop?.find { it.id.toString() == data.height }?.title ?: ""
        binding.edHeight.setText(heightTitle)
        binding.heightchip.text = heightTitle


        isimg1Selected = false
        isimg2Selected = false
        isimg3Selected = false
        isimg4Selected = false
        isimg5Selected = false
        isimg6Selected = false

        binding.img1.tag = data.imageId1
        binding.img2.tag = data.imageId2
        binding.img3.tag = data.imageId3
        binding.img4.tag = data.imageId4
        binding.img5.tag = data.imageId5
        binding.img6.tag = data.imageId6

        Glide.with(this@EditProfileActivity).asBitmap().load(data.imageUrl1).into(binding.img1)
        Glide.with(this@EditProfileActivity).asBitmap().load(data.imageUrl2).into(binding.img2)
        Glide.with(this@EditProfileActivity).asBitmap().load(data.imageUrl3).into(binding.img3)
        Glide.with(this@EditProfileActivity).asBitmap().load(data.imageUrl4).into(binding.img4)
        Glide.with(this@EditProfileActivity).asBitmap().load(data.imageUrl5).into(binding.img5)
        Glide.with(this@EditProfileActivity).asBitmap().load(data.imageUrl6).into(binding.img6)


        videoPath = data.profile_video

        Glide.with(this@EditProfileActivity).load(videoPath)
            .into(binding.video1)

        binding.play1.visibility = View.VISIBLE

        //solution 1
        /*   val retriever = MediaMetadataRetriever()
           retriever.setDataSource(data.profile_video, HashMap())
           val image = retriever.setDataSource(,MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
           Glide.with(this@EditProfileActivity).load(retriever.setDataSource(data.profile_video, HashMap())).into(binding.video1)*/

        Log.d(TAG, "setData: test>>" + data.gender)

        if (data.gender.equals("Male", true)) setStateOfGender(1)
        else if (data.gender.equals("Female", true)) setStateOfGender(2)
        // else if (data.gender.equals("Transgender", true)) setStateOfGender(3)
        //  else if (data.gender.equals("Transexual", true)) setStateOfGender(4)
        else if (data.gender.equals("Non-Binary", true)) setStateOfGender(5)

        if (data.intrested.equals("Male", true)) setStateOfInterest(1)
        else if (data.intrested.equals("Female", true)) setStateOfInterest(2)
        //  else if (data.intrested.equals("Transgender", true)) setStateOfInterest(3)
        // else if (data.intrested.equals("Transexual", true)) setStateOfInterest(4)
        else if (data.intrested.equals("Non-Binary", true)) setStateOfInterest(5)

        var adapterList = ArrayList<AddtionalQueObject>()

        MyListDataHelper.getAllData()?.let {
            adapterList = ArrayList(it.relationship_status)
        }
        MyListDataHelper.getAllData()?.let {
            adapterList = ArrayList(it.education)
        }

        Log.d(TAG, "setData: etstdata>>${data.ukeyeducation}>>${data.ukeyrelationship_status}")

        data.ukeylanguage.split(",").let {
            for (index in it.indices) {
                when (index) {
                    0 -> selectedLangId1 = it[index]
                    1 -> selectedLangId2 = it[index]
                    2 -> selectedLangId3 = it[index]
                }
            }
        }
        data.ukeylooking_for.split(",").let {
            for (index in it.indices) {
                when (index) {
                    0 -> selectedLookingId1 = it[index]
                    1 -> selectedLookingId2 = it[index]
                    2 -> selectedLookingId3 = it[index]
                }
            }
        }

        selectedRelationshipStatus = data.ukeyrelationship_status
        selectedEducationshipStatus = data.ukeyeducation
        /*
                selectedLookingForStatus = data.ukeylooking_for
        */

        MyListDataHelper.getAllData()?.language?.let {
            Log.d(TAG, "onCreate: testflowTop")
            setChipLayout(
                binding.languagechip, it, 4,
                binding.edLanguage, "Language"
            )
        }

        MyListDataHelper.getAllData()?.education?.let {
            setChipLayout(
                binding.educationchip, it, 2,
                binding.edEducation, "Education"
            )
        }

        MyListDataHelper.getAllData()?.relationship_status?.let {
            setChipLayout(
                binding.relationshipStatusChip, it, 2,
                binding.edRelationshipStatus, "Relationship status"
            )
        }

        MyListDataHelper.getAllData()?.looking_for?.let {
            setChipLayout(
                binding.lookingForChip, it, 4,
                binding.edLookingFor, "Looking For"
            )
        }


        var phase1Count = 0
        if (data.ukeysmoking.trim() != "" && data.ukeysmoking.trim() != "0") phase1Count += 1
        if (data.ukeydrugs.trim() != "" && data.ukeydrugs.trim() != "0") phase1Count += 1
        if (data.ukeydrink.trim() != "" && data.ukeydrink.trim() != "0") phase1Count += 1
        StateProgressHelper.Builder()
            .setHorizontalMainView(binding.llStateProgress1)
            .setActiveColor(R.color.phaseColor1)
            .setMaxItems(3)
            .setSelected(phase1Count)
            .build()

        var phase2Count = 0
        if (data.ukeydietary_lifestyle.trim() != "" && data.ukeydietary_lifestyle.trim() != "0") phase2Count += 1
        if (data.ukeyinterests.trim() != "" && data.ukeyinterests.trim() != "0") phase2Count += 1
        if (data.ukeypets.trim() != "" && data.ukeypets.trim() != "0") phase2Count += 1
        if (data.ukeyhoroscope.trim() != "" && data.ukeyhoroscope.trim() != "0") phase2Count += 1
        StateProgressHelper.Builder()
            .setHorizontalMainView(binding.llStateProgress2)
            .setActiveColor(R.color.phaseColor2)
            .setMaxItems(4)
            .setSelected(phase2Count)
            .build()

        var phase3Count = 0
        if (data.ukeypolitical_leaning.trim() != "" && data.ukeypolitical_leaning.trim() != "0") phase3Count += 1
        if (data.ukeyreligion.trim() != "" && data.ukeyreligion.trim() != "0") phase3Count += 1
        StateProgressHelper.Builder()
            .setHorizontalMainView(binding.llStateProgress3)
            .setActiveColor(R.color.phaseColor3)
            .setMaxItems(2)
            .setSelected(phase3Count)
            .build()

        var phase4Count = 0
        if (data.ukeycovid_vaccine.trim() != "" && data.ukeycovid_vaccine.trim() != "0") phase4Count += 1
        if (data.ukeyfirst_date_ice_breaker.trim() != "" && data.ukeyfirst_date_ice_breaker.trim() != "0") phase4Count += 1
        if (data.ukeyarts.trim() != "" && data.ukeyarts.trim() != "0") phase4Count += 1
        StateProgressHelper.Builder()
            .setHorizontalMainView(binding.llStateProgress4)
            .setActiveColor(R.color.phaseColor4)
            .setMaxItems(3)
            .setSelected(phase4Count)
            .build()

        Log.d(
            TAG,
            "setData: testcountPhase>>$phase1Count>>$phase2Count>>$phase3Count>>$phase4Count"
        )

        noteData(adapterList, data)

    }

    /* private fun setChipLayout(
         chipGroup: ChipGroup,
         listOf: List<ChildHobbyData>,
         maxCount: Int,
         edlooking: EditText,
         title: String
     ) {
         ChipGroupHelper.Builder(this@EditProfileActivity)
             .setChipLayout(chipGroup)
             .setSelectedListener(null)
             .setMaxSelected(maxCount)
             .setList(ChipGroupHelper.ListType.ChildList(listOf))
             .setAlreadySelectedIds("", "", "")
             .setBottomSheet(false)
             .setClickable(false)
             .setCloseIconVisible(true)
             .setAllShowOnFirst(false)
             .setViewsForClickedNewInterestPicker(
                 edlooking,
                 title
             )
             .setStyleColor(ChipGroupHelper.StyleTypes.PhaseAddtional)
             .setRemoveListener(null)
             .build()
     }*/


    private fun detectFaceIn(topBitmap: Bitmap, file: File) {
        val image = InputImage.fromBitmap(topBitmap, 0)
        FaceDetection.getClient().process(image).addOnSuccessListener { faces ->
            when {
                faces.size == 1 -> {
                    if (postiontop == 1) {
                        viewModel.removeImage((binding.img1.tag as? String) ?: "")
                        binding.img1.setImageBitmap(null)
                    }
                    if (postiontop == 2) {
                        viewModel.removeImage((binding.img2.tag as? String) ?: "")
                        binding.img2.setImageBitmap(null)
                    }
                    if (postiontop == 3) {
                        viewModel.removeImage((binding.img3.tag as? String) ?: "")
                        binding.img3.setImageBitmap(null)
                    }
                    if (postiontop == 4) {
                        viewModel.removeImage((binding.img4.tag as? String) ?: "")
                        binding.img4.setImageBitmap(null)
                    }
                    if (postiontop == 5) {
                        viewModel.removeImage((binding.img5.tag as? String) ?: "")
                        binding.img5.setImageBitmap(null)
                    }
                    if (postiontop == 6) {
                        viewModel.removeImage((binding.img6.tag as? String) ?: "")
                        binding.img6.setImageBitmap(null)
                    }

                    when (postiontop) {
                        1 -> imguri1 = file.absolutePath
                        2 -> imguri2 = file.absolutePath
                        3 -> imguri3 = file.absolutePath
                        4 -> imguri4 = file.absolutePath
                        5 -> imguri5 = file.absolutePath
                        6 -> imguri6 = file.absolutePath
                    }
                    when (postiontop) {
                        1 -> {
                            isimg1Selected = true
                            binding.pls1.visibility = View.GONE
                            binding.cons1.background = null
                            imguri1 = file.absolutePath
                        }

                        2 -> {
                            isimg2Selected = true
                            binding.pls2.visibility = View.GONE
                            binding.cons2.background = null
                            imguri2 = file.absolutePath
                        }

                        3 -> {
                            isimg3Selected = true
                            binding.pls3.visibility = View.GONE
                            binding.cons3.background = null
                            imguri3 = file.absolutePath
                        }

                        4 -> {
                            isimg4Selected = true
                            binding.pls4.visibility = View.GONE
                            binding.cons4.background = null
                            imguri4 = file.absolutePath
                        }

                        5 -> {
                            isimg5Selected = true
                            binding.pls5.visibility = View.GONE
                            binding.cons5.background = null
                            imguri5 = file.absolutePath
                        }

                        6 -> {
                            isimg6Selected = true
                            binding.pls6.visibility = View.GONE
                            binding.cons6.background = null
                            imguri6 = file.absolutePath
                        }
                    }

                    if (imguri1 != "") binding.img1.setImageURI(Uri.parse(imguri1))
                    if (imguri2 != "") binding.img2.setImageURI(Uri.parse(imguri2))
                    if (imguri3 != "") binding.img3.setImageURI(Uri.parse(imguri3))
                    if (imguri4 != "") binding.img4.setImageURI(Uri.parse(imguri4))
                    if (imguri5 != "") binding.img5.setImageURI(Uri.parse(imguri5))
                    if (imguri6 != "") binding.img6.setImageURI(Uri.parse(imguri6))
                }

                faces.size > 0 -> {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "More than 1 face detected.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                else -> {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Please upload image with face",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
            .addOnFailureListener { }
    }


    private fun isDataValid(): Boolean {
        Log.d(TAG, "isDataValid: " + selectedEducationshipStatus)
        binding.apply {
            if (edfirstname.text.toString().trim().isEmpty()) {
                showToast("Please Enter First Name")
                return false
            } else if (edlastname.text.toString().trim().isEmpty()) {
                showToast("Please Enter Last Name")
                return false
            } else if (edDateofBirth.text.toString().trim().isEmpty()) {
                showToast("Please Select DOB")
                return false
            } else if (edLocation.text.toString().trim().isEmpty()) {
                showToast("Please Select Location")
                return false
            } else if (edemail.text.toString().trim().isEmpty()) {
                showToast("Please Enter Email")
                return false
            } else if (!Constant.checkEmail(edemail.text.toString().trim())) {
                showToast("Please Enter Valid Email")
                return false
            } else if ((emailInValid == null || emailInValid == false) && tempUserDataEditObject?.email != binding.edemail.text.toString()) {
                showToast("This Email Is Already Registered")
                return false
            } else if (educationchip.isEmpty()) {
                showToast("Please Select Education")
                return false
            } else if (edjob.text.toString().trim().isEmpty()) {
                showToast("Please Enter Job Title")
                return false
            } else if (edHeight.text.toString().trim().isEmpty()) {
                showToast("Please Select Height.")
                return false
            } else if (relationshipStatusChip.isEmpty()) {
                showToast("Please Select Relationship Status")
                return false
            } else if (lookingForChip.isEmpty()) {
                showToast("Please Select Looking For")
                return false
            } else if (edbio.text.toString().trim().isEmpty()) {
                showToast("Please Enter Description.")
                return false
            } else if (edbio.text.length < 10) {
                showToast("Please Enter Minimum 10 Characters")
                return false
            } else if (languagechip.isEmpty()) {
                showToast("Please Select Language")
                return false
            } else if (videoPath == null || videoPath!!.isEmpty()) {
                showToast("Please Select Video")
                return false
            }

            var count = 0

            if (isimg1Selected || binding.img1.tag.toString().toIntOrNull() != null) count += 1
            if (isimg2Selected || binding.img2.tag.toString().toIntOrNull() != null) count += 1
            if (isimg3Selected || binding.img3.tag.toString().toIntOrNull() != null) count += 1
            if (isimg4Selected || binding.img4.tag.toString().toIntOrNull() != null) count += 1
            if (isimg5Selected || binding.img5.tag.toString().toIntOrNull() != null) count += 1
            if (isimg6Selected || binding.img6.tag.toString().toIntOrNull() != null) count += 1

            if (count < 3) {
                showToast("Please Select Minimum 3 Images to Continue")
                return false
            }



            tempUserDataObject = TempEditProfileObject(
                fnm = edfirstname.text.toString().trim(),
                lnm = edlastname.text.toString().trim(),
                email = edemail.text.toString().trim(),

                ukeylanguage = "",
                ukyrelationship = "",
                ukeylookingfor = "",
                ukeyeducation = "",
                //ukeyeducation = findEducationId(edEducation.text.toString().trim()),


                gender = selectedgender ?: "",
                intrested = selectedIntrested ?: "",
                image1 = imguri1,
                image2 = imguri2,
                image3 = imguri3,
                image4 = imguri4,
                image5 = imguri5,
                image6 = imguri6,
                dob = edDateofBirth.text.toString().trim(),
                height = findHeightId(edHeight.text.toString().trim()),
                jobtitle = edjob.text.toString().trim(),
                address = edLocation.text.toString().trim(),
                video = videoPath.toString(),
                about = edbio.text.toString().trim(),
            )


        }
        return true
    }

    private fun findHeightId(string: String): String {
        val listHeightTop = MyListDataHelper.getAllData()?.height
        return (listHeightTop?.find { it.title == string }?.id ?: "").toString()
    }

    private fun findEducationId(string: String): String {
        val listHeightTop = MyListDataHelper.getAllData()?.education
        return (listHeightTop?.find { it.title == string }?.id ?: "").toString()
    }


    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    val bitmap = ImagePickerHelper(fileUri, this@EditProfileActivity).getBitmap()

                    val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        File(filesDir.absolutePath + "${System.currentTimeMillis()}image.png")
                    else
                        File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            "${System.currentTimeMillis()}image.png"
                        )

                    /*val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        "${System.currentTimeMillis()}image.png"
                    )*/
                    file.writeBitmap(bitmap!!, Bitmap.CompressFormat.PNG, 100)

                    detectFaceIn(bitmap, file)


                }

                ImagePicker.RESULT_ERROR -> {
                    showToast(ImagePicker.getError(data))
                }

                else -> {
                    //showToast("Task Cancelled")
                }
            }
        }

    private fun requestPermission2() {
        isReadPermissonGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED

        isCameraPermissonGranted =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()
        if (!isReadPermissonGranted) {
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isCameraPermissonGranted) {
            permissionRequest.add(android.Manifest.permission.CAMERA)
        }
        if (permissionRequest.isNotEmpty()) {
            pemissionLauncher.launch(permissionRequest.toTypedArray())
        } else {
            // val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            // intent.addCategory(Intent.CATEGORY_OPENABLE)
            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            // intent.type = "video/*"
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.type = "video/*"
            startForProfileVideo2Result.launch(intent)
        }
    }

    private fun requestPermissionvd1() {
        /* isReadPermissonGranted = ContextCompat.checkSelfPermission(
             this,
             android.Manifest.permission.READ_EXTERNAL_STORAGE
         ) == PackageManager.PERMISSION_GRANTED*/

        isCameraPermissonGranted =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) ==
                    PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()
        /* if (!isReadPermissonGranted) {
             permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
         }*/
        if (!isCameraPermissonGranted) {
            permissionRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
        }
        if (permissionRequest.isNotEmpty()) {
            pemissionLauncher.launch(permissionRequest.toTypedArray())
        } else {

            //  val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            //intent.type = "video/*"
            // val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            // intent.addCategory(Intent.CATEGORY_OPENABLE)
            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.type = "video/*"
            startForProfileVideoResult.launch(intent)


        }
    }


    @SuppressLint("Recycle")
    private fun parsePath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else null
    }

    private val startForProfileVideo2Result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            try {
                val videoUri: Uri = it.data!!.data!!
                val mbLength = LongToMbSizeUseCase().convert(
                    GetUriFileSizeUseCase().length(videoUri, contentResolver)
                )

                if (mbLength > 30) {
                    showToast("Please Select Video Upto 30 MB")
                    return@registerForActivityResult
                }

                var durationTime: Int

                MediaPlayer.create(this, videoUri).also {
                    durationTime = (it.duration / 1000)
                    //  it.reset()
                    it.release()

                    if (durationTime < 10) {
                        showToast("Please Upload Video More Than 10 Second")
                    } else if (durationTime > 30) {
                        showToast("Please Upload Video Less Than 30 Second")
                    } else {

                        videoPath = parsePath(videoUri)!!



                        binding.video2.visibility = View.VISIBLE
                        binding.play2.visibility = View.VISIBLE

                        val path: String = videoPath.toString()
                        val thumb: Bitmap = ThumbnailUtils.createVideoThumbnail(
                            path,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )!!

                        binding.video2.scaleType = ImageView.ScaleType.CENTER_CROP

                        binding.video2.setImageBitmap(thumb)
                    }

                }


            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    private val startForProfileVideoResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->

            try {

                val videoUri: Uri = it.data!!.data!!
                val mbLength = LongToMbSizeUseCase().convert(
                    GetUriFileSizeUseCase().length(videoUri, contentResolver)
                )

                if (mbLength > 30) {
                    showToast("Please Select Video Upto 30 MB")
                    return@registerForActivityResult
                }

                Log.d("TAG", "sgssdfsf: " + mbLength)
                var durationTime: Int



                MediaPlayer.create(this, videoUri).also {
                    durationTime = (it.duration / 1000)
                    //  it.reset()
                    it.release()

                    if (durationTime < 10) {
                        showToast("Minimum video size is 10 second")
                    } else if (durationTime > 30) {
                        showToast("Maximum  video size is 30 second")
                    } else {

                        videoPath = parsePath(videoUri)!!


                        binding.video1.visibility = View.VISIBLE
                        binding.play1.visibility = View.VISIBLE

                        val path: String = videoPath.toString()
                        val thumb: Bitmap = ThumbnailUtils.createVideoThumbnail(
                            path,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )!!

                        binding.video1.scaleType = ImageView.ScaleType.CENTER_CROP

                        binding.video1.setImageBitmap(thumb)

                    }


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }


    private fun imagepickeropen(i: Int) {
        postiontop = i

        ImagePicker.with(this@EditProfileActivity)
            /*.cameraOnly()*/
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }

    }

    private var selectedgender: String? = null
    private var selectedIntrested: String? = null

    private fun setStateOfGender(i: Int) {
        binding.txtMale.setCompoundDrawablesWithIntrinsicBounds(
            if (i == 1) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
            0, 0, 0
        )
        binding.txtFemale.setCompoundDrawablesWithIntrinsicBounds(
            if (i == 2) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
            0, 0, 0
        )
        /* binding.txtTransgender.setCompoundDrawablesWithIntrinsicBounds(
             if (i == 3) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
             0, 0, 0
         )*/
        /*binding.txtTransexual.setCompoundDrawablesWithIntrinsicBounds(
            if (i == 4) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
            0, 0, 0
        )*/
        binding.txtNonBinary.setCompoundDrawablesWithIntrinsicBounds(
            if (i == 5) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
            0, 0, 0
        )

        if (i == 1) {
            selectedgender = "male"
        }
        if (i == 2) {
            selectedgender = "female"
        }
        if (i == 3) {
            selectedgender = "transgender"
        }
        if (i == 4) {
            selectedgender = "transexual"
        }
        if (i == 5) {
            selectedgender = "non-binary"
        }

    }

    private fun setStateOfInterest(i: Int) {
        binding.txtIntMale.setCompoundDrawablesWithIntrinsicBounds(
            if (i == 1) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
            0, 0, 0
        )
        binding.txtIntFemale.setCompoundDrawablesWithIntrinsicBounds(
            if (i == 2) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
            0, 0, 0
        )
        /* binding.txtIntTransgender.setCompoundDrawablesWithIntrinsicBounds(
             if (i == 3) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
             0, 0, 0
         )*/
        /* binding.txtIntTransexual.setCompoundDrawablesWithIntrinsicBounds(
             if (i == 4) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
             0, 0, 0
         )*/
        binding.txtIntNonBinary.setCompoundDrawablesWithIntrinsicBounds(
            if (i == 5) R.drawable.gendereditchackebg else R.drawable.gendereditunchakebg,
            0, 0, 0
        )

        if (i == 1) {
            selectedIntrested = "male"
        }
        if (i == 2) {
            selectedIntrested = "female"
        }
        if (i == 3) {
            selectedIntrested = "transgender"
        }
        if (i == 4) {
            selectedIntrested = "transexual"
        }
        if (i == 5) {
            selectedIntrested = "non-binary"
        }
    }


    private fun IntrestSelected() {

        binding.txtIntMale.setOnClickListener {
            setStateOfInterest(1)

        }

        binding.txtIntFemale.setOnClickListener {

            setStateOfInterest(2)
        }

        /* binding.txtIntTransgender.setOnClickListener {
             setStateOfInterest(3)
         }*/
        /* binding.txtIntTransexual.setOnClickListener {
             setStateOfInterest(4)
         }*/
        binding.txtIntNonBinary.setOnClickListener {
            setStateOfInterest(5)
        }
    }


    private fun EmailPop() {

        val dialog = Dialog(this@EditProfileActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogeditemail)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@EditProfileActivity,
                    R.color.sucessaplaytransperent
                )
            )
        )

        val emailedit = dialog.findViewById<TextInputEditText>(R.id.edemail)
        val righticon = dialog.findViewById<ImageView>(R.id.rightemail)
        val closeIcon = dialog.findViewById<ImageView>(R.id.imageclose)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }


        Glide.with(this@EditProfileActivity).load(R.mipmap.editemail)
            .into(dialog.findViewById<ImageView>(R.id.imageView13))

        val pro = dialog.findViewById<ProgressBar>(R.id.emailloding)
        val Apipro = dialog.findViewById<ProgressBar>(R.id.Apiloding)
        val submit = dialog.findViewById<AppCompatButton>(R.id.btnContinue)
        Apipro.visibility = View.GONE



        emailedit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (emailedit.text.toString().length > 5) {
                    currentEmailText = emailedit.text.toString()
                    righticon.visibility = View.GONE
                    viewModelcraet.verifyEmail(emailedit.text.toString())
                }


            }

        })
        lifecycleScope.launch {

            viewModelcraet.emailValidationConversion.collect {
                when (it) {
                    CreateProfileViewModel.GetLoginEvent.Empty -> {
                    }

                    is CreateProfileViewModel.GetLoginEvent.Failure -> {
                        pro.visibility = View.GONE

                        emailInValid = false

                    }

                    CreateProfileViewModel.GetLoginEvent.Loading -> {
                        pro.visibility = View.VISIBLE

                    }

                    is CreateProfileViewModel.GetLoginEvent.Success -> {
                        pro.visibility = View.GONE



                        if (currentEmailText == emailedit.text.toString()) {
                            emailInValid = true
                            righticon.visibility = View.VISIBLE

                        } else {
                            emailInValid = false
                            righticon.visibility = View.GONE

                        }
                        if (!Constant.checkEmail(emailedit.text.toString().trim())) {
                            righticon.visibility = View.GONE
                        }
                        if (it.result.message == "Email already registered!") {
                            emailInValid = false
                            righticon.visibility = View.GONE

                        }


                    }
                }
            }


        }

        lifecycleScope.launch {

            viewModel.registerConversion.collect {
                when (it) {
                    EditprofileSetDataViewModel.GetLoginEvent.Empty -> {
                        Apipro.visibility = View.GONE
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Failure -> {

                        Apipro.visibility = View.GONE
                    }

                    EditprofileSetDataViewModel.GetLoginEvent.Loading -> {
                        Apipro.visibility = View.VISIBLE
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Success -> {
                        Apipro.visibility = View.GONE
                        //   showToast(it.result.data?.email_verified_otp ?: "")
                        if (it.result.message == "Email OTP Sent") {
                            startActivity(
                                Intent(
                                    this@EditProfileActivity,
                                    EmailOtpActivity::class.java
                                )
                                    .putExtra("IsForm", "EditProfileFlow")
                                    .putExtra("IsFormEmail", emailedit.text.toString())
                                    .putExtra("tempEmail", emailedit.text.toString())
                            )

                            finish()
                            dialog.dismiss()


                        } else {
                            onBackPressed()
                        }
                    }
                }
            }
        }





        submit.setOnClickListener {
            if (isdatavalid(emailedit.text.toString())) {
                updateData(emailedit.text.toString().trim())
            }
        }
        dialog.show()

    }

    private fun isdatavalid(EditEmail: String): Boolean {
        if (EditEmail.trim().isEmpty()) {
            showToast("Please Enter Email")
            return false

        } else if ((emailInValid == null || emailInValid == false) && tempUserDataEditObject?.email != binding.edemail.text.toString()) {
            showToast("This Email Is Already Registered")
            return false
        } else if (emailInValid == false) {
            showToast("This Email Is Already Registered")
            return false
        } else if (EditEmail.trim() == binding.edemail.text.toString()) {
            showToast("OTP Is Already Sent On This Email Address")
            return false

        } else if (!Constant.checkEmail(EditEmail.trim())) {
            showToast("Please Enter Valid Email")
            return false

        }


        return true

    }

    private fun updateData(EditEmail: String) {
        tempUserDataEditObject?.let { it1 ->
            Log.d(TAG, "updateData321: " + it1.imageUrl1)
            viewModel.updateEditUserData(
                first_name = it1.first_name,
                last_name = it1.last_name,
                email = EditEmail,
                gender = it1.gender,
                interested = it1.intrested,

                ukeylooking_for = it1.ukeylooking_for,
                ukeylanguage = it1.ukeylanguage,
                ukeyrelationstatus = it1.ukeyrelationship_status,

                ukeyeducation = it1.ukeyeducation,

                lat = it1.latitude,
                long = it1.longitude,
                image1 = it1.imageUrl1,
                image2 = it1.imageUrl2,
                image3 = it1.imageUrl3,
                image4 = it1.imageUrl4,
                image5 = it1.imageUrl5,
                image6 = it1.imageUrl6,
                about = it1.about,
                job_title = it1.job_title,
                address = it1.address,
                dob = it1.dob,
                profile_video = it1.profile_video,
                height = it1.height
            )

        }

    }


}


data class TempEditProfileObject(
    val fnm: String,
    val lnm: String,
    val email: String,
    val gender: String,
    val intrested: String,
    val dob: String,

    val ukeylookingfor: String,
    val ukyrelationship: String,
    val ukeylanguage: String,
    val ukeyeducation: String,

    val height: String,
    val image1: String,
    val image2: String,
    val image3: String,
    val image4: String,
    val image5: String,
    val image6: String,
    val jobtitle: String,
    val address: String,
    val video: String,
    val about: String,
)