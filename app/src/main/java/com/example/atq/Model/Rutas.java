package com.example.atq.Model;

public class Rutas {
    private String IDRuta;
    private String NombreRuta;
    private String FechaIngreso;

    public Rutas() {
    }

    public Rutas(String nombreRuta) {
        NombreRuta = nombreRuta;
    }

    public String getIDRuta() {
        return IDRuta;
    }

    public void setIDRuta(String IDRuta) {
        this.IDRuta = IDRuta;
    }

    public String getNombreRuta() {
        return NombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        NombreRuta = nombreRuta;
    }

    public String getFechaIngreso() {
        return FechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        FechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return NombreRuta;
    }
}
