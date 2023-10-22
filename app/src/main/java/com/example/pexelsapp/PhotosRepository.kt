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
   suspend fun refreshVideos(queryParam : String){
      app.database.apply {
         withContext(Dispatchers.IO){ photosDao().deleteUnliked()}
         withContext(Dispatchers.IO){
            val photos=PexelsApiClient.apiService.searchPhotos(queryParam,
               perPage = 100).photos.map { it.asEntity() }
            photosDao().insertAll(photos = photos)
         }

      }
   }
}