package cl.duoc.turquiaapp

import cl.duoc.turquiaapp.domain.model.Category
import cl.duoc.turquiaapp.domain.validation.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test fun `nombre vacio es invalido`() = assertNotNull(Validators.personName("  "))
    @Test fun `nombre con numeros es invalido`() = assertNotNull(Validators.personName("Ana2"))
    @Test fun `nombre con tilde y espacio es valido`() = assertNull(Validators.personName("María José"))

    @Test fun `correo sin arroba es invalido`() = assertNotNull(Validators.email("correo.cl"))
    @Test fun `correo correcto es valido`() = assertNull(Validators.email("ric.vargasc@duocuc.cl"))

    @Test fun `contrasena corta es invalida`() = assertEquals("Mínimo 8 caracteres", Validators.password("Ab1"))
    @Test fun `contrasena sin numero es invalida`() =
        assertEquals("Incluye al menos un número", Validators.password("Abcdefgh"))
    @Test fun `contrasena sin mayuscula es invalida`() =
        assertEquals("Incluye al menos una mayúscula", Validators.password("abcdefg1"))
    @Test fun `contrasena fuerte es valida`() = assertNull(Validators.password("Turquia2026"))

    @Test fun `confirmacion distinta es invalida`() =
        assertEquals("Las contraseñas no coinciden", Validators.confirmPassword("Abc12345", "Abc12346"))
    @Test fun `confirmacion igual es valida`() = assertNull(Validators.confirmPassword("Abc12345", "Abc12345"))

    @Test fun `edad fuera de rango es invalida`() {
        assertNotNull(Validators.childAge("2"))
        assertNotNull(Validators.childAge("13"))
    }
    @Test fun `edad no numerica es invalida`() = assertNotNull(Validators.childAge("abc"))
    @Test fun `edad dentro de rango es valida`() = assertNull(Validators.childAge("7"))

    @Test fun `palabra turca con caracteres especiales es valida`() =
        assertNull(Validators.word("Teşekkürler", "la palabra"))
    @Test fun `palabra con simbolos es invalida`() = assertNotNull(Validators.word("hola!!", "la palabra"))

    @Test fun `categoria nula es invalida`() = assertNotNull(Validators.category(null))
    @Test fun `categoria elegida es valida`() = assertNull(Validators.category(Category.ANIMALES))

    @Test fun `busqueda vacia no es error`() = assertNull(Validators.searchText(""))
    @Test fun `busqueda con digitos es invalida`() = assertNotNull(Validators.searchText("gato3"))
}
