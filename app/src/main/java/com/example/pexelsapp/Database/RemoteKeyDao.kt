package com.example.pexelsapp.Database

import androidx.room.*
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: RemoteKeyDao)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys : List<RemoteKeyDao>)
    @Query("delete from photo_keys")
    suspend fun deleteAll()
    @Update
    suspend fun update(key : RemoteKeyDao)
    @Delete
    suspend fun delete(key: RemoteKeyDao)
}