package com.foreverinlove

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.adapter.AddtionalAdepter
import com.foreverinlove.adapter.PhaseType
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityPhase2Binding
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
class Phase2Activity : BaseActivity(), AddtionalAdepter.SelectedListener {
    private lateinit var binding: ActivityPhase2Binding

    private var tempUserDataObject: TempUserDataObject? = null
    private val viewModel: PhaseViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhase2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened( "Phase2")
        window.statusBarColor = ContextCompat.getColor(this, R.color.datapiker)

        viewModel.start(true)




        val adapterList = ArrayList<PhaseListObject>()



        MyListDataHelper.getAllData()?.let {
            adapterList.add(
                PhaseListObject(
                    "Dietary LifeStyles",
                    it.dietary_life_style,
                    ChipGroupHelper.StyleTypes.Phase2, maxSelected = 4
                )
            )
            adapterList.add(
                PhaseListObject(
                    "Interests",
                    it.interests,
                    ChipGroupHelper.StyleTypes.Phase2, maxSelected = 4
                )
            )
            adapterList.add(PhaseListObject("Pets", it.pets, ChipGroupHelper.StyleTypes.Phase2, maxSelected = 4))
            adapterList.add(
                PhaseListObject(
                    "Horoscope",
                    it.horoscope,
                    ChipGroupHelper.StyleTypes.Phase2
                )
            )
        }

        binding.btnnext.setOnClickListener {

            tempUserDataObject?.let { it1 ->
                it1.ukeydietary_lifestyle = selectedDietaryLifestyle
                it1.ukeyinterests = selectedInterests
                it1.ukeypets = selectedPets
                it1.ukeyhoroscope = selectedHoroscope
                showProgressBar()
                viewModel.updateUserData(it1) {
                    Utility.hideProgressBar()
                    if (it) {
                        startActivity(Intent(this, Phase3Activity::class.java))
                       // finish()

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
            startActivity(Intent(this, Phase3Activity::class.java))
            finish()


        }
        binding.imgBack.setOnClickListener {
            onBackPressed()

        }

    }

    private val listUser = ArrayList<PhaseListObject>()

    private lateinit var addtionalistAdapter: AddtionalAdepter

    private fun notedata(adapterList: java.util.ArrayList<PhaseListObject>) {
        listUser.addAll(adapterList)


        lifecycleScope.launch {
            dataStoreGetUserData().firstOrNull {

                listUser.forEachIndexed { index, phaseListObject ->

                    when (phaseListObject.name) {
                        "Dietary LifeStyles" -> listUser[index].alreadySelectedIds = it.ukeydietary_lifestyle
                        "Interests" -> listUser[index].alreadySelectedIds = it.ukeyinterests
                        "Pets" -> listUser[index].alreadySelectedIds = it.ukeypets
                        "Horoscope" -> listUser[index].alreadySelectedIds = it.ukeyhoroscope
                    }

                }

                addtionalistAdapter = AddtionalAdepter(
                    this@Phase2Activity,
                    this@Phase2Activity,
                    listUser,
                    PhaseType.Phase2
                )
                binding.recyQue.adapter = addtionalistAdapter

                true
            }
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out)

    }

    private var selectedDietaryLifestyle = ""
    private var selectedInterests = ""
    private var selectedHobbies = ""
    private var selectedPets = ""
    private var selectedHoroscope = ""

    override fun onSelected(
        selectedIdList: ArrayList<String>,
        title: String,
        allDataList: List<AddtionalQueObject>,

        ) {
        when (title) {
            "Dietary LifeStyles" -> {
                selectedDietaryLifestyle = ""
            }
            "Interests" -> {
                selectedInterests = ""
            }
            "Hobby" -> {
                selectedHobbies = ""
            }
            "Pets" -> {
                selectedPets = ""
            }
            "Horoscope" -> {
                selectedHoroscope = ""
            }

        }
        for (i in selectedIdList.indices) {
            for (j in allDataList.indices) {
                if (title == "Dietary LifeStyles") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedDietaryLifestyle =
                        if (selectedDietaryLifestyle == "") allDataList[j].id.toString()
                        else selectedDietaryLifestyle + "," + allDataList[j].id
                    }
                } else if (title == "Interests") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedInterests = if (selectedInterests == "") allDataList[j].id.toString()
                        else selectedInterests + "," + allDataList[j].id
                    }
                } else if (title == "Hobby") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedHobbies = if (selectedHobbies == "") allDataList[j].id.toString()
                        else selectedHobbies + "," + allDataList[j].id
                    }
                } else if (title == "Pets") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedPets = if (selectedPets == "") allDataList[j].id.toString()
                        else selectedPets + "," + allDataList[j].id
                    }
                } else if (title == "Horoscope") {
                    if (selectedIdList[i] == allDataList[j].title) {
                        selectedHoroscope = if (selectedHoroscope == "") allDataList[j].id.toString()
                        else selectedHoroscope + "," + allDataList[j].id
                    }
                }
            }
        }

    }
}