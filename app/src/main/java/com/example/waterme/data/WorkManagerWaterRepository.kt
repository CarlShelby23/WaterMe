package com.example.waterme.data

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.data.local.PlantDao
import com.example.waterme.model.Plant
import com.example.waterme.worker.WaterReminderWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class WorkManagerWaterRepository(
    private val plantDao: PlantDao,
    context: Context
) : WaterRepository {
    private val workManager = WorkManager.getInstance(context)

    override fun getPlantsStream(): Flow<List<Plant>> = plantDao.getAllPlants()

    override suspend fun insertPlant(plant: Plant) = plantDao.insert(plant)

    override suspend fun updatePlant(plant: Plant) = plantDao.update(plant)

    override suspend fun deletePlant(plant: Plant) = plantDao.delete(plant)

    override fun scheduleReminder(duration: Long, unit: TimeUnit, plantName: String) {
        val data = Data.Builder()
        data.putString(WaterReminderWorker.nameKey, plantName)

        val workRequestBuilder = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInitialDelay(duration, unit)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniqueWork(
            plantName + duration,
            ExistingWorkPolicy.REPLACE,
            workRequestBuilder
        )
    }
}
