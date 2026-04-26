package com.srpafb.model;

public class Baremo {
    private int id;
    private int pruebaId;
    private int categoriaId;
    private String genero;
    private double minValor;
    private double maxValor;
    private double puntaje;
    private String pruebaNombre;
    private String categoriaNombre;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPruebaId() {
        return pruebaId;
    }

    public void setPruebaId(int pruebaId) {
        this.pruebaId = pruebaId;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public double getMinValor() {
        return minValor;
    }

    public void setMinValor(double minValor) {
        this.minValor = minValor;
    }

    public double getMaxValor() {
        return maxValor;
    }

    public void setMaxValor(double maxValor) {
        this.maxValor = maxValor;
    }

    public double getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(double puntaje) {
        this.puntaje = puntaje;
    }

    public String getPruebaNombre() {
        return pruebaNombre;
    }

    public void setPruebaNombre(String pruebaNombre) {
        this.pruebaNombre = pruebaNombre;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }
}