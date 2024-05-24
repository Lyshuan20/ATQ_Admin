package com.example.atq.Model;

public class Unidades {
    private String IDUnidad;
    private String numUnidad;
    private String numSerie;
    private String placas;

    private String FotoUnidad;

    public Unidades() {
    }

    public Unidades(String numUnidad, String placas) {
        this.numUnidad = numUnidad;
        this.placas = placas;
    }

    public String getIDUnidad() {
        return IDUnidad;
    }

    public void setIDUnidad(String IDUnidad) {
        this.IDUnidad = IDUnidad;
    }

    public String getNumUnidad() {
        return numUnidad;
    }

    public void setNumUnidad(String numUnidad) {
        this.numUnidad = numUnidad;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getFotoUnidad() {
        return FotoUnidad;
    }

    public void setFotoUnidad(String fotoUnidad) {
        FotoUnidad = fotoUnidad;
    }

    @Override
    public String toString() {
        return numUnidad;
    }
}
