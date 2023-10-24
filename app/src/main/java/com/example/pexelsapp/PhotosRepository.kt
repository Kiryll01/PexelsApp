package com.example.pexelsapp

import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.Web.PexelsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class PhotosRepository(
   private val app : PexelsApplication
) {
   val photosFlow :  Flow<List<PexelsPhotoEntity>> = app.database.photosDao().getAll()
   val likedPhotosFlow : Flow<List<PexelsPhotoEntity>> = app.database.photosDao().getAllLiked()
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