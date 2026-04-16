package com.srpafb.model;

import java.time.LocalDate;
import java.time.Period;



public class Persona {
    private int id;
    private int categoriaId;
    private String nombre;
    private String apellido;
    private String grado;
    private String dni;
    private String sexo;
    private LocalDate fechaNacimiento;
    private double peso;
    private double talla;
    private double imc;
    private Categoria categoria;

    public Persona() {}

    public Persona(int id, String nombre, String apellido, String dni, String sexo,
                   LocalDate fechaNacimiento, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.categoria = categoria;
    }

    public Persona(int id, String nombre, String apellido, String grado, String dni, String sexo,
                   LocalDate fechaNacimiento, Categoria categoria, double peso, double talla, double imc) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.grado = grado;
        this.dni = dni;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.categoria = categoria;
        this.peso = peso;
        this.talla = talla;
        this.imc = imc;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getTalla() { return talla; }
    public void setTalla(double talla) { this.talla = talla; }

    public double getImc() { return imc; }
    public void setImc(double imc) { this.imc = imc; }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public int getEdad() {
        return calcularEdad(fechaNacimiento);
    }

    public static int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
        LocalDate finDelAno = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        if (fechaNacimiento.isAfter(finDelAno)) {
            return 0;
        }
        return Period.between(fechaNacimiento, finDelAno).getYears();
    }

    public static double calcularImc(double peso, double talla) {
        if (peso <= 0 || talla <= 0) {
            return 0;
        }
        return peso / (talla * talla);
    }

    public String getCategoriaNombre() {
        return calcularCategoria(getEdad());
    }

    public static String calcularCategoria(int edad) {
        if (edad >= 15 && edad <= 25) {
            return "Categoria 1";
        } else if (edad >= 26 && edad <= 30) {
            return "Categoria 2";
        } else if (edad >= 31 && edad <= 35) {
            return "Categoria 3";
        } else if (edad >= 36 && edad <= 40) {
            return "Categoria 4";
        } else if (edad >= 41 && edad <= 45) {
            return "Categoria 5";
        } else if (edad >= 46 && edad <= 50) {
            return "Categoria 6";
        } else if (edad >= 51 && edad <= 55) {
            return "Categoria 7";
        } else if (edad >= 56) {
            return "Categoria X";
        }
        return "(sin categoría)";
    }

    public void actualizarImc() {
        this.imc = calcularImc(this.peso, this.talla);
    }

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
