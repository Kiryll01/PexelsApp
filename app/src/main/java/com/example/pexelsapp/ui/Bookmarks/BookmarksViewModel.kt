package com.example.pexelsapp.ui.Bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.ui.details.DetailsViewModel
import kotlinx.coroutines.flow.map

class BookmarksViewModel(private val repository : PhotosRepository) : ViewModel() {
    fun likedPhotos() = repository.likedPhotosFlow.map { it.map { it.asDto() } }

}
class BookmarksViewModelFactory(private val repository: PhotosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookmarksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}