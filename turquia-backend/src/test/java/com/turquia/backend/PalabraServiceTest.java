package com.turquia.backend;

import com.turquia.backend.model.Palabra;
import com.turquia.backend.repository.PalabraRepository;
import com.turquia.backend.service.PalabraService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PalabraServiceTest {

    @Mock
    private PalabraRepository palabraRepository;

    @InjectMocks
    private PalabraService palabraService;

    @Test
    public void testObtenerTodas() {

        Palabra p1 = new Palabra("Hola", "Merhaba", "Saludos", "nino");
        Palabra p2 = new Palabra("Gracias", "Teşekkürler", "Saludos", "nino");
        when(palabraRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Palabra> resultado = palabraService.obtenerTodas();

        assertEquals(2, resultado.size());
        assertEquals("Merhaba", resultado.get(0).getTurco());
        verify(palabraRepository, times(1)).findAll();
    }

    @Test
    public void testGuardarPalabra() {

        Palabra nueva = new Palabra("Agua", "Su", "Comida", "nino");
        when(palabraRepository.save(nueva)).thenReturn(nueva);

        Palabra guardada = palabraService.guardarPalabra(nueva);
        assertEquals("Su", guardada.getTurco());
        verify(palabraRepository, times(1)).save(nueva);
    }
}