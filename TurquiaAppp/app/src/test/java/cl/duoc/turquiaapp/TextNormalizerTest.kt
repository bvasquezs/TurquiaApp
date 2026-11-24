package cl.duoc.turquiaapp

import cl.duoc.turquiaapp.domain.text.normalizeForSearch
import org.junit.Assert.assertEquals
import org.junit.Test

class TextNormalizerTest {
    @Test fun `ignora mayusculas`() = assertEquals("gato", "GaTo".normalizeForSearch())
    @Test fun `quita tildes del espanol`() = assertEquals("te", "Té".normalizeForSearch())
    @Test fun `iguala la i turca sin punto`() = assertEquals("kirmizi", "Kırmızı".normalizeForSearch())
    @Test fun `quita diacriticos turcos`() = assertEquals("tesekkurler", "Teşekkürler".normalizeForSearch())
    @Test fun `la I mayuscula con punto se normaliza`() = assertEquals("iki", "İki".normalizeForSearch())
    @Test fun `recorta espacios`() = assertEquals("su", "  Su ".normalizeForSearch())
}
