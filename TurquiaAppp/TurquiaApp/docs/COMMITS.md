# Guía de commits (GitHub)

La pauta pide commits **distribuidos entre los integrantes** y con **descripciones técnicas**.
Se sugiere el formato *Conventional Commits* (`tipo(ámbito): descripción en imperativo`).

Tipos: `feat` (funcionalidad) · `fix` (corrección) · `refactor` · `test` · `docs` · `chore` · `style`

## Secuencia sugerida (cada integrante hace commit de lo que desarrolla)

```
chore: crea proyecto Android con Compose, DataStore y Navigation
chore: agrega .gitignore y Gradle wrapper
feat(theme): define paleta turquesa y esquema claro/oscuro
feat(data): agrega modelos User y Word
feat(data): implementa LocalStorage con DataStore Preferences y Flow reactivo
feat(data): carga vocabulario inicial en SeedData
feat(domain): implementa Validators para formularios de la app
test(domain): agrega pruebas unitarias de Validators y TextNormalizer
feat(data): agrega PasswordHasher con sal y SHA-256
feat(data): implementa UserRepository con registro, login y sesión
feat(ui): crea ValidatedTextField con íconos de error y éxito
feat(auth): implementa pantallas de Login y Registro con RegisterViewModel
feat(nav): configura NavHost y barra de navegación inferior
feat(home): agrega pantalla de inicio con categorías animadas
feat(words): agrega lista de palabras con favoritos y eliminación
feat(words): agrega formulario para crear palabras personalizadas
feat(translator): implementa traductor TR/ES con búsqueda sin tildes
feat(device): integra cámara con FileProvider para foto de perfil
feat(ui): agrega componente ImagenInteligente (imagen circular o ícono por defecto)
feat(device): integra vibración con Vibrator y permiso VIBRATE
feat(anim): agrega animaciones shake y latido de favoritos
fix(profile): corrige orientación de la foto usando EXIF
docs: actualiza README con funcionalidades y pasos de ejecución
```

## Reglas del equipo

* Una rama por funcionalidad (`feature/login`, `feature/camara`) y *Pull Request* hacia `main`.
* Revisar el PR de un compañero antes de mezclarlo (deja evidencia de colaboración).
* **No hacer ningún commit después de la fecha de entrega** (la pauta asigna nota 1.0 si hay cambios posteriores).
* Evitar mensajes como "cambios", "arreglo" o "subiendo cosas".
