package com.example.pexelsapp

import android.app.Application
import coil.ImageLoader
import com.example.pexelsapp.Database.PexelsAppDatabase

class PexelsApplication : Application(){
    val database : PexelsAppDatabase by lazy { PexelsAppDatabase.instance(this) }
    val repository : PhotosRepository by  lazy{PhotosRepository(this)}

}