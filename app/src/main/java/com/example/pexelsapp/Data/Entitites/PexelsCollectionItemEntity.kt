package com.example.pexelsapp.Data.Entitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class PexelsCollectionItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name : String
)