package uz.juo.hobee.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.juo.hobee.room.dao.FavoriteDao
import uz.juo.hobee.room.entity.FavoritesEntity

@Database(entities = [FavoritesEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun dao(): FavoriteDao

    companion object {
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "AppDataBase"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}
