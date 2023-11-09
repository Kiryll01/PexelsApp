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
    val tiny : String,
    val small : String,
    val medium : String,
    val large : String,
    val original : String,
    var isLiked : Boolean = false,
    var queryParam : String? = null
){
    fun asDto() = PexelsPhotoDto(
            id,
            width,
            height,
            url,
            photographer,
            mapOf(Pair("tiny",tiny),
                Pair("small",small),
                Pair("medium",medium),
                Pair("large",large),
                Pair("original",original)
            ),
        isLiked,
        queryParam
        )
}
