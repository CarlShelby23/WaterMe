package com.example.waterme.data

import com.example.waterme.R
import com.example.waterme.model.Plant

object DataSource {
    val defaultPlants = listOf(
        Plant(
            name = "Lithops",
            schedule = "mensualmente",
            type = "Suculenta",
            description = "Suculenta que imita a una piedra",
            imageRes = R.drawable.lithops
        ),
        Plant(
            name = "Zanahoria",
            schedule = "diariamente",
            type = "Raíz",
            description = "Hortaliza de raíz resistente",
            imageRes = R.drawable.carrot
        ),
        Plant(
            name = "Peonía",
            schedule = "semanalmente",
            type = "Flor",
            description = "Flor de floración primaveral",
            imageRes = R.drawable.peony
        ),
        Plant(
            name = "Poto",
            schedule = "semanalmente",
            type = "Planta de interior",
            description = "Enredadera de interior",
            imageRes = R.drawable.pothos
        ),
        Plant(
            name = "Ficus pandurata",
            schedule = "semanalmente",
            type = "Perennifolia de hoja ancha",
            description = "Higuera ornamental",
            imageRes = R.drawable.fiddle_leaf_fig
        ),
        Plant(
            name = "Fresa",
            schedule = "diariamente",
            type = "Fruta",
            description = "Deliciosa fruta múltiple",
            imageRes = R.drawable.strawberry
        )
    )
}
