package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.Constant
import com.foreverinlove.R
import com.foreverinlove.SignInActivity
import com.foreverinlove.WatchActivity
import com.foreverinlove.databinding.ActivityCreateProfileBinding
import com.foreverinlove.dialog.ChipGroupGender
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.dialog.DateOfBirthDailog.openDateOfBirthDailog
import com.foreverinlove.dialog.HeightDialog.openHeightPicker
import com.foreverinlove.network.GetUriFileSizeUseCase
import com.foreverinlove.network.LongToMbSizeUseCase
import com.foreverinlove.network.Utility
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.network.response.GenderObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.LocationPickerHelper
import com.foreverinlove.utility.MyListDataHelper
import com.foreverinlove.viewmodels.CreateProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

private const val TAG = "CreateProfileActivity"


@AndroidEntryPoint
@SuppressLint("SetTextI18n", "PrivateResource", "IntentReset")
class CreateProfileActivity : AppCompatActivity() {

    private var tempUserDataObject: TempCreateProfileObject? = null
    private var binding: ActivityCreateProfileBinding? = null
    private var videoPath: String? = null
    var emailInValid: Boolean? = false
    var thumb: Bitmap? = null


    var selectedLookingId1 = ""
    var selectedLookingId2 = ""
    var selectedLookingId3 = ""

    var selectedLangId1 = ""
    var selectedLangId2 = ""
    var selectedLangId3 = ""

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isCameraPermissionGranted = false
    // private var isLocationPermissionGranted = false

    private val locationPickerHelper = LocationPickerHelper()

    private var latStr = ""
    private var longStr = ""

    //private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var latitude: Double? = null
    var longitude: Double? = null

    var apieditcall: String? = null

    var selectedLanguage = ""
    var selectedRelation = ""
    var selectedGender = ""
    var selectedIntrest = ""
    var selectedEducation = ""
    var selectedLookingFor = ""

    private val viewModel: CreateProfileViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel.start()
        locationPickerHelper.initialize(this)



        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
               /* isReadPermissionGranted =
                    permission[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: isReadPermissionGranted*/
                isCameraPermissionGranted =
                    permission[android.Manifest.permission.READ_MEDIA_VIDEO] ?: isCameraPermissionGranted

