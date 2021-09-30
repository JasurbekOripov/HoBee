package uz.juo.hobee.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Favorite_table")
data class FavoritesEntity(
    @PrimaryKey()
    val id: Int = 0,
    var name: String? = "",
    var manufacturer: String? = "",
    var country: String? = "",
    var category: String? = "",
    var price: String? = null
) : Serializable