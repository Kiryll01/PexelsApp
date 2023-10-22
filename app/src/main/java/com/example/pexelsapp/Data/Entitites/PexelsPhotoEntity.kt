package com.example.pexelsapp.Data.Entitites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto

@Entity(tableName = "photos")
data class PexelsPhotoEntity(
    @PrimaryKey
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val src: Map<String, String>,
    var isLiked : Boolean = false
){
    fun asDto() = PexelsPhotoDto(
            id,
            width,
            height,
            url,
            photographer,
            src
        )
}