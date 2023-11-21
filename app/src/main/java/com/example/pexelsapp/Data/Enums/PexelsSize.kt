package com.example.pexelsapp.Data.Enums

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