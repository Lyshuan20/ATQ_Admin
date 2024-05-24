package com.example.atq.Model;

public class GastosUnidades {
    private String IDGasto;
    private String NumUnidad;
    private String FechaGasto;
    private String TipoGasto;
    private int Monto;
    private String Descripción;

    public GastosUnidades() {

    }

    public GastosUnidades(int monto, String numUnidad) {
        NumUnidad = numUnidad;
        Monto = monto;
    }

    public GastosUnidades(String numUnidad, String fechaGasto, String tipoGasto, int monto, String descripción) {
        NumUnidad = numUnidad;
        FechaGasto = fechaGasto;
        TipoGasto = tipoGasto;
        Monto = monto;
        Descripción = descripción;
    }

    public String getNumUnidad() {
        return NumUnidad;
    }

    public void setNumUnidad(String numUnidad) {
        NumUnidad = numUnidad;
    }

    public String getIDGasto() {
        return IDGasto;
    }

    public void setIDGasto(String IDGasto) {
        this.IDGasto = IDGasto;
    }

    public String getFechaGasto() {
        return FechaGasto;
    }

    public void setFechaGasto(String fechaGasto) {
        FechaGasto = fechaGasto;
    }

    public String getTipoGasto() {
        return TipoGasto;
    }

    public void setTipoGasto(String tipoGasto) {
        TipoGasto = tipoGasto;
    }

    public int getMonto() {
        return Monto;
    }

    public void setMonto(int monto) {
        Monto = monto;
    }

    public String getDescripción() {
        return Descripción;
    }

    public void setDescripción(String descripción) {
        Descripción = descripción;
    }
}
