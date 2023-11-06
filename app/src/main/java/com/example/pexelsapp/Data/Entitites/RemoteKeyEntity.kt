package com.example.pexelsapp.Data.Entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_keys")
data class RemoteKeyEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "image_id")
    val imageId : Int,
    val prevPage : Int?,
    val nextPage : Int?
)