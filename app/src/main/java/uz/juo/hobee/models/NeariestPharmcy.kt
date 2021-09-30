package uz.juo.hobee.models

data class NeariestPharmcy(
    val id: Int,
    val name: String,
    val start_time: String,
    val end_time: String,
    val distance: Double?
)