package com.example.pexelsapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.Web.PexelsApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel(private val repository: PhotosRepository ) : ViewModel() {

    private val _photos :  MutableLiveData<List<PexelsPhotoDto>> = MutableLiveData()
    val photos : LiveData<List<PexelsPhotoDto>> = _photos
    var collections : List<String>

    init {
        getPhotos()
//       initCollections()
        collections= arrayListOf()
    }
    private fun getPhotos(){
        viewModelScope.launch{
           val response= PexelsApiClient.apiService.searchPhotos("lions")
            _photos.value=response.photos
        }
    }

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