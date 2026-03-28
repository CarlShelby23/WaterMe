package com.example.waterme.data

import android.content.Context
import com.example.waterme.data.local.PlantDatabase

interface AppContainer {
    val waterRepository : WaterRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val waterRepository: WaterRepository by lazy {
        WorkManagerWaterRepository(
            PlantDatabase.getDatabase(context).plantDao(),
            context
        )
    }
}
