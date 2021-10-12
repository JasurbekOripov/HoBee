package uz.juo.hobee.models

data class GetById(
    val category: String="",
    val country: String="",
    val dosage_info: String="",
    val id: Int=0,
    val international_name: String="",
    val manufacturer: String="",
    val name: String="",
    val price: String? = null


)