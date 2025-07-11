# 📱 AsturNatura

**AsturNatura** es una app Android nativa desarrollada con Kotlin que permite descubrir y filtrar espacios naturales de Asturias, como playas, parques, lagos o ríos. Incorpora filtros interactivos, vista en lista y en mapa, y funcionalidades de favoritos.

---

## 🛠️ Tecnologías usadas

- **Lenguaje:** Kotlin  
- **Arquitectura:** MVVM  
- **UI:** Jetpack (RecyclerView, Navigation, Material Components)  
- **Red:** Retrofit + Moshi  
- **Persistencia:** Room  
- **Testing:** Espresso + JUnit  
- **Otros:** ViewModel, LiveData, Navigation Component

---

## 🚀 Cómo compilar el proyecto

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/asturnatura.git
   ```

2. **Abrir en Android Studio (Hedgehog o posterior).**

3. **Sync Gradle:** Se usa Kotlin DSL con `libs.versions.toml`, asegúrate de tener activado el soporte de `Version Catalog`.

---

## 📸 Características principales

- ✔️ Búsqueda por texto (nombre o descripción).
- ✔️ Filtro múltiple por tipo (playa, lago, parque, etc.).
- ✔️ Vista lista y mapa.
- ✔️ Guardado de favoritos.
- ✔️ Adaptación a tablets (modo landscape).
- ✔️ Test UI con Espresso.

---

## 🧪 Pruebas

Para lanzar las pruebas instrumentadas:

```bash
./gradlew connectedAndroidTest
```

---

## 📂 Estructura básica del proyecto

```
AsturNatura/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/es/uniovi/asturnatura/   ← lógica y vista MVVM
│   │   │   └── res/                          ← layouts, drawables, etc.
│   │   ├── androidTest/                      ← tests UI (Espresso)
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/libs.versions.toml                ← gestión de dependencias
```

---

