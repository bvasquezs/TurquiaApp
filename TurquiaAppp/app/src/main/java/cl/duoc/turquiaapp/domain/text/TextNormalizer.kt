package cl.duoc.turquiaapp.domain.text

import java.text.Normalizer
import java.util.Locale

private val DIACRITICS = Regex("\\p{Mn}+")

/**
 * Normaliza un texto para comparar palabras sin importar mayúsculas ni tildes:
 * "Kırmızı" == "kirmizi", "Teşekkürler" == "tesekkurler", "Té" == "te".
 */
fun String.normalizeForSearch(): String {
    val lower = this.trim().replace('ı', 'i').replace('İ', 'I').lowercase(Locale.ROOT)
    return Normalizer.normalize(lower, Normalizer.Form.NFD).replace(DIACRITICS, "")
}
