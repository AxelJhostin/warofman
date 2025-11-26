# ⚔️ War of Men - RPG Fitness Tracker

**War of Men** es una aplicación móvil nativa para Android que gamifica el ejercicio físico, transformando tus rutinas de entrenamiento en una aventura de Rol (RPG).

> "Tu realidad física define tu destino virtual."

## 📱 Descripción del Proyecto
La aplicación permite a los usuarios crear un personaje cuyas estadísticas iniciales (Fuerza, Resistencia, Agilidad) se basan en sus datos biométricos reales (Peso, Altura, Edad). A medida que el usuario completa entrenamientos ("Misiones"), su personaje gana experiencia (XP), sube de nivel y desbloquea recompensas.

## 🚀 Características Principales (MVP)
* **Creación de Personaje:** Algoritmo que calcula Stats base según IMC y edad.
* **Sistema de Misiones:** Entrenamientos guiados con series, repeticiones y cronómetro de descanso.
* **Progresión RPG:** Sistema de XP, Niveles y escalado de dificultad automático.
* **Dashboard Visual:** Interfaz "Dark Neon" con barras de progreso y feedback visual.
* **Analytics:**
    * Gráfica lineal de historial de peso corporal.
    * Gráfica de rendimiento (volumen de carga) por ejercicio.
* **Persistencia de Datos:** Guardado local mediante DataStore.

## 🛠️ Stack Tecnológico
* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Arquitectura:** MVVM + Clean Architecture (Simplificada)
* **Almacenamiento:** Jetpack DataStore (Proto/Preferences)
* **Navegación:** Jetpack Navigation Compose
* **Control de Versiones:** Git & GitHub

## 📂 Estructura del Proyecto
El proyecto sigue una estructura modular para facilitar la escalabilidad:

* `core/`: Utilidades transversales, constantes y configuración de navegación.
* `data/`: La capa de verdad. Contiene los modelos de datos, repositorios y fuentes de datos (DataStore, Providers).
* `ui/`: Capa de presentación.
    * `components/`: Átomos visuales reutilizables (Gráficas, Tarjetas, Diálogos).
    * `screens/`: Pantallas completas (Home, Workout, Stats).
    * `viewmodel/`: Gestión del estado de la UI.
    * `theme/`: Sistema de diseño (Colores, Tipografía).

💾 Estrategia de Datos (Persistence)
Actualmente utilizamos una estrategia híbrida con Jetpack DataStore Preferences y Serialización JSON.

Librería: androidx.datastore + Gson.

Lógica: Los objetos complejos (como el historial de medidas corporales) se serializan a JSON Strings para almacenarse en DataStore.

Modelos de Datos Clave
PlayerCharacter: El objeto raíz que contiene todo el estado del usuario.

BodyLog (Nuevo): Registro detallado que incluye:

Obligatorio: Peso.

Opcional (Cinta métrica): Cuello, Cintura, Cadera, Bíceps, Pecho.

Calculado: % Grasa Corporal, Masa Magra.

Quest: Misiones de entrenamiento con soporte para Series, Repeticiones y Tiempos de descanso.

🧬 Sistema Biométrico y Matemático
La aplicación implementa fórmulas reales para calcular atributos:

IMC (Índice de Masa Corporal): Cálculo en tiempo real.

% Grasa Corporal (Navy Method): Implementación del método de la Marina de EE.UU. usando altura, cuello, cintura y cadera.

Atributos RPG:

Fuerza (STR): Influenciada por IMC alto o masa muscular.

Agilidad (AGI): Bonificada por % de grasa bajo.

Resistencia (STA): Bonificada por edad joven o % grasa bajo.

🚀 Funcionalidades Implementadas
1. Wizard de Creación de Personaje (Nuevo)
   Flujo paso a paso animado.

Cálculo en vivo de IMC y Grasa Corporal.

Selección visual de Clase (Guerrero/Amazona).

Modo "Experto" para usuarios con cinta métrica.

2. Sistema de Entrenamiento (Workout)
   Interfaz inmersiva (Pantalla Naranja/Negra).

Gestión de Series y Repeticiones.

Cronómetro de descanso integrado.

Validación de ejercicios por Tiempo vs Repeticiones.

3. Visualización de Datos
   Gráfica de Peso: Dibujada nativamente con Canvas (sin librerías externas).

Gráfica de Rendimiento: Historial de volumen de carga por ejercicio.

Dashboard RPG: Barras de progreso de neón para XP y Stats.

🛠️ Stack Tecnológico
Lenguaje: Kotlin

UI: Jetpack Compose (Material 3)

Navegación: Navigation Compose

Inyección de Dependencias: ViewModel Factory (Manual por ahora)

Persistencia: DataStore Preferences

Serialización: Google Gson

Control de Versiones: Git

🗺️ Roadmap (Próximos Pasos)
[x] Refactorización a Clean Architecture.

[x] Implementación de Gson para listas complejas.

[x] Wizard de Creación Biometríca.

[ ] Visualización de Grasa Corporal en Estadísticas.

[ ] Sistema de Inventario y Equipamiento (Loot).

[ ] Migración futura a Room Database (cuando la base de usuarios crezca).

👨‍💻 Notas para Desarrolladores (Contexto AI)
Si se modifica GameModels.kt, recordar actualizar GameStorage.kt para manejar la serialización Gson correctamente.

Los componentes visuales están modularizados en ui/components. No agregar lógica de negocio en los archivos de UI.

Las fórmulas matemáticas residen estrictamente en core/util/GameUtils.kt.

# ⚔️ War of Men - RPG Fitness Tracker

**War of Men** es una aplicación nativa Android que gamifica el fitness, convirtiendo tus datos biométricos y entrenamientos en el progreso de un personaje de RPG.

> "Tu realidad física define tu destino virtual."

---

## 🏗️ Arquitectura del Proyecto (Clean Architecture)

El proyecto ha sido refactorizado (Nov 2025) siguiendo principios de **Clean Architecture** y **MVVM** para garantizar escalabilidad.

```text
com.negocio.warofmen
│
├── core/                  # Utilidades transversales
│   ├── navigation/        # Rutas de navegación (AppScreens)
│   └── util/              # Fórmulas matemáticas, Constantes, Extensions
│
├── data/                  # Capa de Datos (Single Source of Truth)
│   ├── model/             # Data Classes (PlayerCharacter, BodyLog, Quest)
│   ├── repository/        # Patrón Repositorio
│   └── source/            # DataStore, Gson y Providers estáticos
│
└── ui/                    # Capa de Presentación (Jetpack Compose)
    ├── components/        # Átomos visuales modulares (Charts, RPG Bars)
    ├── screens/           # Pantallas completas (Creation, Home, Stats)
    ├── theme/             # Sistema de Diseño (Dark Neon RPG)
    └── viewmodel/         # Gestión de Estado (StateFlow)

## 👨‍💻 Autor
Desarrollado por **Axel Jhostin**.