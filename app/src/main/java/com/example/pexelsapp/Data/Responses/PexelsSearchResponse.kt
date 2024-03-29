package com.example.pexelsapp.Data.Responses

import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Web.NullableInt
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PexelsSearchResponse(
    val page: Int,
    val photos: List<PexelsPhotoDto>,
    @NullableInt
    @Json(name = "per_page")
    val perPage:Int,
    @NullableInt
    @Json(name = "total_results")
    val totalResults: Int
)