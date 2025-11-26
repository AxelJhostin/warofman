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

## 🗺️ Roadmap (Próximos Pasos)
- [x] Sistema de Entrenamiento y Series.
- [x] Gráficas de Rendimiento.
- [ ] **Sistema de Inventario y Equipamiento (En proceso).**
- [ ] Migración de logs históricos a Room Database.
- [ ] Tienda de objetos.

## 👨‍💻 Autor
Desarrollado por **Axel Jhostin**.