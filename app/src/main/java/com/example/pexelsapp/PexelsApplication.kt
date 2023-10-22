package com.example.pexelsapp

import android.app.Application
import com.example.pexelsapp.Database.PexelsAppDatabase

class PexelsApplication : Application(){
    val database : PexelsAppDatabase by lazy { PexelsAppDatabase.instance(this) }

}