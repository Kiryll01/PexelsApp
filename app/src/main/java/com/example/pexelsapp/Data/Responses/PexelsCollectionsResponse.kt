package com.example.pexelsapp.Data.Responses

import com.example.pexelsapp.Data.Entitites.PexelsCollectionItemEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PexelsCollectionsResponse(
    @Json(name="collections")
val pexelsCollection : List<PexelsCollectionItem>
)
data class PexelsCollectionItem(
    val title : String
){
    fun asEntity() : PexelsCollectionItemEntity{
        return PexelsCollectionItemEntity(name=title)
    }
}