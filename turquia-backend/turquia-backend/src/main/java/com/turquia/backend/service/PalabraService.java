package com.turquia.backend.service;

import com.turquia.backend.model.Palabra;
import com.turquia.backend.repository.PalabraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PalabraService {

    @Autowired
    private PalabraRepository palabraRepository;

    public List<Palabra> obtenerTodas() {
        return palabraRepository.findAll();
    }

    public List<Palabra> obtenerPorCategoria(String categoria) {
        return palabraRepository.findByCategoria(categoria);
    }

    public Palabra guardarPalabra(Palabra palabra) {
        return palabraRepository.save(palabra);
    }

    public void eliminarPalabra(Long id) {
        palabraRepository.deleteById(id);
    }
}