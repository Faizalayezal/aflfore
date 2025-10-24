package com.foreverinlove.screen.activity


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.DetailsPhaseAdepter
import com.foreverinlove.adapter.ImageListHorizontalAdapter
import com.foreverinlove.adapter.IndicatorAdapter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityDetailProfileScreenBinding
import com.foreverinlove.network.response.DiscoverData
import com.foreverinlove.network.response.DiscoverImage
import com.foreverinlove.network.response.UsersDetails
import com.foreverinlove.objects.DetailPhase
import com.foreverinlove.objects.ImageOrVideoData
import com.foreverinlove.utility.IndicatorHelper
import com.foreverinlove.utility.MyListDataHelper
import com.foreverinlove.viewmodels.CardDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toDrawable


@AndroidEntryPoint
class DetailProfileScreenActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailProfileScreenBinding

    var imageListAdapter: ImageListHorizontalAdapter? = null
    private var listImage: ArrayList<ImageOrVideoData>? = null

    var isSuperLikeAllowed = false
    var isLikeAllowed = false

    var data: DiscoverData? = null
    private val viewModel: CardDetailViewModel by viewModels()
    private var UserDetailsData: UsersDetails? = null
    private var UserDetailsDatPersonalChat: UsersDetails? = null
    private var userdata: DiscoverData? = null
    private var userliked: Int? = null
    private var userSuperliked: Int? = null
    private var UserDetailsDataButton: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("ProfileDetail")
        userdata = intent.getSerializableExtra("userdata") as DiscoverData?

        userSuperliked = intent.getIntExtra("userSuperlike", 0)
        userliked = intent.getIntExtra("userLikes", 0)


        Log.d("TAG", "onCreate3213: "+userdata)

        UserDetailsData = intent.getSerializableExtra("userDetailsdata") as UsersDetails?
        UserDetailsDatPersonalChat = intent.getSerializableExtra("userDetailsdatapersonalChat") as UsersDetails?


        UserDetailsDataButton = intent.getBooleanExtra("btnVisibility", true)



        userdata?.let {

            val lookingForArray = ArrayList<String>()
            val DietaryLifestyle = ArrayList<String>()
            val Pets = ArrayList<String>()
            val Art = ArrayList<String>()
            val Language = ArrayList<String>()
            val interest = ArrayList<String>()

            userdata?.user_looking_for?.forEach {
                if(!it.title.isNullOrEmpty())lookingForArray.add(it.title?:"")
            }
            userdata?.user_dietary_lifestyle?.forEach {
                if(!it.title.isNullOrEmpty())DietaryLifestyle.add(it.title?:"")
            }
            userdata?.user_pets?.forEach {
                if(!it.title.isNullOrEmpty())Pets.add(it.title?:"")
            }
            userdata?.user_arts?.forEach {
                if(!it.title.isNullOrEmpty())Art.add(it.title?:"")
            }
            userdata?.user_language?.forEach {
                if(!it.title.isNullOrEmpty())Language.add(it.title?:"")
            }
            userdata?.user_interests?.forEach {
                if(!it.title.isNullOrEmpty())interest.add(it.title?:"")
            }

            dataloade(
                images = userdata?.user_images ?: listOf(),
                firstName = userdata?.first_name ?: "",
                age = userdata?.age ?: "",
                address = userdata?.address ?: "",
                jobTitle = userdata?.job_title ?: "",
                gender = userdata?.gender ?: "",
                about = userdata?.about ?: "",
                height = userdata?.height ?: "",
                profileVideo = userdata?.profile_video ?: "",

                //map ni ander j return kro ne ene array kri nakhe
                /*dietary_lifestyle = userdata?.user_dietary_lifestyle?.map {
                    it.title ?: ""
                },*/
                looking_for = lookingForArray,
                dietary_lifestyle=DietaryLifestyle,
                pets = Pets,
                arts = Art,
                language = Language,
                interests =interest,

                drink = userdata?.user_drink?.title ?: "",
                drugs = userdata?.user_drugs?.title ?: "",
                horoscope = userdata?.user_horoscope?.title ?: "",
                religion = userdata?.user_religion?.title ?: "",
                political_leaning = userdata?.user_political_leaning?.title ?: "",
                relationship_status = userdata?.user_relationship_status?.title ?: "",
                educations = userdata?.user_educations?.title ?: "",
                life_style = userdata?.user_life_style?.title ?: "",
                first_date_ice_breaker = userdata?.user_first_date_ice_breaker?.title ?: "",
                covid_vaccine = userdata?.user_covid_vaccine?.title ?: "",
                smoking = userdata?.user_smoking?.title ?: "",
            )

        }



        UserDetailsData?.let {
            if (UserDetailsDataButton == true) {
                binding.imgHeart.visibility = View.GONE
                binding.cancel.visibility = View.GONE
                binding.superlike.visibility = View.GONE
            }
            if (UserDetailsDataButton == false) {
                binding.imgHeart.visibility = View.VISIBLE
                binding.cancel.visibility = View.VISIBLE
                binding.superlike.visibility = View.VISIBLE
            }

            val lookingForArray = ArrayList<String>()
            val DietaryLifestyle = ArrayList<String>()
            val Pets = ArrayList<String>()
            val Art = ArrayList<String>()
            val Language = ArrayList<String>()
            val interest = ArrayList<String>()

            UserDetailsData?.users?.user_looking_for?.forEach {
                if(!it.title.isNullOrEmpty())lookingForArray.add(it.title)
            }
            UserDetailsData?.users?.user_dietary_lifestyle?.forEach {
                if(!it.title.isNullOrEmpty())DietaryLifestyle.add(it.title)
            }
            UserDetailsData?.users?.user_pets?.forEach {
                if(!it.title.isNullOrEmpty())Pets.add(it.title)
            }
            UserDetailsData?.users?.user_arts?.forEach {
                if(!it.title.isNullOrEmpty())Art.add(it.title)
            }
            UserDetailsData?.users?.user_language?.forEach {
                if(!it.title.isNullOrEmpty())Language.add(it.title)
            }
            UserDetailsData?.users?.user_interests?.forEach {
                if(!it.title.isNullOrEmpty())interest.add(it.title)
            }

            dataloade(
                images = UserDetailsData?.users?.user_images ?: listOf(),
                firstName = UserDetailsData?.users?.first_name ?: "",
                age = UserDetailsData?.users?.age ?: "",
                address = UserDetailsData?.users?.address ?: "",
                jobTitle = UserDetailsData?.users?.job_title ?: "",
                about = UserDetailsData?.users?.about ?: "",
                gender = UserDetailsData?.users?.gender ?: "",
                height = UserDetailsData?.users?.height ?: "",
                profileVideo = UserDetailsData?.users?.profile_video ?: "",


                looking_for = lookingForArray,
                dietary_lifestyle=DietaryLifestyle,
                pets = Pets,
                arts = Art,
                language = Language,
                interests =interest,

                drink = UserDetailsData?.users?.user_drink?.title ?: "",
                drugs = UserDetailsData?.users?.user_drugs?.title ?: "",
                horoscope = UserDetailsData?.users?.user_horoscope?.title ?: "",
                religion = UserDetailsData?.users?.user_religion?.title ?: "",
                political_leaning = UserDetailsData?.users?.user_political_leaning?.title ?: "",
                educations = UserDetailsData?.users?.user_educations?.title ?: "",
                relationship_status = UserDetailsData?.users?.user_relationship_status?.title ?: "",
                life_style = UserDetailsData?.users?.user_life_style?.title ?: "",
                first_date_ice_breaker = UserDetailsData?.users?.user_first_date_ice_breaker?.title
                    ?: "",
                covid_vaccine = UserDetailsData?.users?.user_covid_vaccine?.title ?: "",
                smoking = UserDetailsData?.users?.user_smoking?.title ?: "",
            )

        }
        UserDetailsDatPersonalChat?.let {
            if (UserDetailsDataButton == true) {
                binding.imgHeart.visibility = View.GONE
                binding.cancel.visibility = View.GONE
                binding.superlike.visibility = View.GONE
            }
            if (UserDetailsDataButton == false) {
                binding.imgHeart.visibility = View.VISIBLE
                binding.cancel.visibility = View.VISIBLE
                binding.superlike.visibility = View.VISIBLE
            }
            val lookingForArray = ArrayList<String>()
            val dietaryLifestyle = ArrayList<String>()
            val pets = ArrayList<String>()
            val art = ArrayList<String>()
            val language = ArrayList<String>()
            val interest = ArrayList<String>()

            UserDetailsDatPersonalChat?.users?.user_looking_for?.forEach {
                if(!it.title.isNullOrEmpty())lookingForArray.add(it.title)
            }
            UserDetailsDatPersonalChat?.users?.user_dietary_lifestyle?.forEach {
                if(!it.title.isNullOrEmpty())dietaryLifestyle.add(it.title)
            }
            UserDetailsDatPersonalChat?.users?.user_pets?.forEach {
                if(!it.title.isNullOrEmpty())pets.add(it.title)
            }
            UserDetailsDatPersonalChat?.users?.user_arts?.forEach {
                if(!it.title.isNullOrEmpty())art.add(it.title)
            }
            UserDetailsDatPersonalChat?.users?.user_language?.forEach {
                if(!it.title.isNullOrEmpty())language.add(it.title)
            }
            UserDetailsDatPersonalChat?.users?.user_interests?.forEach {
                if(!it.title.isNullOrEmpty())interest.add(it.title)
            }


            dataloade(
                images = UserDetailsDatPersonalChat?.users?.user_images ?: listOf(),
                firstName = UserDetailsDatPersonalChat?.users?.first_name ?: "",
                age = UserDetailsDatPersonalChat?.users?.age ?: "",
                address = UserDetailsDatPersonalChat?.users?.address ?: "",
                jobTitle = UserDetailsDatPersonalChat?.users?.job_title ?: "",
                about = UserDetailsDatPersonalChat?.users?.about ?: "",
                gender = UserDetailsDatPersonalChat?.users?.gender ?: "",
                height = UserDetailsDatPersonalChat?.users?.height ?: "",
                profileVideo = UserDetailsDatPersonalChat?.users?.profile_video ?: "",


                looking_for = lookingForArray,
                dietary_lifestyle=dietaryLifestyle,
                pets = pets,
                arts = art,
                language = language,
                interests =interest,

                drink = UserDetailsDatPersonalChat?.users?.user_drink?.title ?: "",
                drugs = UserDetailsDatPersonalChat?.users?.user_drugs?.title ?: "",
                horoscope = UserDetailsDatPersonalChat?.users?.user_horoscope?.title ?: "",
                religion = UserDetailsDatPersonalChat?.users?.user_religion?.title ?: "",
                political_leaning = UserDetailsDatPersonalChat?.users?.user_political_leaning?.title
                    ?: "",
                relationship_status = UserDetailsDatPersonalChat?.users?.user_relationship_status?.title
                    ?: "",
                educations = UserDetailsDatPersonalChat?.users?.user_educations?.title ?: "",
                life_style = UserDetailsDatPersonalChat?.users?.user_life_style?.title ?: "",
                first_date_ice_breaker = UserDetailsDatPersonalChat?.users?.user_first_date_ice_breaker?.title
                    ?: "",
                covid_vaccine = UserDetailsDatPersonalChat?.users?.user_covid_vaccine?.title ?: "",
                smoking = UserDetailsDatPersonalChat?.users?.user_smoking?.title ?: "",
            )

        }



        userSuperliked?.let {
            userSuperliked =
                (userSuperliked ?: 0) - 1

            isSuperLikeAllowed = (userSuperliked ?: 0) >= 0
        }


        UserDetailsData?.remaining_super_likes_count?.let {
            UserDetailsData?.remaining_super_likes_count =
                (UserDetailsData?.remaining_super_likes_count ?: 0) - 1

            isSuperLikeAllowed = (UserDetailsData?.remaining_super_likes_count ?: 0) >= 0

        }



        userliked?.let {
            userliked =
                (userliked ?: 0) - 1
            isLikeAllowed = (userliked ?: 0) >= 0

        }
        UserDetailsData?.remaining_likes_count?.let {
            UserDetailsData?.remaining_likes_count =
                (UserDetailsData?.remaining_likes_count ?: 0) - 1
            isLikeAllowed = (UserDetailsData?.remaining_likes_count ?: 0) >= 0



        }

        binding.cancel.setOnClickListener {
            setResult(5)
            finish()
        }
        binding.imgHeart.setOnClickListener {
            if (isLikeAllowed) {
                setResult(6)
                finish()
            } else {
                successfulApplied()
            }

        }

        binding.superlike.setOnClickListener {

            if (isSuperLikeAllowed) {
              //  val intent = Intent()
              //  intent.putExtra("superlike_status", userdata)
               // setResult(7,intent)
                setResult(7)
                finish()
            } else {
                startActivity(
                    Intent(
                        this@DetailProfileScreenActivity,
                        SuperLikeActivity::class.java
                    )
                )
                finish()
            }
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }



        binding.nested.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            imageListAdapter?.SampleAdapter()


        }
        binding.userimageRecy.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            imageListAdapter?.offVideo()
        }


        if(UserDetailsData?.is_matched=="1" || UserDetailsDatPersonalChat?.is_matched=="1"){

            Log.d("TAG", "onCreate375: "+"alredyMatch")

        }else{
            lifecycleScope.launch {
                delay(1000)
                if ((UserDetailsData?.users?.id ?: 0) == 0) {

                    viewModel.addViewToUser((userdata?.id ?: 0).toString())
                } else {
                    viewModel.addViewToUser((UserDetailsData?.users?.id ?: 0).toString())

                }
            }
        }




    }


    @SuppressLint("SetTextI18n")
    private fun dataloade(
        images: List<DiscoverImage?>?,
        firstName: String,
        age: String,
        address: String,
        jobTitle: String,
        about: String,
        height: String,
        gender: String,

        educations: String?,
        looking_for: List<String>?,
        dietary_lifestyle: List<String>?,
        pets: List<String>?,
        arts: List<String>?,
        language: List<String>?,
        interests: List<String>?,

        drink: String,
        drugs: String,
        profileVideo: String,
        horoscope: String,
        religion: String,
        political_leaning: String,
        relationship_status: String,
        life_style: String,
        first_date_ice_breaker: String,
        covid_vaccine: String,
        smoking: String,
    ) {

        slideingImage(
            images?.getOrNull(0)?.url ?: "",
            images?.getOrNull(1)?.url ?: "",
            images?.getOrNull(2)?.url ?: "",
            images?.getOrNull(3)?.url ?: "",
            images?.getOrNull(4)?.url ?: "",
            images?.getOrNull(5)?.url ?: "",
            profileVideo

        )
        Log.d("TAG", "dadsdsdtaloade: " + profileVideo)


        //MainDaat
        val mainArrayList = ArrayList<DetailPhase>()
        if (height != "") {

            MyListDataHelper.getAllData()?.let {
                for (i in it.height) {
                    if (i.id == height.toIntOrNull()) {
                        mainArrayList.add(DetailPhase(R.mipmap.dhight, i.title, arrayOf("")))
                    }
                }
            }


        }
        if (!educations.isNullOrEmpty()) {

            mainArrayList.add(DetailPhase(R.mipmap.deducation, educations, arrayOf()))
            //  mainArrayList.add(DetailPhase(R.mipmap.deducation, educations, arrayOf("")))

        }
        if (gender != "") {
            mainArrayList.add(DetailPhase(R.mipmap.dgender, gender, arrayOf("")))
        }
        if (!language.isNullOrEmpty()) {
            language.toTypedArray().let {//list ne array ma convert krinakhe
                mainArrayList.add(DetailPhase(R.mipmap.language, "", it))
            }

        }
        if (!looking_for.isNullOrEmpty()) {
            looking_for.toTypedArray().let {
                mainArrayList.add(DetailPhase(R.mipmap.lookingd, "", it))
            }

        }
        /* life_style?.toTypedArray()?.let {
             mainArrayList.add(DetailPhase(R.mipmap.language, "", it))
         }*/
        if (life_style != "") {
            mainArrayList.add(DetailPhase(R.mipmap.casual, life_style, arrayOf("")))
        }
        if (relationship_status != "") {
            mainArrayList.add(DetailPhase(R.mipmap.reletionship, relationship_status, arrayOf("")))
        }

        binding.phaseGRV.adapter = DetailsPhaseAdepter(this, mainArrayList)

        //phase1

        val phase1ArrayList = ArrayList<DetailPhase>()
        if (smoking != "") {
            phase1ArrayList.add(DetailPhase(R.mipmap.dsmoke, smoking, arrayOf("")))
        }
        if (drink != "") {
            phase1ArrayList.add(DetailPhase(R.mipmap.ddrink, drink, arrayOf("")))
        }
        if (drugs != "") {
            phase1ArrayList.add(DetailPhase(R.mipmap.ddrugs, drugs, arrayOf("")))
        }
        if (phase1ArrayList.isEmpty()) {

            binding.view2.visibility = View.GONE
            binding.phase.visibility = View.GONE
            binding.phase1GRV.visibility = View.GONE
        }
      //  binding.phase1GRV.adapter = DetailsPhaseAdepter(this, phase1ArrayList)


        //phase2
      //  val phase2ArrayList = ArrayList<DetailPhase>()

        if (!dietary_lifestyle.isNullOrEmpty()) {
            dietary_lifestyle.toTypedArray().let {
                phase1ArrayList.add(DetailPhase(R.mipmap.dveg, "", it))
            }
        }
        if (!interests.isNullOrEmpty()) {
            interests.toTypedArray().let {
                phase1ArrayList.add(DetailPhase(R.mipmap.dart, "", it))
            }

        }
        if (!pets.isNullOrEmpty()) {
            pets.toTypedArray().let {
                phase1ArrayList.add(DetailPhase(R.mipmap.dpets, "", it))
            }
        }
        if (!horoscope.isNullOrEmpty()) {
            phase1ArrayList.add(DetailPhase(R.mipmap.dleo, horoscope, arrayOf("")))
        }



        if (religion != "") {
            phase1ArrayList.add(DetailPhase(R.mipmap.dreligeion, religion, arrayOf("")))
        }
        if (political_leaning != "") {
            phase1ArrayList.add(DetailPhase(R.mipmap.dpolitical, political_leaning, arrayOf("")))
        }

        if (!covid_vaccine.isNullOrEmpty()) {
            phase1ArrayList.add(DetailPhase(R.mipmap.dvaccine, covid_vaccine, arrayOf("")))
        }
        if (!first_date_ice_breaker.isNullOrEmpty()) {
            phase1ArrayList.add(
                DetailPhase(
                    R.mipmap.dfirstdate,
                    first_date_ice_breaker,
                    arrayOf("")
                )
            )
        }
        if (!arts.isNullOrEmpty()) {
            arts.toTypedArray().let {
                phase1ArrayList.add(DetailPhase(R.mipmap.dart, "", it))
            }

        }

        binding.phase1GRV.adapter = DetailsPhaseAdepter(this, phase1ArrayList)



        binding.apply {
            userName.text = firstName
            userAge.text = ", $age"
            userLocation.text = address
            userJob.text = jobTitle
            txtbio.text = about

        }
    }


    private fun slideingImage(
        image1: String?,
        image2: String?,
        image3: String?,
        image4: String?,
        image5: String?,
        image6: String?,
        profileVideo: String
    ) {

        listImage = ArrayList()



        listImage.let {

            if (image1 != null && image1 != "")
                listImage!!.add(ImageOrVideoData(image1, false))
            if (image2 != null && image2 != "")
                listImage!!.add(ImageOrVideoData(image2, false))
            if (image3 != null && image3 != "")
                listImage!!.add(ImageOrVideoData(image3, false))
            if (image4 != null && image4 != "")
                listImage!!.add(ImageOrVideoData(image4, false))
            if (image5 != null && image5 != "")
                listImage!!.add(ImageOrVideoData(image5, false))
            if (image6 != null && image6 != "")
                listImage!!.add(ImageOrVideoData(image6, false))
            if (profileVideo != "")
                listImage!!.add(ImageOrVideoData(profileVideo, true))


        }

        val indiList = ArrayList<Boolean>()
        listImage!!.forEachIndexed { i, _ ->
            if (i == 0) indiList.add(true)
            else indiList.add(false)
        }
        binding.apply {
            userimageRecy.apply {
                val snapHelper: SnapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(this)
                imageListAdapter = ImageListHorizontalAdapter(listImage!!)
                adapter = imageListAdapter


            }

            val cusAdapter = IndicatorAdapter(indiList)
            rcvIndicator.apply {
                adapter = cusAdapter
            }

            IndicatorHelper(userimageRecy, cusAdapter)
        }


    }

    private fun successfulApplied() {
        val dialog = Dialog(this@DetailProfileScreenActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogdiscover)
        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getColor(
                this@DetailProfileScreenActivity,
                R.color.sucessaplaytransperent
            ).toDrawable()
        )



        Glide.with(this@DetailProfileScreenActivity).load(R.mipmap.browseplansempty)
            .into(dialog.findViewById(R.id.imageView13))

        dialog.findViewById<AppCompatButton>(R.id.btnBrowsePlan).setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this@DetailProfileScreenActivity,
                    SubscriptionPlanActivity::class.java
                )
            )
        }

        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
            this@DetailProfileScreenActivity.onBackPressed()
        }


        dialog.show()

    }


    override fun onPause() {
        super.onPause()
        imageListAdapter?.offVideo()

    }

    override fun onDestroy() {
        super.onDestroy()
        imageListAdapter?.offVideo()

    }

    override fun onResume() {
        super.onResume()
        imageListAdapter?.onVideo()

    }

}





