package com.example.pexelsapp.Database

import androidx.paging.PagingSource
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
    @Query("delete from photos")
    suspend fun deleteAll()
    @Update
    suspend fun update(photo: PexelsPhotoEntity)
    @Delete
    suspend fun delete(photo: PexelsPhotoEntity)
    suspend fun refresh(photos : List<PexelsPhotoEntity>){
        deleteUnliked()
        insertAll(photos)
    }
    @Query("delete from photos where id = :id")
    suspend fun deleteById(id:Int)
    @Query("SELECT * FROM photos")
    fun getAll(): Flow<List<PexelsPhotoEntity>>
    @Query("select * from photos where isLiked=true")
    fun getAllLiked() : Flow<List<PexelsPhotoEntity>>
    @Query("delete from photos where isLiked=false")
    suspend fun deleteUnliked()
    @Query("SELECT * FROM photos")
    fun pagingSource() : PagingSource<Int,PexelsPhotoEntity>
    @Query("select * from photos where query_param = :queryParam")
    fun pagingPhotosByName(queryParam : String) : PagingSource<Int,PexelsPhotoEntity>
    @Query("select count(*) > 0 from photos")
    suspend fun isEmpty() : Boolean
}