                /*  isLocationPermissionGranted =
                      permission[android.Manifest.permission.ACCESS_COARSE_LOCATION]
                          ?: isLocationPermissionGranted*/
            }


        binding?.btnSave?.setOnClickListener {

            //startActivity(Intent(this, BioScreenActivity::class.java))

            //chnages

            if (isdatavalid()) {
                tempUserDataObject?.let { it1 -> viewModel.updateUserData(it1) }

                Log.d("looking_fordsd", "onCreate: " + tempUserDataObject?.ukeylanguage)
                Log.d("ukeylookingFor", "onCreate: " + tempUserDataObject?.ukeylookingFor)
                Log.d("ukeyrelationstatus", "onCreate: " + tempUserDataObject?.ukeyrelationstatus)
                Log.d("gendersggf", "ongfjgf3465Create: " + tempUserDataObject?.gender)
                startActivity(Intent(this@CreateProfileActivity, BioScreenActivity::class.java))
            }

        }
        binding?.edemail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                apieditcall = binding?.edemail?.text.toString()
                binding?.rightemail?.visibility = View.GONE
                viewModel.verifyEmail(binding?.edemail?.text.toString())


            }

        })

        lifecycleScope.launch {

            viewModel.emailValidationConversion.collect {
                when (it) {
                    CreateProfileViewModel.GetLoginEvent.Empty -> {
                    }

                    is CreateProfileViewModel.GetLoginEvent.Failure -> {
                        binding?.emailloding?.visibility = View.GONE
                        emailInValid = false

                        // showToast("Email already registered!")
                        // showToast("Email already exist")

                    }

                    CreateProfileViewModel.GetLoginEvent.Loading -> {
                        binding?.emailloding?.visibility = View.VISIBLE
                    }

                    is CreateProfileViewModel.GetLoginEvent.Success -> {
                        binding?.emailloding?.visibility = View.GONE



                        if (apieditcall == binding?.edemail?.text.toString()) {
                            emailInValid = true
                            binding?.rightemail?.visibility = View.VISIBLE
                            /* binding?.edemail?.setCompoundDrawables(null,null,ContextCompat.getDrawable(applicationContext,), null)*/
                        } else {
                            emailInValid = false
                            binding?.rightemail?.visibility = View.GONE
                            viewModel.verifyEmail(binding?.edemail?.text.toString())
                            apieditcall = binding?.edemail?.text.toString()

                        }
                        if (!Constant.checkEmail(binding?.edemail?.text.toString().trim())) {
                            binding?.rightemail?.visibility = View.GONE
                        }


                    }
                }
            }

        }

        binding?.edemail?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding?.edemail?.clearFocus()
            }
            false
        }
        binding?.edjob?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding?.edjob?.clearFocus()
            }
            false
        }
        binding?.linervideo?.setOnClickListener {
            requestPermission()
        }
        binding?.imgBack?.setOnClickListener {
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        }


        //drawble click
        /*
                binding?.edgender?.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        val drawableRight = 2
                        if (event?.action == MotionEvent.ACTION_UP) {
                            if (event.rawX >= (binding?.edgender?.right?.minus(
                                    binding?.edgender?.compoundDrawables?.get(
                                        drawableRight
                                    )?.bounds?.width() ?: 0
                                )!!)
                            ) {
                                // your action here
                                binding?.edemail?.clearFocus()
                                opneGenderDialog(binding!!.edgender)



                                return true
                            }
                        }
                        return false

                    }

                })
        */

        /*
                binding?.edintrest?.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        val drawableRight = 2
                        if (event?.action == MotionEvent.ACTION_UP) {
                            if (event.rawX >= (binding?.edintrest?.right?.minus(
                                    binding?.edintrest?.compoundDrawables?.get(
                                        drawableRight
                                    )?.bounds?.width() ?: 0
                                )!!)
                            ) {
                                binding?.edemail?.clearFocus()
                                opneGenderDialog(binding!!.edintrest)

                                return true
                            }
                        }
                        return false

                    }

                })
        */

        Constant.savbtnClick.observe(this@CreateProfileActivity, Observer {
            if(it==""){
                binding?.edgender?.setText("")

            }else{
            }
        })

        binding?.location?.setOnClickListener {
            binding?.edemail?.clearFocus()
            locationPickerHelper.openLocationPicker(resultLauncher)
        }

        openDateOfBirthDailog(binding!!.edDateofBirth) {
            binding?.edemail?.clearFocus()
        }
        binding?.close1?.setOnClickListener {
            binding?.linervideo?.performClick()

        }


        binding?.play?.setOnClickListener {
            val intent = Intent(this, WatchActivity::class.java)
            intent.putExtra("videoplayer", videoPath)
            startActivity(intent)
        }

        /* if (binding!!.heightchip.text.toString() == "") {

             binding!!.heightchip.visibility = View.GONE
         }*/
        Constant.heightdata.observe(this@CreateProfileActivity, Observer {
            if(it.isNullOrEmpty()){
                binding!!.heightchip.visibility=View.GONE
            }else{
                binding!!.heightchip.visibility=View.VISIBLE
            }
        })
        binding?.edHeight?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val drawableRight = 2
                if (event?.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (binding?.edHeight?.right?.minus(
                            binding?.edHeight?.compoundDrawables?.get(
                                drawableRight
                            )?.bounds?.width() ?: 0
                        )!!)
                    ) {
                        openHeightPicker(
                            binding!!.heightchip,
                            binding!!.edHeight,

                        )

                        return true
                    }
                }
                return false

            }

        })

        binding?.videopic?.setOnClickListener {

            binding?.linervideo?.performClick()
        }


        /*binding!!.edgender.setOnClickListener {

            nav("Select Your Gender")
        }*/


        MyListDataHelper.getAllDataGender().let {
            binding?.edemail?.clearFocus()
            setChipLayoutGender(
                binding!!.genderChip, it!!, 2,
                binding!!.edgender, "Select Your Gender"
            )
        }

        MyListDataHelper.getAllDataGender().let {
            binding?.edemail?.clearFocus()
            setChipLayoutGender(
                binding!!.intrestChip, it!!, 2,
                binding!!.edintrest, "Select Your Interest"
            )
        }


        MyListDataHelper.getAllData()?.language?.let {
            binding?.edemail?.clearFocus()
            setChipLayout(
                binding!!.languagechip, it, 4,
                binding!!.edlanguage, "Language"
            )
        }
        MyListDataHelper.getAllData()?.relationship_status?.let {
            binding?.edemail?.clearFocus()
            setChipLayout(
                binding!!.statuschip2, it, 2,
                binding!!.edstatus, "Relationship status"
            )
        }
        MyListDataHelper.getAllData()?.education?.let {
            binding?.edemail?.clearFocus()

            setChipLayout(
                binding!!.educationchip, it, 2,
                binding!!.edEducation, "Highest Level of Education"
            )
        }

        MyListDataHelper.getAllData()?.looking_for?.let {
            binding?.edemail?.clearFocus()
            setChipLayout(
                binding!!.statuschip, it, 4,
                binding!!.edlooking, "Looking For"
            )
        }

    }




    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {it->

        if (it.resultCode == RESULT_OK) {

            locationPickerHelper.getDataFromResult(it) { address, lat, long ->
                latStr = lat
                longStr = long
                binding?.location?.setText(address)
            }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, SignInActivity::class.java))
        Constant.heightdata.value=""
        finish()

    }

    private fun getLookingString(): String {
        var str = if (selectedLookingId1 != "") selectedLookingId1 else ""
        str += if (selectedLookingId2 != "") ",$selectedLookingId2" else ""
        str += if (selectedLookingId3 != "") ",$selectedLookingId3" else ""

        return str
    }

    private fun getLanguageString(): String {
        var str = if (selectedLangId1 != "") selectedLangId1 else ""
        str += if (selectedLangId2 != "") ",$selectedLangId2" else ""
        str += if (selectedLangId3 != "") ",$selectedLangId3" else ""

        return str
    }

    private var myLookingForBuilder: ChipGroupHelper.Builder? = null
    private var myLanguageBuilder: ChipGroupHelper.Builder? = null


    private fun setChipLayout(
        chipGroup: ChipGroup,
        listTop: List<AddtionalQueObject>,
        maxCount: Int,
        edlooking: TextInputEditText,
        title: String

    ) {

        val listener = object : ChipGroupHelper.ChipSelectedListener {
            override fun onSelectedListChange(list: ArrayList<String>) {
                Log.d(TAG, "onSelectedListChange231: " + list)

                when (title) {
                    "Language" -> selectedLanguage = ""
                    "Relationship status" -> selectedRelation = ""
                    "Highest Level of Education" -> selectedEducation = ""
                    "Looking For" -> selectedLookingFor = ""

                }
                for (i in list.indices) {
                    for (j in listTop.indices) {
                        if (title == "Language") {
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
                        } else if (title == "Relationship status") {
                            Log.d(TAG, "onSelectedListChangedsdsd1221: " + listTop[j].title)

                            if (list[i] == listTop[j].title) {
                                Log.d(TAG, "onSelectedListChangedsdsd12: " + list[i])

                                selectedRelation = listTop[j].id.toString()
                                if (selectedRelation == "") list[i]
                                else selectedRelation + "," + listTop[i].id
                            }

                        } else if (title == "Highest Level of Education") {
                            if (list[i] == listTop[j].title) {

                                selectedEducation = listTop[j].id.toString()
                                if (selectedEducation == "") list[i]
                                else selectedEducation + "," + listTop[i].id
                            }
                        } else if (title == "Looking For") {
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

                        }
                    }
                }
            }
        }
        Log.d(TAG, "setChipLayoutGender123: " + listTop)

        ChipGroupHelper.Builder(this@CreateProfileActivity)
            .setChipLayout(chipGroup)
            .setSelectedListener(listener)
            .setMaxSelected(maxCount)
            .setList(ChipGroupHelper.ListType.ChildList(listTop))
            .setAlreadySelectedIds("", "", "")
            //.setSelectedIdsForChildren("172","","")
            .setBottomSheet(false)
            .setClickable(false)
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

    private fun setChipLayoutGender(
        chipGroup: ChipGroup,
        listTop: List<GenderObject>,
        maxCount: Int,
        edlooking: TextInputEditText,
        title: String

    ) {

        val listenergender = object : ChipGroupGender.ChipSelectedListener {
            override fun onSelectedListChange(list: ArrayList<String>) {

                Log.d(TAG, "onSelectedListChange231: " + list)

                when (title) {
                    "Select Your Gender" -> selectedGender = ""
                    "Select Your Interest" -> selectedIntrest = ""


                }
                for (i in list.indices) {
                    for (j in listTop.indices) {
                        if (title == "Select Your Gender") {
                            Log.d(TAG, "onSelectedListChangedsdsd1221: " + listTop[i].title)

                            if (list[i] == listTop[j].title) {
                                Log.d(TAG, "onSelectedListChangedsdsd12: " + list[i])
                                selectedGender = listTop[j].title
                                if (selectedGender == "") list[i]
                                else selectedGender + "," + listTop[i].id
                            }
                            Log.d(TAG, "onSelectedListChangedsdsd: " + selectedGender)
                        } else if (title == "Select Your Interest") {
                            if (list[i] == listTop[j].title) {
                                selectedIntrest = listTop[j].title
                                if (selectedIntrest == "") list[i]
                                else selectedIntrest + "," + listTop[i].id
                            }
                        }
                    }
                }


            }
        }
        Log.d(TAG, "setChipLayoutGender123: " + listTop)
        ChipGroupGender.Builder(this@CreateProfileActivity)
            .setChipLayout(chipGroup)
            .setSelectedListener(listenergender)
            .setMaxSelected(maxCount)
            .setList(ChipGroupGender.ListType.ChildList(listTop))
            .setAlreadySelectedIds("", "", "")
            .setBottomSheet(false)
            .setClickable(false)
            .setCloseIconVisible(false)
            .setAllShowOnFirst(false)
            .setViewsForClickedNewInterestPicker(
                edlooking,
                title
            )
            .setStyleColor(ChipGroupGender.StyleTypes.PhaseAddtional)
            .setRemoveListener(null)
            .build()
    }


    private fun requestPermission() {
       /* isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED*/

        isCameraPermissionGranted =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) ==
                    PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()
       /* if (!isReadPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }*/
        if (!isCameraPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        } else {
           // val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
           // intent.addCategory(Intent.CATEGORY_OPENABLE)
           // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
           // intent.type = "video/*"

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.type = "video/*"
            startForProfileVideoeResult.launch(intent)
            binding?.videopic?.setImageResource(android.R.color.transparent)

        }
    }

    private fun parsePath(uri: Uri): String? {

        val contentResolver = contentResolver ?: return null
        val filePath = (applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0)
                outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.absolutePath
    }


    private fun isdatavalid(): Boolean {

        binding?.apply {
            if (edfirstname.text.toString().trim().isEmpty()) {

                showToast("Please Enter First Name")
                return false

            } else if (edlastname.text.toString().trim().isEmpty()) {
                showToast("Please Enter Last Name")
                return false

            } else if (edemail.text.toString().trim().isEmpty()) {
                showToast("Please Enter Email")
                return false
            } else if (!Constant.checkEmail(edemail.text.toString().trim())) {
                showToast("Please Enter Valid Email")
                return false
            } else if (emailInValid == null || emailInValid == false) {
                showToast("This Email Is Already Registered")
                return false
            } else if (genderChip.isEmpty()) {
                showToast("Please Select Gender")
                return false

            } else if (edDateofBirth.text.toString().trim().isEmpty()) {
                showToast("Please Select DOB")
                return false

            } else if (edHeight.text.toString().trim().isEmpty()) {
                showToast("Please Select Height")
                return false

            } else if (intrestChip.isEmpty()) {
                showToast("Please Select Interested In")
                return false

            } else if (statuschip.isEmpty()) {
                showToast("Please Select Looking For")
                return false

            } else if (statuschip2.isEmpty()) {
                showToast("Please Select Relationship Status")
                return false

            } else if (languagechip.isEmpty()) {
                showToast("Please Select Language")
                return false

            } else if (location.text.toString().trim().isEmpty()) {
                showToast("Please Select Location")
                return false

            } else if (educationchip.isEmpty()) {
                showToast("Please Select Education")
                return false

            } else if (edjob.text.toString().trim().isEmpty()) {
                showToast("Please Select Job Title")
                return false
            } else if (videoPath == null || videoPath!!.isEmpty()) {
                showToast("Please Select Video")
                return false
            }

            tempUserDataObject = TempCreateProfileObject(
                fnm = edfirstname.text.toString().trim(),
                lnm = edlastname.text.toString().trim(),
                email = edemail.text.toString().trim(),
                // gender = edgender.text.toString().trim(),
                gender = selectedGender,
                looking_for = selectedIntrest,
                dob = edDateofBirth.text.toString().trim(),
                height = findHeightSelected(),
                // ukeylanguage = selectedLanguage,
                ukeylanguage = getLanguageString(),
                lat = latStr,
                long = longStr,
                ukeyrelationstatus = selectedRelation,
                // ukeylookingFor = selectedLookingFor,
                ukeylookingFor = getLookingString(),
                ukeyeducation = selectedEducation,
                jobTitle = edjob.text.toString().trim(),
                address = location.text.toString().trim(),
                video = videoPath.toString(),
                registerFlowStatus = RegisterFlowStatus.CreateProfile
            )

            return true


        }
        return false


    }

    private fun findHeightSelected(): String {
        MyListDataHelper.getAllData()?.height?.forEach { addtionalQueObject ->
            if (addtionalQueObject.title == binding?.edHeight?.text.toString().trim()) {
                return addtionalQueObject.id.toString()
            }
        }

        return ""
    }

    // data receive mate ForActivityResult
    private val startForProfileVideoeResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            try {
                val videoUri: Uri = it?.data!!.data!!

                val mbLength = LongToMbSizeUseCase().convert(
                    GetUriFileSizeUseCase().length(videoUri, contentResolver)
                )

                if (mbLength > 30) {
                   // showToast("File cannot exceed 30 mb size.")
                    showToast("Please Select Video Upto 30 MB")
                    videoPath = null
                    //  binding?.videopic?.setImageResource(R.mipmap.vdemptry)
                    return@registerForActivityResult

                }


                var durationTime: Int

                MediaPlayer.create(this, videoUri).also {
                    durationTime = (it.duration / 1000)

                    //  it.reset()
                    it.release()

                    if (durationTime < 10) {
                        showToast("Please Upload Video More Than 10 Second")
                        videoPath = null
                        // binding?.videopic?.setImageResource(R.mipmap.vdemptry)
                    } else if (durationTime > 30) {
                        showToast("Please Upload Video Less Than 30 Second")
                        videoPath = null
                    } else {

                        videoPath = parsePath(videoUri)?:""

                        binding?.play?.visibility = View.INVISIBLE


                        val path: String = videoPath.toString()

                        thumb = ThumbnailUtils.createVideoThumbnail(
                            path,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )!!

                        binding?.videopic?.scaleType = ImageView.ScaleType.CENTER_CROP
                        binding?.videopic?.setImageBitmap(thumb)

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (thumb == null) {
                    binding?.videopic?.setImageResource(R.mipmap.vdemptry)
                } else {
                    binding?.videopic?.setImageBitmap(thumb)
                }


            }


        }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }

    @SuppressLint("MissingInflatedId")
    private fun nav(txtlabel: String) {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            override fun onStateChanged(p0: View, p1: Int) {}
        }
        val bottomSheetView =
            this.layoutInflater.inflate(R.layout.dailoggenders, null)
        val bottomSheetDialog =
            BottomSheetDialog(this@CreateProfileActivity, R.style.AppBottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)

        //val unlockdisc = bottomSheetView.findViewById<TextView>(R.id.unlockdisc)
        val btnpremium = bottomSheetView.findViewById<AppCompatButton>(R.id.txtGSave)
        val txtLabel = bottomSheetView.findViewById<TextView>(R.id.txtGTitle)
        val radioGroup = bottomSheetView.findViewById<RadioGroup>(R.id.radioGroup)

        txtLabel.setText(txtlabel)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton: RadioButton = findViewById(checkedId)
            val selectedValue: String = selectedRadioButton.text.toString()
            Log.d(TAG, "nav321321: "+selectedValue)
            // Now, selectedValue contains the text of the selected radio button
            // You can use this value as needed
        }

        btnpremium.setOnClickListener {
            bottomSheetDialog.dismiss()
           /* startActivity(
                Intent(this@CreateProfileActivity, SubscriptionPlanActivity::class.java)
            )*/
        }
        bottomSheetDialog.show()
    }

}


data class TempCreateProfileObject(
    val fnm: String,
    val lnm: String,
    val email: String,
    val gender: String,
    val looking_for: String,

    val ukeylookingFor: String,
    val ukeylanguage: String,
    val ukeyrelationstatus: String,
    val ukeyeducation: String,
    val lat: String,
    val long: String,

    val dob: String,
    val height: String,
    val jobTitle: String,
    val address: String,
    val video: String,
    val registerFlowStatus: RegisterFlowStatus,

    )



enum class RegisterFlowStatus {
    CreateProfile, Bio
}