package com.foreverinlove.screen.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.foreverinlove.R
import com.foreverinlove.adapter.SubScriptionAdapter
import com.foreverinlove.adapter.SubsType
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityPrimiumPlanBinding
import com.foreverinlove.network.response.SubscriptionPlanListResponse
import com.foreverinlove.objects.SubscriptionList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrimiumPlanActivity : BaseActivity() {
    private lateinit var binding: ActivityPrimiumPlanBinding

    private val listData = listOf(
        SubscriptionList(subsymbol = true, presubsymbol = true, desc1 = SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
        SubscriptionList(true,true, SpannableStringBuilder()),
    )

    val allPlanData: SubscriptionPlanListResponse? by lazy { intent.getSerializableExtra("allData") as? SubscriptionPlanListResponse? }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrimiumPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("SubscriptionPlanDetail")
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnbrowseplan.setOnClickListener {
            startActivity(Intent(this@PrimiumPlanActivity, SubscriptonPlan2Activity::class.java))
        }

        allPlanData?.data?.find { it.plan_type == "pro" }?.let {

            for (i in listData.indices) {
                when (i) {
                    0 -> listData[i].desc1 =
                        getCustomString("Can Access", " All Filters", "", R.color.orange)
                    1 -> listData[i].desc1 =
                        getCustomString("", "Unlimited ", "Likes & Dislikes", R.color.orange)
                    2 -> listData[i].desc1 = getCustomString(
                        it.super_like_par_day ?: "",
                        " Super Like Per Day",
                        "",
                        R.color.orange
                    )
                    3 -> listData[i].desc1 =
                        getCustomString("In App ", "Chat", "", R.color.orange)

                    4 -> listData[i].desc1 =
                        getCustomString("In App ", "Live", " Video Chat", R.color.orange)

                    5 -> listData[i].desc1 =
                        getCustomString("Group", " Video Calls", "", R.color.orange)
                    6 -> listData[i].desc1 =
                        getCustomString("", " Who Likes", " Me", R.color.orange)
                    7 -> listData[i].desc1 =
                        getCustomString("Can See", " Who's Viewed", " Me", R.color.orange)
                    8 -> listData[i].desc1 = getCustomString(
                        "",
                        "Send Private Chat",
                        " Request From Groups",
                        R.color.orange
                    )

                }
            }
            //setList()
        }

        subsctiptplan(ArrayList(listData), SubsType.PrimiumDesign)

    }


    private fun subsctiptplan(listData: ArrayList<SubscriptionList>, primiumDesign: SubsType) {
        binding.subrecy.apply {
            adapter = SubScriptionAdapter(this@PrimiumPlanActivity, listData, primiumDesign)
        }

    }

    private fun getCustomString(
        desc1: String,
        desc2: String,
        desc3: String,
        color: Int
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        if (desc1 != "") {
            val redSpannable1 = SpannableString(desc1)
            redSpannable1.setSpan(ForegroundColorSpan(Color.BLACK), 0, desc1.length, 0)
            builder.append(redSpannable1)
        }

        if (desc2 != "") {
            val redSpannable1 = SpannableString(desc2)
            redSpannable1.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(applicationContext, color)),
                0,
                desc2.length,
                0
            )
            builder.append(redSpannable1)
        }
        if (desc3 != "") {
            val redSpannable1 = SpannableString(desc3)
            redSpannable1.setSpan(ForegroundColorSpan(Color.BLACK), 0, desc3.length, 0)
            builder.append(redSpannable1)
        }
        return builder
    }




}