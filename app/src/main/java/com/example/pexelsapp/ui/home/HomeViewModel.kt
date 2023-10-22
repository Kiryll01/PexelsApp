package com.example.pexelsapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.Web.PexelsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.streams.asSequence
import kotlin.streams.toList

class HomeViewModel : ViewModel() {

    private val _photos :  MutableLiveData<List<PexelsPhotoDto>> = MutableLiveData()
    val photos : LiveData<List<PexelsPhotoDto>> = _photos
    lateinit var collections : List<String>
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