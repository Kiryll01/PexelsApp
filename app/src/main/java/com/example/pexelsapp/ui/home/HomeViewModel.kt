package com.example.pexelsapp.ui.home

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.Web.PexelsApiClient
import com.google.android.material.tabs.TabLayout.TabGravity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


private const val TAG = "HOME_VIEW_MODEL"
class HomeViewModel(private val repository: PhotosRepository ) : ViewModel() {

    private val _collections : MutableLiveData<List<String>> = MutableLiveData(arrayListOf())
    val collections  : LiveData<List<String>> get() =_collections
    private val _searchQuery = MutableStateFlow<String>("cats")

    val searchQuery = _searchQuery.asStateFlow()
    fun setQuery(query : String){
        _searchQuery.value=query
        Log.d(TAG, "$query emit")
    }

    val photosFlow : Flow<PagingData<PexelsPhotoDto>> = _searchQuery
        .flatMapLatest {
            Log.d(TAG,"new photosFlow has been created ")
            repository.pagingPhotos(it) }
        .cachedIn(viewModelScope)


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
