package com.example.pexelsapp.Data.Dtos

import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Data.Enums.PexelsSize

data class PexelsPhotoDto (
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val src: Map<String, String>,
    var isLiked : Boolean = false,
    var queryParam : String? = null,
    var isCurated : Boolean = false
) : java.io.Serializable{
    constructor() : this(0, 0, 0, "", "", mapOf(), false)
    fun asEntity() = PexelsPhotoEntity(
        id,
        width,
        height, url, photographer,
        src[PexelsSize.TINY.sizeName] ?:"",
        src.get(PexelsSize.SMALL.sizeName) ?:"",
        src.get(PexelsSize.MEDIUM.sizeName)?:"",
        src.get(PexelsSize.LARGE.sizeName)?:"",
        src.get(PexelsSize.ORIGINAL.sizeName)?:"",
        isLiked,
        queryParam,
        isCurated
    )
}

