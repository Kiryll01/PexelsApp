package com.example.pexelsapp.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pexelsapp.Data.Entitites.PexelsCollectionItemEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection:PexelsCollectionItemEntity )
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(collection : List<PexelsCollectionItemEntity>)
    @Query("delete from collections")
    suspend fun deleteAll()
    @Query("SELECT * FROM collections")
    fun getAll(): Flow<List<PexelsCollectionItemEntity>>
}