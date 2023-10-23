package com.example.pexelsapp.ui.home

import androidx.lifecycle.*
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.Web.PexelsApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel(private val repository: PhotosRepository ) : ViewModel() {


    var collections : List<String>
    fun photos() = repository.photosFlow.map {
        it.map { photo->photo.asDto() }
    }
    init {
//        getPhotos()
//       initCollections()
        collections= arrayListOf()
    }
//    private fun getPhotos(){
//        viewModelScope.launch{
//           val response= PexelsApiClient.apiService.searchPhotos("lions")
//            _photos.value=response.photos
//        }
//    }

     fun refreshPhotos(queryParamName : String) : Job {
       return viewModelScope.launch {  repository.refreshVideos(queryParamName)}
    }

    private fun initCollections() {
        runBlocking {
            val response = viewModelScope.async {
                PexelsApiClient.apiService.getFeaturedCollections()
            }.await()
            collections = response
                .pexelsCollection
                .map { it.title }

        }
    }
}
class HomeViewModelFactory(private val repository: PhotosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}