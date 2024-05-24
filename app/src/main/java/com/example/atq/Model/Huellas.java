package com.example.atq.Model;

public class Huellas {
    private String IDHuella;
    private String Contra;
    private String TipoEmpleado;
    private String nomUsuario;
    private String nomCompleto;

    public Huellas() {
    }

    public Huellas(String IDHuella, String nomCompleto) {
        this.IDHuella = IDHuella;
        this.nomCompleto = nomCompleto;
    }

    public Huellas(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    public String getIDHuella() {
        return IDHuella;
    }

    public void setIDHuella(String IDHuella) {
        this.IDHuella = IDHuella;
    }

    public String getContra() {
        return Contra;
    }

    public void setContra(String contra) {
        Contra = contra;
    }

    public String getTipoEmpleado() {
        return TipoEmpleado;
    }

    public void setTipoEmpleado(String tipoEmpleado) {
        TipoEmpleado = tipoEmpleado;
    }

    public String getNomUsuario() {
        return nomUsuario;
    }

    public void setNomUsuario(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    public String getNomCompleto() {
        return nomCompleto;
    }

    public void setNomCompleto(String nomCompleto) {
        this.nomCompleto = nomCompleto;
    }

    @Override
    public String toString() {
        return nomCompleto;
    }
}
