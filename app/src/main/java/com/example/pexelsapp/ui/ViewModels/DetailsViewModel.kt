package com.example.pexelsapp.ui.ViewModels

import androidx.lifecycle.*
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.PhotosRepository
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: PhotosRepository) : ViewModel(){

    private val _saveButtonState : MutableLiveData<Boolean> = MutableLiveData(true)

    val saveButtonState : LiveData<Boolean> = _saveButtonState

    fun saveState(photo : PexelsPhotoEntity){
        _saveButtonState.value=!(_saveButtonState.value!!)

        if(_saveButtonState.value==false) viewModelScope.launch {
           val photoFromDb = repository.getPhotoById(photo.id)
            photoFromDb.isLiked=false
            repository.insertPhoto(photoFromDb)
        }
         else viewModelScope.launch {
            photo.isLiked=true
            repository.insertPhoto(photo)
        }
    }
}
class DetailsViewModelFactory(private val repository: PhotosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}