package uz.juo.hobee.models

data class ItemMedIdPrice(
    val address: String,
    val distance: Double?,
    val end_time: String,
    val id: Int,
    val latitude: Any,
    val longitude: Any,
    val name: String,
    val phone: String,
    val price: String,
    val start_time: String
)