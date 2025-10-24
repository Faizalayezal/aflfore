package com.foreverinlove

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.adapter.AddtionalAdepter
import com.foreverinlove.adapter.PhaseType
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityPhase1Binding
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.network.Utility.hideProgressBar
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
class Phase1Activity : BaseActivity(), AddtionalAdepter.SelectedListener {
    private lateinit var binding: ActivityPhase1Binding
    private var tempUserDataObject: TempUserDataObject? = null
    private val viewModel: PhaseViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhase1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened( "Phase1")
        window.statusBarColor = ContextCompat.getColor(this, R.color.datapiker)
        viewModel.start(true)


        val adapterList = ArrayList<PhaseListObject>()

        MyListDataHelper.getAllData()?.let {
            adapterList.add(PhaseListObject("Smoking", it.smoking, ChipGroupHelper.StyleTypes.Phase1))
            adapterList.add(PhaseListObject("Drinking", it.drink, ChipGroupHelper.StyleTypes.Phase1))
            adapterList.add(PhaseListObject("Substances", it.drugs, ChipGroupHelper.StyleTypes.Phase1))
        }

        noteData(adapterList)

        binding.skip.setOnClickListener {
            startActivity(Intent(this, Phase2Activity::class.java))
            finish()

        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        //data set mate
        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject = it

                }
        }



        binding.btnnext.setOnClickListener {

            tempUserDataObject?.let { it1 ->
                it1.ukeysmoking = selectedSmoke
                it1.ukeydrugs = selectedDrug
                it1.ukeydrink = selectedDrink
                showProgressBar()
                viewModel.updateUserData(it1) {
                   hideProgressBar()
                    if (it) {
                        startActivity(Intent(this, Phase2Activity::class.java))
                       // finish()
                    } else {

                    }
                }
            }

        }




    }

    private val listUser = ArrayList<PhaseListObject>()
    private lateinit var addtionalistAdapter: AddtionalAdepter


    private fun noteData(adapterList: java.util.ArrayList<PhaseListObject>) {
        listUser.addAll(adapterList)

        lifecycleScope.launch {
            dataStoreGetUserData().firstOrNull {

                listUser.forEachIndexed { index, phaseListObject ->

                    when (phaseListObject.name) {
                        "Smoking" -> listUser[index].alreadySelectedIds = it.ukeysmoking
                        "Drinking" -> listUser[index].alreadySelectedIds = it.ukeydrink
                        "Substances" -> listUser[index].alreadySelectedIds = it.ukeydrugs
                    }

                }

                addtionalistAdapter = AddtionalAdepter(this@Phase1Activity, this@Phase1Activity, listUser, PhaseType.Phase1)
                binding.recyQue.adapter = addtionalistAdapter

                true
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out)
    }

    private var selectedSmoke = ""
    private var selectedDrink = ""
    private var selectedDrug = ""
    override fun onSelected(
        selectedIdList: ArrayList<String>,
        title: String,
        allDataList: List<AddtionalQueObject>
    ) {

        when (title) {
            "Smoking" -> {
                selectedSmoke = ""
            }
            "Drinking" -> {
                selectedDrink = ""
            }
            "Substances" -> {
                selectedDrug = ""
            }

        }
        for (i in selectedIdList.indices) {
            for (j in allDataList.indices) {
                if (title == "Smoking") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedSmoke = if (selectedSmoke == "") allDataList[j].id.toString()
                        else selectedSmoke + "," + allDataList[j].id
                        Log.d("TAG", "onSelected312: "+selectedSmoke)
                    }
                } else if (title == "Drinking") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedDrink = if (selectedDrink == "") allDataList[j].id.toString()
                        else selectedDrink + "," + allDataList[j].id
                        Log.d("TAG", "onSelected312: "+selectedDrink)

                    }
                } else if (title == "Substances") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedDrug = if (selectedDrug == "") allDataList[j].id.toString()
                        else selectedDrug + "," + allDataList[j].id
                        Log.d("TAG", "onSelected312: "+selectedDrug)

                    }
                }
            }
        }
    }

}
