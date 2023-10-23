package com.example.pexelsapp.ui.home

import androidx.lifecycle.*
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.Web.PexelsApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PhotosRepository ) : ViewModel() {

    private val _collections : MutableLiveData<List<String>> = MutableLiveData(arrayListOf())
    val collections  : LiveData<List<String>> get() =_collections
    init {
        initCollections()
        initVideos()
    }

    fun photos() = repository.photosFlow
        .map {
        it.filter { !it.isLiked }
            .map { photo -> photo.asDto() }
    }



    fun refreshPhotos(queryParamName: String): Job {
        return viewModelScope.launch { repository.refreshVideos(queryParamName) }
    }

    private fun initVideos(){
        viewModelScope.launch {
            repository.defaultPhotos()
        }
    }
   private fun initCollections() {
        viewModelScope.launch() {
            _collections.value = PexelsApiClient.apiService.getFeaturedCollections()
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
