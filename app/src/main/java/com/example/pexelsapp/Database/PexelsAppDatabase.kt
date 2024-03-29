package com.example.pexelsapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pexelsapp.Data.Entitites.PexelsCollectionItemEntity
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Data.Entitites.RemoteKeyEntity

@Database(entities = [PexelsPhotoEntity::class,
                     RemoteKeyEntity::class,
                     PexelsCollectionItemEntity::class],
    version = 6,
    exportSchema = false)
abstract class PexelsAppDatabase : RoomDatabase() {
    abstract fun photosDao() : PexelsPhotoDao
    abstract fun keysDao() : RemoteKeyDao
    abstract fun collectionsDao() : CollectionsDao
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