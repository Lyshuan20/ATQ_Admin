package com.example.atq.Model;

public class TipoGasto {
    String IDTipoGasto;
    String NombreGasto;

    public TipoGasto(String IDTipoGasto, String nombreGasto) {
        this.IDTipoGasto = IDTipoGasto;
        NombreGasto = nombreGasto;
    }

    public String getIDTipoGasto() {
        return IDTipoGasto;
    }

    public void setIDTipoGasto(String IDTipoGasto) {
        this.IDTipoGasto = IDTipoGasto;
    }

    public String getNombreGasto() {
        return NombreGasto;
    }

    public void setNombreGasto(String nombreGasto) {
        NombreGasto = nombreGasto;
    }

    @Override
    public String toString() {
        return NombreGasto;
    }
}
