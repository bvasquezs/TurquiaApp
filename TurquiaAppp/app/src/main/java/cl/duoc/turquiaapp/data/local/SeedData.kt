package cl.duoc.turquiaapp.data.local

import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.domain.model.Word

/** Vocabulario inicial que se carga la primera vez que se abre la app. */
object SeedData {
    private fun w(c: Category, tr: String, es: String, emoji: String) =
        Word(turkish = tr, spanish = es, category = c.name, emoji = emoji)

    val words: List<Word> = listOf(
        // Saludos
        w(Category.SALUDOS, "Merhaba", "Hola", "👋"),
        w(Category.SALUDOS, "Günaydın", "Buenos días", "☀️"),
        w(Category.SALUDOS, "İyi geceler", "Buenas noches", "🌙"),
        w(Category.SALUDOS, "Teşekkürler", "Gracias", "🙏"),
        w(Category.SALUDOS, "Lütfen", "Por favor", "😊"),
        w(Category.SALUDOS, "Evet", "Sí", "👍"),
        w(Category.SALUDOS, "Hayır", "No", "👎"),
        w(Category.SALUDOS, "Hoşça kal", "Adiós", "🖐️"),
        // Animales
        w(Category.ANIMALES, "Kedi", "Gato", "🐱"),
        w(Category.ANIMALES, "Köpek", "Perro", "🐶"),
        w(Category.ANIMALES, "Kuş", "Pájaro", "🐦"),
        w(Category.ANIMALES, "Balık", "Pez", "🐟"),
        w(Category.ANIMALES, "At", "Caballo", "🐴"),
        w(Category.ANIMALES, "İnek", "Vaca", "🐮"),
        w(Category.ANIMALES, "Tavşan", "Conejo", "🐰"),
        w(Category.ANIMALES, "Aslan", "León", "🦁"),
        // Colores
        w(Category.COLORES, "Kırmızı", "Rojo", "🔴"),
        w(Category.COLORES, "Mavi", "Azul", "🔵"),
        w(Category.COLORES, "Yeşil", "Verde", "🟢"),
        w(Category.COLORES, "Sarı", "Amarillo", "🟡"),
        w(Category.COLORES, "Siyah", "Negro", "⚫"),
        w(Category.COLORES, "Beyaz", "Blanco", "⚪"),
        w(Category.COLORES, "Turuncu", "Naranja", "🟠"),
        w(Category.COLORES, "Mor", "Morado", "🟣"),
        // Números
        w(Category.NUMEROS, "Bir", "Uno", "1️⃣"),
        w(Category.NUMEROS, "İki", "Dos", "2️⃣"),
        w(Category.NUMEROS, "Üç", "Tres", "3️⃣"),
        w(Category.NUMEROS, "Dört", "Cuatro", "4️⃣"),
        w(Category.NUMEROS, "Beş", "Cinco", "5️⃣"),
        w(Category.NUMEROS, "Altı", "Seis", "6️⃣"),
        w(Category.NUMEROS, "Yedi", "Siete", "7️⃣"),
        w(Category.NUMEROS, "Sekiz", "Ocho", "8️⃣"),
        w(Category.NUMEROS, "Dokuz", "Nueve", "9️⃣"),
        w(Category.NUMEROS, "On", "Diez", "🔟"),
        // Familia
        w(Category.FAMILIA, "Anne", "Mamá", "👩"),
        w(Category.FAMILIA, "Baba", "Papá", "👨"),
        w(Category.FAMILIA, "Kardeş", "Hermano/a", "🧒"),
        w(Category.FAMILIA, "Dede", "Abuelo", "👴"),
        w(Category.FAMILIA, "Nine", "Abuela", "👵"),
        w(Category.FAMILIA, "Bebek", "Bebé", "👶"),
        w(Category.FAMILIA, "Arkadaş", "Amigo", "🤝"),
        // Comida
        w(Category.COMIDA, "Elma", "Manzana", "🍎"),
        w(Category.COMIDA, "Su", "Agua", "💧"),
        w(Category.COMIDA, "Süt", "Leche", "🥛"),
        w(Category.COMIDA, "Ekmek", "Pan", "🍞"),
        w(Category.COMIDA, "Peynir", "Queso", "🧀"),
        w(Category.COMIDA, "Muz", "Plátano", "🍌"),
        w(Category.COMIDA, "Çay", "Té", "🍵"),
        w(Category.COMIDA, "Yumurta", "Huevo", "🥚")
    )
}
