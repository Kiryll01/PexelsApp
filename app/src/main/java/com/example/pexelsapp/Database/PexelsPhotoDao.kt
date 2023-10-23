package com.example.pexelsapp.Database

import androidx.room.*
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.google.android.material.circularreveal.CircularRevealHelper.Strategy
import kotlinx.coroutines.flow.Flow
import java.io.Serializable

@Dao
interface PexelsPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PexelsPhotoEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos : List<PexelsPhotoEntity>)
    @Update
    suspend fun update(photo: PexelsPhotoEntity)

    @Delete
    suspend fun deleteUnliked(photo: PexelsPhotoEntity)

    @Query("SELECT * FROM photos")
    fun getAll(): Flow<List<PexelsPhotoEntity>>

    @Query("delete from photos where isLiked=false")
    suspend fun deleteUnliked()
}