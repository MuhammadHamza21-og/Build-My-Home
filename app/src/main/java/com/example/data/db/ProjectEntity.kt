package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "house_projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val city: String,
    val statedArea: Float,
    val areaUnit: String,
    val marlaStandard: String,
    val plotShape: String,
    val frontWidth: Float,
    val rearWidth: Float,
    val leftLength: Float,
    val rightLength: Float,
    val buildingType: String,
    val bedrooms: Int,
    val baths: Int,
    val kitchens: Int,
    val selectedDesignAlt: String,
    val architecturalStyle: String,
    val constructionQuality: String,
    val createdAt: Long,
    val modifiedAt: Long
)
