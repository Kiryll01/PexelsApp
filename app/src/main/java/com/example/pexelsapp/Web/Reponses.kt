package com.example.pexelsapp.Web

import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto

data class PexelsSearchResponse(
    val totalResults: Int,
    val page: Int,
    val perPage: Int,
    val photos: List<PexelsPhotoDto>
)