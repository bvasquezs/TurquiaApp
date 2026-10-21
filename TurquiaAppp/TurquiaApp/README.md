# TurquiaApp - Traductor Turco/Español para Niños

Aplicación móvil Android para que niños de 3 a 12 años aprendan vocabulario básico en turco
con apoyo de un adulto: palabras con emoji, traductor turco ⇄ español y diccionario personalizable.

**Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles · **Evaluación Parcial 2**

## 👥 Integrantes

* Bastian Vasquez
* Sebastian Lillo
* Ricardo Vargas

## 🎯 Contexto del problema

Muchas familias chileno-turcas (o interesadas en la cultura turca) no cuentan con material
interactivo y en español para que los niños aprendan turco. TurquiaApp ofrece vocabulario
organizado por categorías, ejemplos visuales, en una interfaz simple pensada para niños.

## ✅ Funcionalidades implementadas

| Requisito de la evaluación | Dónde está en la app |
|---|---|
| Interfaz organizada y navegación clara | Navegación con *Navigation Compose* y barra inferior (Inicio, Traductor, Favoritas, Perfil). Pantallas: Login, Registro, Inicio, Palabras por categoría, Nueva palabra, Traductor, Favoritas, Perfil |
| Formularios validados con íconos y mensajes visuales | 5 formularios: Login, Registro, Perfil del niño, Nueva palabra y Buscador del traductor. Cada campo muestra ícono propio, ícono ✓ (válido) o ⚠ (error), borde rojo y mensaje bajo el campo |
| Validaciones desde la lógica | `domain/validation/Validators.kt` (Kotlin puro, sin Android) + `ViewModel`s que exponen el estado. La UI solo pinta el error |
| Animaciones funcionales | Entrada escalonada de tarjetas, efecto de presión, "shake" del formulario al fallar, aparición animada de errores, latido del corazón de favoritos, giro del botón de dirección, transiciones entre pantallas y barra inferior |
| Estructura modular + persistencia local | MVVM + repositorios + almacenamiento local con **DataStore Preferences** (JSON): usuarios, palabras y favoritos. Sesión con `SharedPreferences`. Clave guardada con sal + SHA-256 |
| GitHub + Trello | Ver [`docs/TRELLO.md`](docs/TRELLO.md) y [`docs/COMMITS.md`](docs/COMMITS.md) |
| Al menos dos recursos nativos | 📷 **Cámara** (foto de perfil con `TakePicture()` + `FileProvider`, permiso `CAMERA` en tiempo de ejecución) · 📳 **Vibración** (servicio `Vibrator`, permiso `VIBRATE`, al marcar/desmarcar una palabra favorita) |

## 🛠️ Stack tecnológico

* **Lenguaje:** Kotlin 2.0
* **UI:** Jetpack Compose + Material 3
* **Arquitectura:** MVVM (Model-View-ViewModel) con repositorios e inyección manual (`AppContainer`)
* **Persistencia:** DataStore Preferences 1.1 (almacenamiento local clave-valor, sin base de datos)
* **Navegación:** Navigation Compose
* **Control de versiones y planificación:** Git, GitHub y Trello
* **SDK:** minSdk 26 · targetSdk/compileSdk 34

## 🗂️ Estructura del proyecto

```
app/src/main/java/cl/duoc/turquiaapp/
├── data/
│   ├── local/          # LocalStorage (DataStore), JsonMapper, datos iniciales, sesión
│   └── repository/     # UserRepository, WordRepository, PasswordHasher
├── di/                 # AppContainer (dependencias)
├── device/             # Recursos nativos: CameraSupport, VibrationSupport
├── domain/
│   ├── model/          # Category, User, Word
│   ├── text/           # Normalización de texto para buscar sin tildes
│   └── validation/     # Validators (reglas de todos los formularios)
├── viewmodel/          # ViewModels + estado de cada pantalla
└── ui/
    ├── theme/          # Colores y tema
    ├── components/     # ImagenInteligente, campo validado, tarjetas, botones, animación shake
    ├── navigation/     # Rutas y NavHost
    └── screens/        # auth, home, words, translator, profile
```

## 📥 Instrucciones de ejecución

Requisitos: **Android Studio Koala (2024.1) o superior**, JDK 17 y un emulador o teléfono con Android 8.0+ (API 26).

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/bvasquezs/TurquiaApp.git
   ```
2. Abrir la carpeta en Android Studio y esperar a que Gradle sincronice.
3. Ejecutar el módulo `app` (▶) en un emulador o dispositivo físico.
4. Pruebas unitarias: `./gradlew testDebugUnitTest`

### Notas para probar los recursos nativos

* **Cámara:** en el emulador configurar *Camera → Back: VirtualScene* (AVD > Advanced settings).
* **Vibración:** el emulador no vibra visiblemente; probar en un teléfono físico (con el modo silencio/vibración del equipo activo).

## 🧪 Cómo probar la app (flujo sugerido)

1. **Registrarse** con datos inválidos para ver los mensajes de error y la animación; luego con datos correctos.
2. En **Inicio**, abrir una categoría, tocar ❤️ para marcar favorita (el teléfono vibra).
3. En **Traductor**, escribir "gato" o "kedi" y cambiar la dirección con ⇄.
4. En **Perfil**, tomar una foto con 📷 y editar nombre/edad.
5. **Agregar palabra** desde el botón de una categoría; se guarda en el almacenamiento local.

## 📄 Licencia

Proyecto académico - Duoc UC.
