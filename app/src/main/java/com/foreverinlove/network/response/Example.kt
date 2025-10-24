package com.foreverinlove.network.response

data class Example(
    val data: Data?,
    val message: String?,
    val status: Int?
) : java.io.Serializable {
    data class Data(
        val arts: List<Art?>?,
        val covid_vaccine: List<CovidVaccine?>?,
        val dietary_life_style: List<DietaryLifeStyle?>?,
        val drink: List<Drink?>?,
        val drugs: List<Drug?>?,
        val education: List<Education?>?,
        val first_date_ice_breaker: List<FirstDateIceBreaker?>?,
        val horoscope: List<Horoscope?>?,
        val interests: List<Interest?>?,
        val language: List<Language?>?,
        val life_style: List<LifeStyle?>?,
        val looking_for: List<LookingFor?>?,
        val pets: List<Pet?>?,
        val political_leaning: List<PoliticalLeaning?>?,
        val relationship_status: List<RelationshipStatu?>?,
        val religion: List<Religion?>?
    ) : java.io.Serializable {
        data class Art(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class CovidVaccine(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class DietaryLifeStyle(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Drink(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Drug(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Education(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class FirstDateIceBreaker(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Horoscope(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Interest(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Language(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class LifeStyle(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class LookingFor(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Pet(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class PoliticalLeaning(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class RelationshipStatu(
            val created_at: String?,
            val id: Int?,
            val title: String?,
            val updated_at: String?
        ) : java.io.Serializable

        data class Religion(
            val created_at: String?,
            val id: Int?,
            val titel: String?,
            val updated_at: String?
        ) : java.io.Serializable
    }
}