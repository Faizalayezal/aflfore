package com.foreverinlove.screen.activity


import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.adapter.ReportViewAdapter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityReportUserBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.ReasonData
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.ReportListViewmodel
import com.foreverinlove.viewmodels.ReportUnmatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportUserActivity : BaseActivity() {

    private val viewModel: ReportListViewmodel by viewModels()
    private val viewModelReport: ReportUnmatchViewModel by viewModels()

    private lateinit var otherUserId: String

    private lateinit var binding: ActivityReportUserBinding
    private lateinit var adepter: ReportViewAdapter
    private var tempUserDataObject: TempUserDataObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelReport.start()
        screenOpened("ReportList")
        otherUserId = intent.getStringExtra("otherUserId") ?: ""

        binding.btnSubmit.setOnClickListener {

            adepter.let {
                val selected = it.getSelectedId()
                if (selected == null) {
                } else {
                    viewModelReport.callApiData(otherUserId, selected.toString(), "unmatch")
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject = it

                }
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }


        lifecycleScope.launch {
            viewModel.conversionReason.collect {
                when (it) {
                    ReportListViewmodel.ReasonListEvent.Empty -> Utility.hideProgressBar()
                    is ReportListViewmodel.ReasonListEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        binding.rcvReportList.visibility = View.GONE
                    }

                    is ReportListViewmodel.ReasonListEvent.Loading -> {
                        showProgressBar()

                        binding.rcvReportList.visibility = View.VISIBLE
                    }

                    is ReportListViewmodel.ReasonListEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data.isEmpty()) {

                                binding.rcvReportList.visibility = View.GONE
                            } else {
                                binding.rcvReportList.visibility = View.VISIBLE


                                reportdata(it.result.data)
                            }
                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }

    }


    private fun reportdata(listData: ArrayList<ReasonData>) {
        adepter = ReportViewAdapter(this@ReportUserActivity, listData)
        binding.rcvReportList.adapter = adepter


    }


}