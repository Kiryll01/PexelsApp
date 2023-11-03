package com.example.pexelsapp.Data.Entitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_keys")
data class RemoteKeyEntity (
    @PrimaryKey(autoGenerate = false)
    val imageId : Int,
    val prevPage : Int,
    val nextPage : Int
)