package com.example.pexelsapp.ui.home

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.PhotosRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


private const val TAG = "HOME_VIEW_MODEL"
class HomeViewModel(private val repository: PhotosRepository ) : ViewModel(), ImageLoadingListener {
    companion object {
        var isFirstLaunch = true
        var photoNavArg = PexelsPhotoDto()
    }

    var loadedImagesCount : Int = 0

    private val _isDataReady = MutableLiveData(DataInitState())
    val isDataReady : LiveData<DataInitState> get() = _isDataReady
    private fun incrementImagesCount(){
        loadedImagesCount++
        Log.d(TAG,"loadedImages : $loadedImagesCount")
        if(loadedImagesCount>5)return
        if(loadedImagesCount==5){
         setRecyclerViewReady()
        }
    }
    fun setScrollViewReady(){
        val isRecyclerViewReady= _isDataReady.value!!.isRecyclerViewReady

        _isDataReady.value=DataInitState(isRecyclerViewReady = isRecyclerViewReady,
            isScrollViewReady = true)
    }
    private fun setRecyclerViewReady(){
       val isScrollViewReady= _isDataReady.value!!.isScrollViewReady
        _isDataReady.value= DataInitState(isRecyclerViewReady = true,
            isScrollViewReady =isScrollViewReady )
    }

    val collections =repository.collectionsFlow

    private var _searchQuery : MutableSharedFlow<String> = MutableSharedFlow(replay = 50,
        extraBufferCapacity = 64)
    val searchQuery = _searchQuery.asSharedFlow()

    val photosFlow : Flow<PagingData<PexelsPhotoDto>> = _searchQuery
        .flatMapLatest {
            Log.d(TAG,"new photosFlow has been created ")
            repository.pagingPhotos(it) }
        .cachedIn(viewModelScope)

    val photosFlowByQueryParam : Flow<PagingData<PexelsPhotoDto>> = _searchQuery
        .flatMapLatest {
            Log.d(TAG,"new photos flow by param was created ")
            repository.pagingPhotosByQueryParam(it)
        }
        .cachedIn(viewModelScope)
    val curatedPhotosFlow =  repository.pagingCuratedPhotos()

    private val _launchException : MutableLiveData<NetworkExceptionInfo> = MutableLiveData(NetworkExceptionInfo())
    val launchException : LiveData<NetworkExceptionInfo> = _launchException

    fun setQuery(query : String){
        _searchQuery.tryEmit(query)
        Log.d(TAG, "$query emit")
    }

    fun setLaunchException(value : NetworkExceptionInfo){
        _launchException.value=value
    }
    init {
        Log.d(TAG,"viwModel is Created")
       if(isFirstLaunch) initCollections()
        setRecyclerViewReady()

    }

    suspend fun isPhotosTableEmpty() = repository.isPhotosTableEmpty()
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
    private fun initCollections() {
        viewModelScope.launch {
            repository.initCollections()
        }
    }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"view model is destroyed")
    }
    override fun onImageLoaded() {
        Log.d(TAG, "loading listener is called")
        incrementImagesCount()
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
data class NetworkExceptionInfo(
    val launchException : Boolean=false,
    val searchWord : String = " "
)
data class DataInitState(
    var isRecyclerViewReady : Boolean = false,
    var isScrollViewReady : Boolean = false,
){
    fun isReady() : Boolean{
        return isRecyclerViewReady && isScrollViewReady
    }
}



