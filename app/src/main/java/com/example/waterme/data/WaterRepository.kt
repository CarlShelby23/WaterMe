package com.example.waterme.data

import com.example.waterme.model.Plant
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

interface WaterRepository {
    fun scheduleReminder(duration: Long, unit: TimeUnit, plantName: String)
    fun getPlantsStream(): Flow<List<Plant>>
    suspend fun insertPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant)
}
