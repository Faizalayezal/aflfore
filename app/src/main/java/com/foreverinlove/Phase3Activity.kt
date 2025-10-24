package com.foreverinlove

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.adapter.AddtionalAdepter
import com.foreverinlove.adapter.PhaseType
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityPhase3Binding
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.objects.PhaseListObject
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.MyListDataHelper
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.PhaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Phase3Activity : BaseActivity(), AddtionalAdepter.SelectedListener {
    private lateinit var binding: ActivityPhase3Binding
    private var tempUserDataObject: TempUserDataObject? = null
    private val viewModel: PhaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phase3)
        binding= ActivityPhase3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened( "Phase3")
        window.statusBarColor = ContextCompat.getColor(this, R.color.datapiker)
        viewModel.start(true)


        val adapterList = ArrayList<PhaseListObject>()

        MyListDataHelper.getAllData()?.let{
            adapterList.add(PhaseListObject("Political Views",it.political_leaning,ChipGroupHelper.StyleTypes.Phase3))
            adapterList.add(PhaseListObject("Religion",it.religion,ChipGroupHelper.StyleTypes.Phase3))

        }

        binding.btnnext.setOnClickListener {

            tempUserDataObject?.let { it1 ->
                it1.ukeypolitical_leaning = selectedPoliticle
                it1.ukeyreligion = selectedReligion
                showProgressBar()
                viewModel.updateUserData(it1) {
                    Utility.hideProgressBar()
                    if (it) {
                        startActivity(Intent(this, Phase4Activity::class.java))
                        //finish()

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


        binding.skip.setOnClickListener {
            startActivity(Intent(this, Phase4Activity::class.java))
            finish()


        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private val listuser = ArrayList<PhaseListObject>()
    private lateinit var AddtionalistAdapter: AddtionalAdepter

    private fun notedata(adapterList: java.util.ArrayList<PhaseListObject>) {
        listuser.addAll(adapterList)

        lifecycleScope.launch {
            dataStoreGetUserData().firstOrNull {

                listuser.forEachIndexed { index, phaseListObject ->

                    when (phaseListObject.name) {
                        "Political Views" -> listuser[index].alreadySelectedIds = it.ukeypolitical_leaning
                        "Religion" -> listuser[index].alreadySelectedIds = it.ukeyreligion
                    }

                }

                AddtionalistAdapter = AddtionalAdepter(
                    this@Phase3Activity,
                    this@Phase3Activity,
                    listuser,
                    PhaseType.Phase3
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

    private var selectedPoliticle = ""
    private var selectedReligion = ""
    override fun onSelected(
        selectedIdList: ArrayList<String>,
        title: String,
        allDataList: List<AddtionalQueObject>
    ) {
        when (title) {
            "Political Views" -> {
                selectedPoliticle = ""
            }
            "Religion" -> {
                selectedReligion = ""
            }


        }
        for (i in selectedIdList.indices) {
            for (j in allDataList.indices) {
                if (title == "Political Views") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedPoliticle = if (selectedPoliticle == "") allDataList[j].id.toString()
                        else selectedPoliticle + "," + allDataList[j].id
                    }
                } else if (title == "Religion") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedReligion = if (selectedReligion == "") allDataList[j].id.toString()
                        else selectedReligion + "," + allDataList[j].id
                    }
                }
            }
        }
    }
}