package com.example.atq.Model;

public class Usuarios extends Huellas {
    private String NombreUsuario;
    private String NombreCompleto;
    private String Contra;
    private String Email;
    private String TipoUsuario;

    public Usuarios() {
    }

    public Usuarios(String nombreUsuario, String nombreCompleto, String contra) {
        NombreUsuario = nombreUsuario;
        NombreCompleto = nombreCompleto;
        Contra = contra;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNombreUsuario() {
        return NombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        NombreUsuario = nombreUsuario;
    }

    public String getNombreCompleto() {
        return NombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        NombreCompleto = nombreCompleto;
    }

    public String getContra() {
        return Contra;
    }

    public void setContra(String contra) {
        Contra = contra;
    }

    public String getTipoUsuario() {
        return TipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        TipoUsuario = tipoUsuario;
    }

    @Override
    public String toString() {
        return NombreUsuario;
    }
}
