package com.example.waterme.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.waterme.FIVE_SECONDS
import com.example.waterme.ONE_DAY
import com.example.waterme.R
import com.example.waterme.SEVEN_DAYS
import com.example.waterme.THIRTY_DAYS
import com.example.waterme.data.Reminder
import com.example.waterme.model.Plant
import com.example.waterme.ui.theme.WaterMeTheme
import java.util.concurrent.TimeUnit

@Composable
fun WaterMeApp(waterViewModel: WaterViewModel = viewModel(factory = WaterViewModel.Factory)) {
    val plants by waterViewModel.plants.collectAsState()
    var showAddPlantDialog by rememberSaveable { mutableStateOf(false) }

    WaterMeTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddPlantDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar planta")
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                PlantListContent(
                    plants = plants,
                    onScheduleReminder = { waterViewModel.scheduleReminder(it) },
                    onDeletePlant = { waterViewModel.deletePlant(it) },
                    onUpdatePlant = { waterViewModel.updatePlant(it) }
                )
            }
        }

        if (showAddPlantDialog) {
            EditPlantDialog(
                onDismiss = { showAddPlantDialog = false },
                onConfirm = { name, type, desc, schedule, uri ->
                    waterViewModel.addPlant(name, type, desc, schedule, uri)
                    showAddPlantDialog = false
                }
            )
        }
    }
}

@Composable
fun EditPlantDialog(
    plant: Plant? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String?) -> Unit
) {
    var name by remember { mutableStateOf(plant?.name ?: "") }
    var type by remember { mutableStateOf(plant?.type ?: "") }
    var description by remember { mutableStateOf(plant?.description ?: "") }
    var schedule by remember { mutableStateOf(plant?.schedule ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(plant?.imageUri?.let { Uri.parse(it) }) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (plant == null) "Nueva Planta" else "Editar Planta") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Frecuencia") })
                
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(if (imageUri == null && plant?.imageRes == null) "Seleccionar Foto" else "Cambiar Foto")
                }
                
                if (imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                } else if (plant?.imageRes != null) {
                    Image(
                        painter = painterResource(plant.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, type, description, schedule, imageUri?.toString()) },
                enabled = name.isNotBlank() && type.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun PlantListContent(
    plants: List<Plant>,
    onScheduleReminder: (Reminder) -> Unit,
    onDeletePlant: (Plant) -> Unit,
    onUpdatePlant: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlantForReminder by remember { mutableStateOf<Plant?>(null) }
    var selectedPlantForEdit by remember { mutableStateOf<Plant?>(null) }
    var showReminderDialog by remember { mutableStateOf(false) }

    if (plants.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay plantas aún. ¡Agrega una!")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
            modifier = modifier
        ) {
            items(items = plants, key = { it.id }) { plant ->
                PlantListItem(
                    plant = plant,
                    onItemSelect = {
                        selectedPlantForReminder = plant
                        showReminderDialog = true
                    },
                    onEditClick = { selectedPlantForEdit = plant },
                    onDeleteClick = { onDeletePlant(plant) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showReminderDialog && selectedPlantForReminder != null) {
        ReminderDialogContent(
            onDialogDismiss = { showReminderDialog = false },
            plantName = selectedPlantForReminder!!.name,
            onScheduleReminder = onScheduleReminder
        )
    }

    if (selectedPlantForEdit != null) {
        EditPlantDialog(
            plant = selectedPlantForEdit,
            onDismiss = { selectedPlantForEdit = null },
            onConfirm = { name, type, desc, schedule, uri ->
                onUpdatePlant(selectedPlantForEdit!!.copy(
                    name = name,
                    type = type,
                    description = desc,
                    schedule = schedule,
                    imageUri = uri,
                    imageRes = if (uri != null) null else selectedPlantForEdit!!.imageRes
                ))
                selectedPlantForEdit = null
            }
        )
    }
}

@Composable
fun PlantListItem(
    plant: Plant,
    onItemSelect: (Plant) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.clickable { onItemSelect(plant) }) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (plant.imageRes != null) {
                Image(
                    painter = painterResource(plant.imageRes),
                    contentDescription = plant.name,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } else if (plant.imageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(plant.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = plant.name,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = plant.name,
                    style = typography.headlineSmall,
                    textAlign = TextAlign.Start
                )
                Text(text = plant.type, style = typography.titleMedium)
                Text(text = plant.description, style = typography.titleMedium)
                Text(
                    text = "${stringResource(R.string.water)} ${plant.schedule}",
                    style = typography.titleMedium
                )
            }
            
            Column {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

@Composable
fun ReminderDialogContent(
    onDialogDismiss: () -> Unit,
    plantName: String,
    onScheduleReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminders = listOf(
        Reminder(R.string.five_seconds, FIVE_SECONDS, TimeUnit.SECONDS, plantName),
        Reminder(R.string.one_day, ONE_DAY, TimeUnit.DAYS, plantName),
        Reminder(R.string.one_week, SEVEN_DAYS, TimeUnit.DAYS, plantName),
        Reminder(R.string.one_month, THIRTY_DAYS, TimeUnit.DAYS, plantName)
    )

    AlertDialog(
        onDismissRequest = onDialogDismiss,
        confirmButton = {},
        title = { Text(stringResource(R.string.remind_me, plantName)) },
        text = {
            Column {
                reminders.forEach {
                    Text(
                        text = stringResource(it.durationRes),
                        modifier = Modifier
                            .clickable {
                                onScheduleReminder(it)
                                onDialogDismiss()
                            }
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        },
        modifier = modifier
    )
}
