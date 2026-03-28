package com.example.waterme.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val description: String,
    val schedule: String,
    val imageRes: Int? = null,
    val imageUri: String? = null
): Parcelable
