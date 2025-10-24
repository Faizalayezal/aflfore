package com.foreverinlove.chatmodual

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.R
import com.foreverinlove.chatmodual.ImageViewExt.loadImageWithGlide
import com.foreverinlove.network.Utility
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.PendingPopupScreenData
import com.foreverinlove.screen.activity.NoInternetActivity
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.InternetConnectivityHelper
import com.foreverinlove.utility.PopupListHelper
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment(layoutId: Int) : Fragment(layoutId) {
    private val userLastSeenViewModel: UserLastSeenViewModel by viewModels()

    fun screenOpened(screenName: String) = lifecycleScope.launch {
        delay(2000)
        requireActivity().checkForPendingPopup(screenName) {
            removeAPopup(it)
        }
    }

    @Inject
    lateinit var repository: MainRepository

    private fun removeAPopup(screenId: String) = GlobalScope.launch {
        val token = context?.dataStoreGetUserData()?.firstOrNull()?.token ?: ""
        repository.readPopupData(token, screenId = screenId)
    }


    override fun onStart() {
        super.onStart()
        userLastSeenViewModel.start()
    }

    override fun onResume() {
        super.onResume()

        userLastSeenViewModel.makeOnline()
        InternetConnectivityHelper.checkConnection(requireContext()) {
            if (!it) startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()

        userLastSeenViewModel.makeOffline()
        InternetConnectivityHelper.cancelListener()

    }

    override fun onDestroy() {
        Utility.hideProgressBar()
        super.onDestroy()

        userLastSeenViewModel.makeOffline()
        InternetConnectivityHelper.cancelListener()
    }
}

@SuppressLint("SuspiciousIndentation")
private fun Activity.checkForPendingPopup(screenName: String, popupIsShowed: (String) -> Unit) {
    var screenData: PendingPopupScreenData? = null

    val pendingPopupDataData = PopupListHelper.getAllData()?.find {
        screenData = it.screen_data?.find { it.name.equals(screenName, ignoreCase = true) }
        screenData != null
    }

    if (pendingPopupDataData == null || screenData == null) return

    val dialog = Dialog(this, R.style.successfullDailog11)
    dialog.setContentView(R.layout.activity_dynamicpopup)
    dialog.window!!.setBackgroundDrawable(
        ColorDrawable(
            ContextCompat.getColor(
                this,
                R.color.transparent3
            )
        )
    )

    val clMain = dialog.findViewById<ConstraintLayout>(R.id.mainCl)
    val popUp = dialog.findViewById<CardView>(R.id.popUp)
    val imgPopUp = dialog.findViewById<ImageView>(R.id.imgPopUp)
    val txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
    val txtDescription = dialog.findViewById<TextView>(R.id.txtDescription)

    val flow: (TextView, String, String) -> Unit =
        { textView: TextView, text: String, color: String ->
            textView.text = text
            if (color.isNotBlank())
                textView.setTextColor(Color.parseColor(color))
        }

    flow(txtTitle, pendingPopupDataData.name ?: "", pendingPopupDataData.txt_color ?: "#000000")
    flow(
        txtDescription,
        pendingPopupDataData.description ?: "",
        pendingPopupDataData.txt_color ?: "#000000"
    )

    imgPopUp.loadImageWithGlide(pendingPopupDataData.icon ?: "", ImageBorderOption.NOTCROP)

    if (!pendingPopupDataData.bg_color.isNullOrEmpty())
        popUp.setCardBackgroundColor(Color.parseColor(pendingPopupDataData.bg_color))

    clMain.setOnClickListener { dialog.dismiss() }

    dialog.show()

    PopupListHelper.removeSinglePopupData(screenData?.screen_id.toString())
    popupIsShowed.invoke(screenData?.screen_id.toString())


}

private const val TAG = "BaseActivity"

//class ne access krva mate open keyword use thy

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    fun screenOpened(screenName: String) = lifecycleScope.launch {
        delay(2000)
        checkForPendingPopup(screenName) {
            userLastSeenViewModel.removeAPopup(it)
        }
    }

    private val userLastSeenViewModel: UserLastSeenViewModel by viewModels()



    override fun onStart() {
        super.onStart()
        userLastSeenViewModel.start()
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { it ->

                if (it) {

                    Log.d(TAG, "onStartsddsfd: "+"permissiongranted")
                } else {
                    /*Log.d(TAG, "onStartsddsfd: "+"permissiongrantedDeneail")
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri: Uri = Uri.fromParts("package", "com.foreverinlove", null)
                    intent.data = uri
                    startActivity(intent)*/

                }


            }

            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

    }


    override fun onResume() {
        super.onResume()

        userLastSeenViewModel.makeOnline()
        InternetConnectivityHelper.checkConnection(applicationContext) {
            if (!it) startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()

        userLastSeenViewModel.makeOffline()
        InternetConnectivityHelper.cancelListener()

    }

    override fun onDestroy() {
        Utility.hideProgressBar()
        super.onDestroy()

        userLastSeenViewModel.makeOffline()
        InternetConnectivityHelper.cancelListener()
    }

}
