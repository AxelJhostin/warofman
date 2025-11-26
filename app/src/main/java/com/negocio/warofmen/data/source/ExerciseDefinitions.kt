package com.negocio.warofmen.data.source

import com.negocio.warofmen.data.model.ExerciseFamily
import com.negocio.warofmen.data.model.ExerciseVariant

object ExerciseDefinitions {

    // 1. FAMILIA DE FUERZA (EMPUJE / PECHO / TRICEPS)
    val pushFamily = ExerciseFamily(
        id = "push",
        stat = "STR",
        variants = listOf(
            // Nivel 1-19: Novato
            ExerciseVariant("Flexiones en Pared/Rodillas", 1.0f),
            // Nivel 20-39: Guerrero
            ExerciseVariant("Flexiones Clásicas", 1.2f),
            // Nivel 40-59: Élite
            ExerciseVariant("Flexiones Diamante", 1.5f),
            // Nivel 60-79: Maestro
            ExerciseVariant("Flexiones Arquero", 1.8f),
            // Nivel 80+: Leyenda
            ExerciseVariant("Flexiones a una Mano", 2.5f)
        )
    )

    // 2. FAMILIA DE RESISTENCIA (PIERNAS)
    val legFamily = ExerciseFamily(
        id = "legs",
        stat = "STA",
        variants = listOf(
            // Nivel 1-19
            ExerciseVariant("Sentadilla Asistida (Silla)", 1.0f),
            // Nivel 20-39
            ExerciseVariant("Sentadilla Clásica", 1.2f),
            // Nivel 40-59
            ExerciseVariant("Zancadas (Lunges)", 1.4f),
            // Nivel 60-79
            ExerciseVariant("Sentadilla Búlgara", 1.7f),
            // Nivel 80+
            ExerciseVariant("Pistol Squat (Una pierna)", 2.5f)
        )
    )

    // 3. FAMILIA DE VOLUNTAD/CORE (ABDOMINALES)
    val coreFamily = ExerciseFamily(
        id = "core",
        stat = "WIL",
        variants = listOf(
            // Nivel 1-19
            ExerciseVariant("Crunch Corto", 1.0f),
            // Nivel 20-39
            ExerciseVariant("Elevación de Piernas", 1.2f),
            // Nivel 40-59
            ExerciseVariant("Plancha Abdominal", 1.5f, isTimer = true), // Es por tiempo
            // Nivel 60-79
            ExerciseVariant("V-Ups (Navajas)", 1.9f),
            // Nivel 80+
            ExerciseVariant("Dragon Flag", 3.0f)
        )
    )
    // 4. FAMILIA DE AGILIDAD (ESPALDA / CARDIO) -> NUEVA
    val backFamily = ExerciseFamily(
        id = "back",
        stat = "AGI",
        variants = listOf(
            // Nivel 1-19: Muy fácil, para activar la espalda sin equipo
            ExerciseVariant("Remo en Marco de Puerta", 1.0f),

            // Nivel 20-39: Lumbares y postura (Por tiempo)
            ExerciseVariant("Superman (Lumbares)", 1.2f, isTimer = true),

            // Nivel 40-59: Arrastre en suelo (Requiere suelo liso o toalla)
            ExerciseVariant("Deslizamientos (Floor Pulls)", 1.5f),

            // Nivel 60-79: Cardio intenso y cuerpo completo
            ExerciseVariant("Burpees Completos", 1.8f),

            // Nivel 80+: La prueba reina (Requiere barra o parque)
            ExerciseVariant("Dominadas (Pull-ups)", 3.0f)
        )
    )
}