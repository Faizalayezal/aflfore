package com.foreverinlove.screen.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.foreverinlove.Constant
import com.foreverinlove.Constant.getImageUri
import com.foreverinlove.Constant.writeBitmap
import com.foreverinlove.EmailOtpActivity
import com.foreverinlove.databinding.ActivityAddProfilePictureBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.ImagePickerHelper
import com.foreverinlove.utility.dataStoreClearAll
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.AddProfilePictureViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File


@AndroidEntryPoint
class AddProfilePictureActivity : AppCompatActivity() {
    private var binding: ActivityAddProfilePictureBinding? = null
    private var tempUserDataObject: TempCreateProfilePictureObject? = null
    private val viewModel: AddProfilePictureViewModel by viewModels()


    private var positionTop: Int = 0
    private var imgUri1: String = ""
    private var imgUri2: String = ""
    private var imgUri3: String = ""
    private var imgUri4: String = ""
    private var imgUri5: String = ""
    private var imgUri6: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProfilePictureBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel.start()

        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    Log.d("TAG", "onCredgdfgdfgdfgdfgate: "+it.sdk_token)


                }
        }

        binding?.img1?.setOnClickListener {

            imagePickerOpen(1)

        }
        binding?.img2?.setOnClickListener {
            imagePickerOpen(2)

        }
        binding?.img3?.setOnClickListener {
            imagePickerOpen(3)

        }
        binding?.img4?.setOnClickListener {
            imagePickerOpen(4)

        }
        binding?.img5?.setOnClickListener {
            imagePickerOpen(5)

        }
        binding?.img6?.setOnClickListener {
            imagePickerOpen(6)

        }

        binding?.close1?.setOnClickListener {
            binding?.img1?.setImageBitmap(null)
            imgUri1 = ""
            isImg1Selected = false
            binding?.pls1?.visibility = View.VISIBLE
        }
        binding?.close2?.setOnClickListener {
            binding?.img2?.setImageBitmap(null)
            imgUri2 = ""
            isImg2Selected = false


            binding?.pls2?.visibility = View.VISIBLE
        }
        binding?.close3?.setOnClickListener {
            binding?.img3?.setImageBitmap(null)
            imgUri3 = ""
            isImg3Selected = false

            binding?.pls3?.visibility = View.VISIBLE
        }
        binding?.close4?.setOnClickListener {
            binding?.img4?.setImageBitmap(null)
            imgUri4 = ""
            isImg4Selected = false

            binding?.pls4?.visibility = View.VISIBLE
        }
        binding?.close5?.setOnClickListener {
            binding?.img5?.setImageBitmap(null)
            imgUri5 = ""
            isImg5Selected = false

            binding?.pls5?.visibility = View.VISIBLE
        }
        binding?.close6?.setOnClickListener {
            binding?.img6?.setImageBitmap(null)
            imgUri6 = ""
            isImg6Selected = false

            binding?.pls6?.visibility = View.VISIBLE
        }



        binding?.btnnext?.setOnClickListener {


            if (isDataValid()) {
                tempUserDataObject?.let {
                    viewModel.setUserData(
                        imgUri1,
                        imgUri2,
                        imgUri3,
                        imgUri4,
                        imgUri5,
                        imgUri6
                    )
                }

            }
        }



        binding?.imgBack?.setOnClickListener {
            runBlocking {
                dataStoreClearAll()
            }
            onBackPressed()
        }

        lifecycleScope.launch {

            viewModel.loginConversion.collect {
                when (it) {
                    AddProfilePictureViewModel.GetLoginEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is AddProfilePictureViewModel.GetLoginEvent.Failure -> {
                        showToast(it.errorText)

                        Utility.hideProgressBar()
                    }

                    AddProfilePictureViewModel.GetLoginEvent.Loading -> {
                        showProgressBar()
                    }

                    is AddProfilePictureViewModel.GetLoginEvent.Success -> {
                        Utility.hideProgressBar()
                        startActivity(
                            Intent(
                                this@AddProfilePictureActivity,
                                EmailOtpActivity::class.java
                            ).putExtra("IsForm", "RegisterFlow")
                        )
                        showToast("Email OTP Sent")
                        finishAffinity()
                    }
                }
            }

        }

    }

    private var isImg1Selected = false
    private var isImg2Selected = false
    private var isImg3Selected = false
    private var isImg4Selected = false
    private var isImg5Selected = false
    private var isImg6Selected = false

    private fun detectFaceIn(topBitmap: Bitmap, file: File) {
        val image = InputImage.fromBitmap(topBitmap, 0)
        FaceDetection.getClient().process(image).addOnSuccessListener { faces ->
            when {
                faces.size == 1 -> {
                    when (positionTop) {
                        1 -> imgUri1 = file.absolutePath
                        2 -> imgUri2 = file.absolutePath
                        3 -> imgUri3 = file.absolutePath
                        4 -> imgUri4 = file.absolutePath
                        5 -> imgUri5 = file.absolutePath
                        6 -> imgUri6 = file.absolutePath
                    }
                    when (positionTop) {
                        1 -> {

                            isImg1Selected = true

                            binding?.img1?.setImageURI(Uri.parse(file.absolutePath))
                            binding?.pls1?.visibility = View.GONE
                            imgUri1 = file.absolutePath


                        }

                        2 -> {
                            isImg2Selected = true
                            binding?.img2?.setImageURI(Uri.parse(file.absolutePath))
                            binding?.pls2?.visibility = View.GONE
                            imgUri2 = file.absolutePath


                        }

                        3 -> {
                            isImg3Selected = true
                            binding?.img3?.setImageURI(Uri.parse(file.absolutePath))
                            binding?.pls3?.visibility = View.GONE
                            imgUri3 = file.absolutePath


                        }

                        4 -> {
                            isImg4Selected = true
                            binding?.img4?.setImageURI(Uri.parse(file.absolutePath))
                            binding?.pls4?.visibility = View.GONE
                            imgUri4 = file.absolutePath


                        }

                        5 -> {
                            isImg5Selected = true
                            binding?.img5?.setImageURI(Uri.parse(file.absolutePath))
                            binding?.pls5?.visibility = View.GONE
                            imgUri5 = file.absolutePath


                        }

                        6 -> {
                            isImg6Selected = true
                            binding?.img6?.setImageURI(Uri.parse(file.absolutePath))
                            binding?.pls6?.visibility = View.GONE
                            imgUri6 = file.absolutePath


                        }
                    }


                }

                faces.size > 0 -> {
                    Toast.makeText(
                        this@AddProfilePictureActivity,
                        "Please provide pictures only of yourself",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                else -> {
                    Toast.makeText(
                        this@AddProfilePictureActivity,
                        "Please Upload Image With Face",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        }
            .addOnFailureListener { }
    }

    private fun isDataValid(): Boolean {

        tempUserDataObject = TempCreateProfilePictureObject(

            img1 = imgUri1,
            img2 = imgUri2,
            img3 = imgUri3,
            img4 = imgUri4,
            img5 = imgUri5,
            img6 = imgUri6,

            )
        if (!isImg1Selected) {
            showToast("Please Select Main Image")
            return false
        }

        var count = 0

        if (isImg2Selected) count += 1
        if (isImg3Selected) count += 1
        if (isImg4Selected) count += 1
        if (isImg5Selected) count += 1
        if (isImg6Selected) count += 1

        if (count < 2) {
            showToast("Please Select Minimum 3 Images to Continue")
            return false
        }

        return true
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    var bitmap =
                        ImagePickerHelper(fileUri, this@AddProfilePictureActivity).getBitmap()
                    if (bitmap != null) {
                        bitmap = Constant.scaleBitmap(bitmap, 999999)
                        if (bitmap != null) {
                            val uri = getImageUri(bitmap, this@AddProfilePictureActivity)

                            bitmap =
                                ImagePickerHelper(uri!!, this@AddProfilePictureActivity).getBitmap()

                        }
                    }

                    val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        File(filesDir.absolutePath + "${System.currentTimeMillis()}image.png")
                    else
                        File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            "${System.currentTimeMillis()}image.png"
                        )

                    file.writeBitmap(bitmap!!, Bitmap.CompressFormat.PNG, 100)

                    detectFaceIn(bitmap, file)
                }

                ImagePicker.RESULT_ERROR -> {
                    showToast(ImagePicker.getError(data))
                }

                else -> {
                }
            }
        }


    private fun imagePickerOpen(position: Int) {
        positionTop = position

        if (positionTop == 1) {
            ImagePicker.with(this@AddProfilePictureActivity)
               // .cameraOnly()
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        } else {

            ImagePicker.with(this@AddProfilePictureActivity)
                /*.cameraOnly()*/
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }
}


data class TempCreateProfilePictureObject(
    var img1: String,
    val img2: String,
    val img3: String,
    val img4: String,
    val img5: String,
    val img6: String,
)