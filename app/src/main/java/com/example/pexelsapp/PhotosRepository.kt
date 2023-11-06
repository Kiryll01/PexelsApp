package com.example.pexelsapp

import android.util.Log
import androidx.paging.*
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
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
   val photosFlow :  Flow<List<PexelsPhotoEntity>> = app.database.photosDao().getAll()
   val likedPhotosFlow : Flow<List<PexelsPhotoEntity>> = app.database.photosDao().getAllLiked()


   @OptIn(ExperimentalPagingApi::class)
   fun pagingPhotos(queryParam: String) : Flow<PagingData<PexelsPhotoDto>>{
      val pagingSourceFactory= { app.database.photosDao().pagingSource() }

       Log.d(TAG,"new pager with param $queryParam")

       return Pager(
         config = PagingConfig(
            pageSize = PexelsApiService.PEXELS_PAGE_SIZE,
             enablePlaceholders = true,
             initialLoadSize = PexelsApiService.PEXELS_PAGE_SIZE*2,
             prefetchDistance = 2,
           ),
          pagingSourceFactory = pagingSourceFactory,
          remoteMediator = PexelsRemoteMediator(
             db = app.database,
             apiCall = {page, perPage -> PexelsApiClient.apiService.searchPhotos(queryParam,page=page, perPage = perPage)  }
          )
      ).flow.map { it.map { it.asDto() }}
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