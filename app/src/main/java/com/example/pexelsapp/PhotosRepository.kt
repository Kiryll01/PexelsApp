package com.example.pexelsapp

import android.util.Log
import androidx.paging.*
import androidx.room.util.query
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.Entitites.PexelsCollectionItemEntity
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.Web.PexelsApiService
import com.example.pexelsapp.pagination.PexelsRemoteMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val TAG="PHOTOS_REPOSITORY"

class PhotosRepository(
   private val app : PexelsApplication
) {

   val collectionsFlow : Flow<List<PexelsCollectionItemEntity>> = app.database.collectionsDao().getAll()
   val photosFlow :  Flow<List<PexelsPhotoEntity>> = app.database.photosDao().getAll()
   val likedPhotosFlow : Flow<List<PexelsPhotoEntity>> = app.database.photosDao().getAllLiked()

    private val pagingSourceFactory= { app.database.photosDao().pagingSource() }

    private val pagingConfig = PagingConfig(
        pageSize = PexelsApiService.PEXELS_PAGE_SIZE,
        enablePlaceholders = false,
        initialLoadSize = PexelsApiService.PEXELS_PAGE_SIZE*2,
        prefetchDistance = 2
    )
    @OptIn(ExperimentalPagingApi::class)
    fun pagingPhotosByQueryParam(queryParam: String) : Flow<PagingData<PexelsPhotoDto>>{
        Log.d(TAG,"new query pager is created ")
        val pagingQuerySourceFactory={app.database.photosDao().pagingPhotosByName(queryParam = queryParam)}

        return Pager(
            config = pagingConfig,
            pagingSourceFactory=pagingQuerySourceFactory,
            remoteMediator = PexelsRemoteMediator(
                db=app.database,
                apiCall = {page, perPage -> PexelsApiClient.apiService.searchPhotos(queryParam,perPage, page) },
                queryParam =  null
            )
        ).flow.map { it.map { it.asDto() }}

    }

    @OptIn(ExperimentalPagingApi::class)
    fun pagingCuratedPhotos() : Flow<PagingData<PexelsPhotoDto>>{
        Log.d(TAG,"new curated photos pager")
       return Pager(
           config = pagingConfig,
           pagingSourceFactory=pagingSourceFactory,
           remoteMediator = PexelsRemoteMediator(
               db=app.database,
               apiCall = {page, perPage -> PexelsApiClient.apiService.getCuratedPhotos(perPage, page) },
                queryParam =  null,
               isCuratedCall = true
           )
       ).flow.map { it.map { it.asDto() }}
    }

   @OptIn(ExperimentalPagingApi::class)
   fun pagingPhotos(queryParam: String) : Flow<PagingData<PexelsPhotoDto>>{

       Log.d(TAG,"new pager with param $queryParam")

       return Pager(
           pagingConfig,
          pagingSourceFactory = pagingSourceFactory,
          remoteMediator = PexelsRemoteMediator(
             db = app.database,
             apiCall = {page, perPage -> PexelsApiClient.apiService.searchPhotos(queryParam,page=page, perPage = perPage)},
             queryParam = queryParam
          )
      ).flow.map { it.map { it.asDto() }}
   }

    suspend fun initCollections(){
       val collections = PexelsApiClient.apiService.getFeaturedCollections().pexelsCollection.map {
           it.asEntity()
       }
        insertCollections(collections)
    }

    private suspend fun insertCollections(collections : List<PexelsCollectionItemEntity>){
        app.database.collectionsDao().insertAll(collections)
    }

   suspend fun refreshVideos(queryParam : String){
      app.database.apply {
         withContext(Dispatchers.IO){ photosDao().deleteUnliked()}
         withContext(Dispatchers.IO){
            val photos=PexelsApiClient.apiService.searchPhotos(queryParam,
               perPage = 30).photos.map { it.asEntity() }

            photosDao().insertAll(photos = photos)
         }

      }
   }
    suspend fun isPhotosTableEmpty() = app.database.photosDao().isEmpty()

   suspend fun defaultPhotos(){
      app.database.apply {
       //  withContext(Dispatchers.IO){ photosDao().deleteUnliked()}
         withContext(Dispatchers.IO){
            val photos=PexelsApiClient.apiService.getCuratedPhotos()
               .photos.map { it.asEntity() }
            photosDao().insertAll(photos)
         }
      }
   }
   suspend fun insertPhoto(photo : PexelsPhotoEntity){
      app.database.photosDao().insert(photo)
   }
   suspend fun deletePhotoById(id : Int){
      app.database.photosDao().deleteById(id)
   }
}