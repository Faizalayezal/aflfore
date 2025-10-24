package com.foreverinlove

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.adapter.AddtionalAdepter
import com.foreverinlove.adapter.PhaseType
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityPhase4Binding
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.network.Utility.hideProgressBar
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.objects.PhaseListObject
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.screen.activity.MainActivity
import com.foreverinlove.utility.MyListDataHelper
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.PhaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Phase4Activity : BaseActivity(), AddtionalAdepter.SelectedListener {
    private lateinit var binding: ActivityPhase4Binding
    private var tempUserDataObject: TempUserDataObject? = null
    private val viewModel: PhaseViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phase4)
        binding = ActivityPhase4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened( "Phase4")
        window.statusBarColor = ContextCompat.getColor(this, R.color.datapiker)
        viewModel.start(true)
        binding.skip.setOnClickListener {
            finish()
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        val adapterList = ArrayList<PhaseListObject>()
        MyListDataHelper.getAllData()?.let {
            adapterList.add(
                PhaseListObject(
                    "Vaccinated",
                    it.covid_vaccine,
                    ChipGroupHelper.StyleTypes.Phase4
                )
            )
            adapterList.add(
                PhaseListObject(
                    "First Date Ice Breaker",
                    it.first_date_ice_breaker,
                    ChipGroupHelper.StyleTypes.Phase4
                )
            )

            adapterList.add(
                PhaseListObject(
                    "Art",
                    it.arts,
                    ChipGroupHelper.StyleTypes.Phase4, maxSelected = 4
                )
            )

        }

        binding.btnnext.setOnClickListener {

            tempUserDataObject?.let { it1 ->
                it1.ukeyfirst_date_ice_breaker = selectedFirstDateIceBreaker
                it1.ukeycovid_vaccine = selectedVaccinated
                it1.ukeyarts = selectedArt

                showProgressBar()
                viewModel.updateUserData(it1) {
                    hideProgressBar()
                    if (it) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {

                    }
                }
            }

        }

        //data set mate
        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject = it

                }
        }
        notedata(adapterList)

    }

    private val listuser = ArrayList<PhaseListObject>()
    private lateinit var AddtionalistAdapter: AddtionalAdepter

    private fun notedata(adapterList: java.util.ArrayList<PhaseListObject>) {
        listuser.addAll(adapterList)
        lifecycleScope.launch {
            dataStoreGetUserData().firstOrNull {

                listuser.forEachIndexed { index, phaseListObject ->
                    when (phaseListObject.name) {
                        "Vaccine" -> listuser[index].alreadySelectedIds = it.ukeycovid_vaccine
                        "First Date Ice Breaker" -> listuser[index].alreadySelectedIds =
                            it.ukeyfirst_date_ice_breaker
                        "Art" -> listuser[index].alreadySelectedIds = it.ukeyarts
                    }
                }

                AddtionalistAdapter = AddtionalAdepter(
                    this@Phase4Activity,
                    this@Phase4Activity,
                    listuser,
                    PhaseType.Phase4
                )
                binding.recyQue.adapter = AddtionalistAdapter

                true
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out)

    }

    private var selectedVaccinated = ""
    private var selectedFirstDateIceBreaker = ""
    private var selectedArt = ""
    override fun onSelected(
        selectedIdList: ArrayList<String>,
        title: String,
        allDataList: List<AddtionalQueObject>
    ) {
        when (title) {
            "Vaccine" -> {
                selectedVaccinated = ""
            }
            "First Date Ice Breaker" -> {
                selectedFirstDateIceBreaker = ""
            }
            "Art" -> {
                selectedArt = ""
            }

        }
        for (i in selectedIdList.indices) {
            for (j in allDataList.indices) {
                if (title == "Vaccine") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedVaccinated =
                            if (selectedVaccinated == "") allDataList[j].id.toString()
                            else selectedVaccinated + "," + allDataList[j].id
                    }
                } else if (title == "First Date Ice Breaker") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedFirstDateIceBreaker =
                            if (selectedFirstDateIceBreaker == "") allDataList[j].id.toString()
                            else selectedFirstDateIceBreaker + "," + allDataList[j].id
                    }
                } else if (title == "Art") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedArt = if (selectedArt == "") allDataList[j].id.toString()
                        else selectedArt + "," + allDataList[j].id
                    }
                }
            }
        }

    }

}
