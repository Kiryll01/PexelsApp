package com.example.pexelsapp.ui.home

import android.net.http.NetworkException
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Database.PexelsPhotoDao
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.Web.PexelsApiClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PhotosRepository ) : ViewModel() {

    private val _collections : MutableLiveData<List<String>> = MutableLiveData(arrayListOf())
    val collections  : LiveData<List<String>> get() =_collections

//    private val _photosStateFlow= MutableStateFlow("cats")
//    val photosStateFlow = _photosStateFlow.asStateFlow()
//    fun setQueryParam(queryParam : String) {
//        _photosStateFlow.value = queryParam
//    }
    val photosFlow : StateFlow<PagingData<PexelsPhotoDto>> = repository
    .pagingPhotos("cats")
    .stateIn(viewModelScope, SharingStarted.Lazily,PagingData.empty())


    private val _launchException : MutableLiveData<NetworkExceptionInfo> = MutableLiveData(
        NetworkExceptionInfo()
    )
    val launchException : LiveData<NetworkExceptionInfo> = _launchException

    data class NetworkExceptionInfo(
        val launchException : Boolean=false,
        val searchWord : String = " "
    )
    fun setLaunchException(value : NetworkExceptionInfo){
        _launchException.value=value
    }
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
        val handler = CoroutineExceptionHandler { _, exception ->
            _launchException.value= NetworkExceptionInfo(
                launchException = true,
                searchWord = queryParamName
            )
            println("Caught $exception")
        }
        return viewModelScope.launch(handler) { repository.refreshVideos(queryParamName)
            _launchException.value=NetworkExceptionInfo(
                launchException = false,
                searchWord = queryParamName
            )
        }


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
