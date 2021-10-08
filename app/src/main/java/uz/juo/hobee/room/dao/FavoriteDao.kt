package uz.juo.hobee.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.juo.hobee.room.entity.FavoritesEntity

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(mediacament: FavoritesEntity)

    @Query("select * from Favorite_table where id=:id")
    fun getById(id: Int): FavoritesEntity

    @Query("select * from Favorite_table")
    fun getAll(): List<FavoritesEntity>

    @Query("Delete from Favorite_table where id=:id")
    fun delete(id: Int)

    @Query("Delete from Favorite_table ")
    fun deleteAll()
}