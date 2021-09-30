package uz.juo.hobee.models

import java.io.Serializable

data class Item(
    val address: String="",
    val company_name: String="",
    val distance: Double?=0.0,
    val end_time: String="",
    val id: Int=0,
    val latitude: Double= 0.0,
    val longitude: Double= 0.0,
    val name: String="",
    val phone: String="",
    val region: String="",
    val start_time: String=""
):Serializable