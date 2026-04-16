package com.srpafb.service;

import com.srpafb.dao.BaremoDAO;
import com.srpafb.model.Persona;
import com.srpafb.model.Prueba;

public class PuntajeService {

    private static final PuntajeService INSTANCE = new PuntajeService();
    private final BaremoDAO baremoDAO = new BaremoDAO();

    private PuntajeService() {
    }

    public static PuntajeService getInstance() {
        return INSTANCE;
    }

    public double obtenerPuntaje(Prueba prueba, Persona persona, double valor) {
        if (prueba == null || prueba.getId() <= 0) {
            return valor;
        }
        if (persona == null || persona.getSexo() == null || persona.getSexo().trim().isEmpty()) {
            return valor;
        }

        int categoriaId = persona.getCategoria() != null ? persona.getCategoria().getId() : persona.getCategoriaId();
        if (categoriaId <= 0) {
            return valor;
        }

        double puntaje = baremoDAO.obtenerPuntaje(prueba.getId(), categoriaId, persona.getSexo().trim(), valor);
        return puntaje < 0 ? valor : puntaje;
    }
}
