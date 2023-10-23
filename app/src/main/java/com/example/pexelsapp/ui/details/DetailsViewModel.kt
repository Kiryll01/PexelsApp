package com.example.pexelsapp.ui.details

import androidx.lifecycle.*
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.ui.home.HomeViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: PhotosRepository) : ViewModel(){

    private val _saveButtonState : MutableLiveData<Boolean> = MutableLiveData(false)

    val saveButtonState : LiveData<Boolean> = _saveButtonState

    fun setState(state : Boolean){
        _saveButtonState.value=state
    }
    fun saveState(photo : PexelsPhotoEntity){
        _saveButtonState.value=!(_saveButtonState.value!!)
        if(_saveButtonState.value==false) viewModelScope.launch {
            repository.deletePhotoById(photo.id)
        }
        else viewModelScope.launch {
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