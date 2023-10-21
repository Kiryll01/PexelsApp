package com.example.pexelsapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.Web.PexelsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _photos :  MutableLiveData<List<PexelsPhotoDto>> = MutableLiveData()
    val photos : LiveData<List<PexelsPhotoDto>> = _photos
    init {
        getPhotos()
    }
    private fun getPhotos(){
        viewModelScope.launch() {
           val response= PexelsApiClient.apiService.searchPhotos("lions")
            _photos.value=response.photos

        }
    }
}