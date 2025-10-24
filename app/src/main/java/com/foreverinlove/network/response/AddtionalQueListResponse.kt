package com.foreverinlove.network.response


data class AddtionalQueListResponse(

    val data: AddtionalQueListData,
    val message: String,
    val status: Int

)

data class AddtionalQueListData(

    val pets: List<AddtionalQueObject>,
    val education: List<AddtionalQueObject>,
    val dietary_life_style: List<AddtionalQueObject>,
    val drugs: List<AddtionalQueObject>,
    val language: List<AddtionalQueObject>,
    val life_style: List<AddtionalQueObject>,
    val drink: List<AddtionalQueObject>,
    val religion: List<AddtionalQueObject>,
    val horoscope: List<AddtionalQueObject>,
    val relationship_status: List<AddtionalQueObject>,
    val looking_for: List<AddtionalQueObject>,
    val covid_vaccine: List<AddtionalQueObject>,
    val political_leaning: List<AddtionalQueObject>,
    val interests: List<AddtionalQueObject>,
    val first_date_ice_breaker: List<AddtionalQueObject>,
    val arts: List<AddtionalQueObject>,
    val smoking: List<AddtionalQueObject>,
    val height: List<AddtionalQueObject>,
   // val gender: List<AddtionalQueObject>,

    )


data class AddtionalQueObject(

    val updated_at: String,
    val created_at: String,
    val id: Int,
    val title: String

)


data class GenderObject(

    val id: Int,
    val title: String

):java.io.Serializable












