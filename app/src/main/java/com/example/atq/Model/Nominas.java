package com.example.atq.Model;

public class Nominas extends Huellas {

    private String IDNomina;
    private String IDEmpleado;
    private String FechaInicio;
    private String FechaFinal;
    private int HorasTrabajadas;
    private int PrecioHora;
    private int TotalAPagar;

    public Nominas() {
    }

    public String getIDNomina() {
        return IDNomina;
    }

    public void setIDNomina(String IDNomina) {
        this.IDNomina = IDNomina;
    }

    public String getIDEmpleado() {
        return IDEmpleado;
    }

    public void setIDEmpleado(String IDEmpleado) {
        this.IDEmpleado = IDEmpleado;
    }

    public String getFechaInicio() {
        return FechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        FechaInicio = fechaInicio;
    }

    public String getFechaFinal() {
        return FechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        FechaFinal = fechaFinal;
    }

    public int getHorasTrabajadas() {
        return HorasTrabajadas;
    }

    public void setHorasTrabajadas(int horasTrabajadas) {
        HorasTrabajadas = horasTrabajadas;
    }

    public int getPrecioHora() {
        return PrecioHora;
    }

    public void setPrecioHora(int precioHora) {
        PrecioHora = precioHora;
    }

    public int getTotalAPagar() {
        return TotalAPagar;
    }

    public void setTotalAPagar(int totalAPagar) {
        TotalAPagar = totalAPagar;
    }
}
