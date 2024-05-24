package com.example.atq.Model;

public class Asistencias extends Huellas {
    String IDEmpleado;
    String NombreEmpleado;
    String FechaAsistencia;
    String HoraEntrada;
    String HoraSalida;

    public Asistencias(String IDEmpleado, String nombreEmpleado, String fecha, String horaEntrada, String horaSalida) {
        this.IDEmpleado = IDEmpleado;
        NombreEmpleado = nombreEmpleado;
        FechaAsistencia = fecha;
        HoraEntrada = horaEntrada;
        HoraSalida = horaSalida;
    }

    public Asistencias() {
    }

    public String getNombreEmpleado() {
        return NombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        NombreEmpleado = nombreEmpleado;
    }

    public String getIDEmpleado() {
        return IDEmpleado;
    }

    public void setIDEmpleado(String IDEmpleado) {
        this.IDEmpleado = IDEmpleado;
    }

    public String getFechaAsistencia() {
        return FechaAsistencia;
    }

    public void setFechaAsistencia(String fechaAsistencia) {
        FechaAsistencia = fechaAsistencia;
    }

    public String getHoraEntrada() {
        return HoraEntrada;
    }

    public void setHoraEntrada(String HoraEntrada) {
        this.HoraEntrada = HoraEntrada;
    }

    public String getHoraSalida() {
        return HoraSalida;
    }

    public void setHoraSalida(String HoraSalida) {
        this.HoraSalida = HoraSalida;
    }
}
