package com.example.pexelsapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity

@Database(entities = [PexelsPhotoEntity::class],
    version = 1,
    exportSchema = false)
abstract class PexelsAppDatabase : RoomDatabase() {
    abstract fun photosDao() : PexelsPhotoDao

    companion object{
        @Volatile
        private var INSTANCE : PexelsAppDatabase?=null

        fun instance(context: Context) : PexelsAppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                   PexelsAppDatabase::class.java,
                    "pexels_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE =instance

                instance
            }
        }
}
}