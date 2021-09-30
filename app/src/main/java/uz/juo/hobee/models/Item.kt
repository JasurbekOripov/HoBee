package uz.juo.hobee.models

import java.io.Serializable

data class Item(
    val address: String,
    val company_name: String,
    val distance: Double?,
    val end_time: String,
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val phone: String,
    val region: String,
    val start_time: String
):Serializable