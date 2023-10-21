package com.example.pexelsapp.Data.Dtos

import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity

data class PexelsPhotoDto(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val src: Map<String, String>
){
    fun asEntity() : PexelsPhotoEntity {
        return PexelsPhotoEntity(id)
    }
}