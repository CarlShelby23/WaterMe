package com.example.waterme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.waterme.WaterMeApplication
import com.example.waterme.data.Reminder
import com.example.waterme.data.WaterRepository
import com.example.waterme.model.Plant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WaterViewModel(private val waterRepository: WaterRepository) : ViewModel() {

    val plants: StateFlow<List<Plant>> = waterRepository.getPlantsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun scheduleReminder(reminder: Reminder) {
        waterRepository.scheduleReminder(reminder.duration, reminder.unit, reminder.plantName)
    }

    fun addPlant(name: String, type: String, description: String, schedule: String, imageUri: String?) {
        viewModelScope.launch {
            waterRepository.insertPlant(
                Plant(
                    name = name,
                    type = type,
                    description = description,
                    schedule = schedule,
                    imageUri = imageUri
                )
            )
        }
    }

    fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            waterRepository.updatePlant(plant)
        }
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            waterRepository.deletePlant(plant)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val waterRepository =
                    (this[APPLICATION_KEY] as WaterMeApplication).container.waterRepository
                WaterViewModel(
                    waterRepository = waterRepository
                )
            }
        }
    }
}
