package com.example.pexelsapp.Database

import androidx.room.*
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Data.Entitites.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: RemoteKeyEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys : List<RemoteKeyEntity>)
    @Query("delete from photo_keys")
    suspend fun deleteAll()
    @Update
    suspend fun update(key : RemoteKeyEntity)
    @Delete
    suspend fun delete(key: RemoteKeyEntity)
    @Query("select * from photo_keys where image_id = :id")
    suspend fun getById(id : Int) : RemoteKeyEntity?
}