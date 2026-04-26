package com.srpafb.model;

import java.time.LocalDate;
import java.time.Period;

public class ResultadoInforme {
    private String grado;
    private String apellidoNombre;
    private String dni;
    private LocalDate fechaNacimiento;
    private double peso;
    private double talla;
    private double imc;
    private String sexo;
    private int edad;
    private String categoria;
    private int flexionesCantidad;
    private double flexionesPuntaje;
    private int abdominalesCantidad;
    private double abdominalesPuntaje;
    private int barrasCantidad;
    private double barrasPuntaje;
    private double carreraTiempo;
    private double carreraPuntaje;
    private double promedio;
    private boolean aprueba;

    // Constructor vacío
    public ResultadoInforme() {}

    // Getters y setters
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public String getApellidoNombre() { return apellidoNombre; }
    public void setApellidoNombre(String apellidoNombre) { this.apellidoNombre = apellidoNombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        if (fechaNacimiento != null) {
            this.edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
    }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getTalla() { return talla; }
    public void setTalla(double talla) {
        this.talla = talla;
        calcularIMC();
    }

    public double getImc() { return imc; }
    public void setImc(double imc) { this.imc = imc; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getFlexionesCantidad() { return flexionesCantidad; }
    public void setFlexionesCantidad(int flexionesCantidad) { this.flexionesCantidad = flexionesCantidad; }

    public double getFlexionesPuntaje() { return flexionesPuntaje; }
    public void setFlexionesPuntaje(double flexionesPuntaje) { this.flexionesPuntaje = flexionesPuntaje; }

    public int getAbdominalesCantidad() { return abdominalesCantidad; }
    public void setAbdominalesCantidad(int abdominalesCantidad) { this.abdominalesCantidad = abdominalesCantidad; }

    public double getAbdominalesPuntaje() { return abdominalesPuntaje; }
    public void setAbdominalesPuntaje(double abdominalesPuntaje) { this.abdominalesPuntaje = abdominalesPuntaje; }

    public int getBarrasCantidad() { return barrasCantidad; }
    public void setBarrasCantidad(int barrasCantidad) { this.barrasCantidad = barrasCantidad; }

    public double getBarrasPuntaje() { return barrasPuntaje; }
    public void setBarrasPuntaje(double barrasPuntaje) { this.barrasPuntaje = barrasPuntaje; }

    public double getCarreraTiempo() { return carreraTiempo; }
    public void setCarreraTiempo(double carreraTiempo) { this.carreraTiempo = carreraTiempo; }

    public double getCarreraPuntaje() { return carreraPuntaje; }
    public void setCarreraPuntaje(double carreraPuntaje) { this.carreraPuntaje = carreraPuntaje; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) {
        this.promedio = promedio;
        this.aprueba = promedio >= 70;
    }

    public boolean isAprueba() { return aprueba; }
    public void setAprueba(boolean aprueba) { this.aprueba = aprueba; }

    private void calcularIMC() {
        if (peso > 0 && talla > 0) {
            this.imc = peso / (talla * talla);
        }
    }

    public void calcularPromedio() {
        double suma = flexionesPuntaje + abdominalesPuntaje + barrasPuntaje + carreraPuntaje;
        setPromedio(suma / 4.0);
    }
}