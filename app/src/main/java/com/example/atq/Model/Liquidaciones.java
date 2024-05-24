package com.example.atq.Model;

public class Liquidaciones {
    private String IDLiquidacion;
    private String FechaLiquidacion;
    private int CantidadLiquidacion;
    private int NumeroVueltas;
    private int CantidadCombustible;
    private int NumeroBoletera1;
    private int NumeroBoletera2;
    private int NumeroBoletera3;
    private String NombreOperador;
    private String RutaLiquidacion;
    private String NumUnidad;

    public Liquidaciones() {
    }

    //REPORTES
    public Liquidaciones(String Fecha, int cantidadLiquidacion, String nombreOperador, String numUnidad, String ruta
            , int Vueltas) {
        FechaLiquidacion = Fecha;
        CantidadLiquidacion = cantidadLiquidacion;
        NombreOperador = nombreOperador;
        NumUnidad = numUnidad;
        RutaLiquidacion = ruta;
        NumeroVueltas = Vueltas;
    }

    //ROLES
    public Liquidaciones(int cantidadLiquidacion, String nombreOperador, String numUnidad, String rutaLiquidacion) {
        CantidadLiquidacion = cantidadLiquidacion;
        NombreOperador = nombreOperador;
        RutaLiquidacion = rutaLiquidacion;
        NumUnidad = numUnidad;
    }

    //GANACIA RUTA
    public Liquidaciones(int cantidadLiquidacion, String rutaLiquidacion) {
        CantidadLiquidacion = cantidadLiquidacion;
        RutaLiquidacion = rutaLiquidacion;
    }

    //GANACIA CHOFER Y UNIDAD
    public Liquidaciones(int cantidadLiquidacion, String nombreOperador, String numUnidad) {
        CantidadLiquidacion = cantidadLiquidacion;
        NombreOperador = nombreOperador;
        NumUnidad = numUnidad;
    }

    public String getIDLiquidacion() {
        return IDLiquidacion;
    }

    public void setIDLiquidacion(String IDLiquidacion) {
        this.IDLiquidacion = IDLiquidacion;
    }

    public String getFechaLiquidacion() {
        return FechaLiquidacion;
    }

    public void setFechaLiquidacion(String fechaLiquidacion) {
        FechaLiquidacion = fechaLiquidacion;
    }

    public int getCantidadLiquidacion() {
        return CantidadLiquidacion;
    }

    public void setCantidadLiquidacion(int cantidadLiquidacion) {
        CantidadLiquidacion = cantidadLiquidacion;
    }

    public int getNumeroVueltas() {
        return NumeroVueltas;
    }

    public void setNumeroVueltas(int numeroVueltas) {
        NumeroVueltas = numeroVueltas;
    }

    public int getCantidadCombustible() {
        return CantidadCombustible;
    }

    public void setCantidadCombustible(int cantidadCombustible) {
        CantidadCombustible = cantidadCombustible;
    }

    public int getNumeroBoletera1() {
        return NumeroBoletera1;
    }

    public void setNumeroBoletera1(int numeroBoletera1) {
        NumeroBoletera1 = numeroBoletera1;
    }

    public int getNumeroBoletera2() {
        return NumeroBoletera2;
    }

    public void setNumeroBoletera2(int numeroBoletera2) {
        NumeroBoletera2 = numeroBoletera2;
    }

    public int getNumeroBoletera3() {
        return NumeroBoletera3;
    }

    public void setNumeroBoletera3(int numeroBoletera3) {
        NumeroBoletera3 = numeroBoletera3;
    }

    public String getNombreOperador() {
        return NombreOperador;
    }

    public void setNombreOperador(String nombreOperador) {
        NombreOperador = nombreOperador;
    }

    public String getRutaLiquidacion() {
        return RutaLiquidacion;
    }

    public void setRutaLiquidacion(String rutaLiquidacion) {
        RutaLiquidacion = rutaLiquidacion;
    }

    public String getNumUnidad() {
        return NumUnidad;
    }

    public void setNumUnidad(String numUnidad) {
        NumUnidad = numUnidad;
    }
}
