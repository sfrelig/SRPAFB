package com.sistemapafb.app;

import com.srpafb.dao.*;
import com.srpafb.model.*;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        PersonaDAO personaDAO = new PersonaDAO();
        PruebaDAO pruebaDAO = new PruebaDAO();
        ResultadoDAO resultadoDAO = new ResultadoDAO();

        // 1) Insertar categoría
        Categoria cat = new Categoria(0, "Adulto");
        categoriaDAO.insertar(cat);

        // 2) Insertar persona
        Persona p = new Persona(
                0,
                "Juan",
                "Pérez",
                "12345678",
                LocalDate.of(1990, 5, 10),
                categoriaDAO.obtenerTodas().get(0)
        );
        personaDAO.insertar(p);

        // 3) Insertar prueba
        Prueba pr = new Prueba(0, "Abdominales", "reps");
        pruebaDAO.insertar(pr);

        // 4) Insertar resultado
        Resultado r = new Resultado(
                0,
                personaDAO.obtenerTodas().get(0),
                pruebaDAO.obtenerTodas().get(0),
                LocalDate.now(),
                42.5
        );
        resultadoDAO.insertar(r);

        System.out.println("Sistema funcionando correctamente.");
    }
}