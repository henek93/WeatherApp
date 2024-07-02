package com.example.weatherapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.model.CityDbModel

@Database(entities = [CityDbModel::class], version = 1, exportSchema = false)
abstract class FavouriteDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "FavouriteDatabase.db"
        private var INSTANCE: FavouriteDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): FavouriteDatabase{
            INSTANCE?.let { return it }

            synchronized(LOCK){
                INSTANCE?.let { return it }

                val db = Room.databaseBuilder(
                    context = context,
                    klass = FavouriteDatabase::class.java,
                    name = DB_NAME
                ).build()

                INSTANCE = db
                return db
            }
        }
    }
}