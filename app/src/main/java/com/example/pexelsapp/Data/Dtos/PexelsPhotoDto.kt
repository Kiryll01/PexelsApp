package com.example.pexelsapp.Data.Dtos

import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity

data class PexelsPhotoDto(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val src: Map<String, String>
){
    fun asEntity() : PexelsPhotoEntity {
        return PexelsPhotoEntity(id)
    }
}

enum class PexelsSize(val sizeName:String) {
    TINY("tiny"),
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
    ORIGINAL("original");
    companion object {
        fun fromName(name: String): PexelsSize? = PexelsSize.values().find { it.sizeName == name }
    }

}