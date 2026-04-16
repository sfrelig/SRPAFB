package com.srpafb.model;

import com.srpafb.service.PuntajeService;
import java.time.LocalDate;

public class Resultado {
    private int id;
    private Persona persona;
    private Prueba prueba;
    private LocalDate fecha;
    private double valor;

    public Resultado() {}

    public Resultado(int id, Persona persona, Prueba prueba, LocalDate fecha, double valor) {
        this.id = id;
        this.persona = persona;
        this.prueba = prueba;
        this.fecha = fecha;
        this.valor = valor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public Prueba getPrueba() { return prueba; }
    public void setPrueba(Prueba prueba) { this.prueba = prueba; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getValor() { return valor; }

    public double getPuntaje() {
        return calcularPuntaje(prueba, persona, valor);
    }

    public void setValor(double valor) { this.valor = valor; }

    public static double calcularPuntaje(Prueba prueba, Persona persona, double valor) {
        return PuntajeService.getInstance().obtenerPuntaje(prueba, persona, valor);
    }
}