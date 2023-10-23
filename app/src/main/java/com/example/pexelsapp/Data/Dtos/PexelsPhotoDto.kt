package com.example.pexelsapp.Data.Dtos

import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Data.PexelsSize
import kotlinx.serialization.Serializable

data class PexelsPhotoDto (
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val src: Map<String, String>,
    var isLiked : Boolean = false
) : java.io.Serializable{
    fun asEntity() = PexelsPhotoEntity(
        id,
        width,
        height, url, photographer,
        src[PexelsSize.TINY.sizeName] ?:"",
        src.get(PexelsSize.SMALL.sizeName) ?:"",
        src.get(PexelsSize.MEDIUM.sizeName)?:"",
        src.get(PexelsSize.LARGE.sizeName)?:"",
        src.get(PexelsSize.ORIGINAL.sizeName)?:"",
        isLiked
    )
}

