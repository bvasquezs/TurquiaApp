package com.turquia.backend.controller;

import com.turquia.backend.model.Palabra;
import com.turquia.backend.service.PalabraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/palabras")
@CrossOrigin(origins = "*") // Permite que Android se conecte sin bloqueos
public class PalabraController {

    @Autowired
    private PalabraService palabraService;

    @GetMapping
    public List<Palabra> listarTodas() {
        return palabraService.obtenerTodas();
    }

    @GetMapping("/categoria/{categoria}")
    public List<Palabra> listarPorCategoria(@PathVariable String categoria) {
        return palabraService.obtenerPorCategoria(categoria);
    }

    @PostMapping
    public Palabra crear(@RequestBody Palabra palabra) {
        return palabraService.guardarPalabra(palabra);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        palabraService.eliminarPalabra(id);
    }
}