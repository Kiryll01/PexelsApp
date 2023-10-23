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
    fun asEntity() = PexelsPhotoEntity(
        id,
        width,
        height, url, photographer,
        src[PexelsSize.TINY.sizeName] ?:"",
        src.get(PexelsSize.SMALL.sizeName) ?:"",
        src.get(PexelsSize.MEDIUM.sizeName)?:"",
        src.get(PexelsSize.LARGE.sizeName)?:"",
        src.get(PexelsSize.ORIGINAL.sizeName)?:""
    )
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