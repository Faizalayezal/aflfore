package com.foreverinlove

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.databinding.ActivityIdVerifyBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import com.foreverinlove.viewmodels.BioViewModel
import com.foreverinlove.viewmodels.WhoViewedMeViewModel
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.token.TokenExpirationHandler
import com.onfido.android.sdk.capture.ui.options.FlowStep
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder
import com.onfido.android.sdk.capture.upload.Captures
import com.onfido.workflow.OnfidoWorkflow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IdVerifyActivity : AppCompatActivity() {
    private var binding: ActivityIdVerifyBinding? = null
    private var client: Onfido? = null

    private lateinit var onfidoWorkflow: OnfidoWorkflow
    private val viewModel: BioViewModel by viewModels()
    var sdkToken:String=""

//private lateinit var onfidoWorkflow: OnfidoWorkflow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdVerifyBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel.start()
        viewModel.callApiData{sdkToken->
            this.sdkToken =sdkToken
        }
        client = OnfidoFactory.create(this).client
        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    Log.d("TAG", "onCreadfgdfgte: "+it)

                }
        }

        lifecycleScope.launch {
            viewModel.onFidoListConversion.collect {
                when (it) {
                    BioViewModel.OnfidoResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is BioViewModel.OnfidoResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                    }

                    is BioViewModel.OnfidoResponseEvent.Loading -> {
                        showProgressBar()

                    }

                    is BioViewModel.OnfidoResponseEvent.Success -> {
                        Utility.hideProgressBar()

                    }
                }
            }
        }




        binding?.continueButton?.setOnClickListener {
            startFlow()

        }

    }

    private fun startFlow() {
        val flowStepsWithOptions = arrayOf(
            FlowStep.WELCOME,
            FlowStep.CAPTURE_DOCUMENT,
            FlowStep.CAPTURE_FACE,
           // FlowStep.PROOF_OF_ADDRESS,
            // FaceCaptureStepBuilder.forPhoto().build(),
            FlowStep.FINAL
        )
        startFlow(flowStepsWithOptions)

    }


    private fun startFlow(flowSteps: Array<FlowStep>) {
        Log.d("TAG", "stafhfghfghrtFlow: "+sdkToken)
        val onfidoConfig = OnfidoConfig.builder(applicationContext)
            .withSDKToken(sdkToken)
            .withCustomFlow(flowSteps)
            .build()
        client?.startActivityForResult(this@IdVerifyActivity, 1, onfidoConfig)
       // startActivityForResult(onfidoWorkflow.createIntent(onfidoConfig), 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        client?.handleActivityResult(resultCode, data, object : Onfido.OnfidoResultListener {
            override fun userExited(exitCode: ExitCode) {
                showToast("User cancelled.")
            }

            override fun userCompleted(captures: Captures) {

                lifecycleScope.launch {
                    val job = launch {
                        val tempData = TempUserDataObject(
                            id_verification = "1",
                        )
                        application.dataStoreSetUserData(tempData)

                    }
                    job.join()
                    startActivity(
                        Intent(
                            this@IdVerifyActivity,
                            ComplatedRegisterActivity::class.java
                        )
                    )
                }



            }

            override fun onError(e: OnfidoException) {
                e.printStackTrace()
                showToast(e.localizedMessage)
            }

        })



    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun startCheck(captures: Captures) {
        Log.d("TAG", "dgfgdfgdfgstartCheck: "+captures.document)
        Log.d("TAG", "dgfgdfgdfgstartCheck:97 "+captures.poa)
        Log.d("TAG", "dgfgdfgdfgstartCheck:98 "+captures.face)
        //Call your back end to initiate the check https://github.com/onfido/onfido-android-sdk#2-creating-a-check
    }
}
