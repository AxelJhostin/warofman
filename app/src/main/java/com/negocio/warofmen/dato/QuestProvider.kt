package com.negocio.warofmen.dato

object QuestProvider {

    // Esta función decide qué misiones darte según tu nivel
    fun getQuestsForLevel(level: Int): List<Quest> {
        val m = level // Multiplicador de repeticiones
        val xpBase = 20 * level

        // Aquí puedes poner lógica compleja (ej: cada 5 niveles sale un Boss)
        return listOf(
            // Fuerza
            Quest(1, "Flexiones Espartanas", "Haz ${5 * m} flexiones", xpBase, "STR"),
            Quest(2, "Plancha de Acero", "Aguanta ${10 * m} segundos", (xpBase * 1.2).toInt(), "STR"),

            // Resistencia
            Quest(3, "Sentadillas Infinitas", "Haz ${10 * m} sentadillas", (xpBase * 0.8).toInt(), "STA"),

            // Agilidad
            Quest(4, "Burpees Explosivos", "Haz ${3 * m} burpees", (xpBase * 1.5).toInt(), "AGI"),

            // Voluntad (Mental)
            Quest(5, "Muro de Dolor", "Wall-sit por ${15 * m} segundos", xpBase, "WIL")
        )
    }
}