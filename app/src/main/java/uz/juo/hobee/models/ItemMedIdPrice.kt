package uz.juo.hobee.models

import java.io.Serializable

data class ItemMedIdPrice(
    val address: String? = "",
    val distance: Double? = null,
    val end_time: String? = "",
    val id: Int? = null,
    val latitude: Any? = null,
    val longitude: Any? = null,
    val name: String = "",
    val phone: String = "",
    val price: String = "",
    val start_time: String? = ""
) : Serializable