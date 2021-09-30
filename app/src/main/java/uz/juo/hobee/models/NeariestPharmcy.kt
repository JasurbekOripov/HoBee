package uz.juo.hobee.models

data class NeariestPharmcy(
    val distance: Double?,
    val end_time: String,
    val id: Int,
    val name: String,
    val start_time: String
)