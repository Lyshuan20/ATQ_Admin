package com.example.atq.Model;

public class Choferes extends ChoferesLicencia {
    private String IDChofer;
    private String Fecha_nac;
    private String Fecha_Ing;
    private String telefono;
    private String NombreChofer;
    private String ApellidosChofer;
    private String Domicilio;
    private String CURP;
    private String TipoSangre;
    private String FotoChofer;
    private String FotoLic1;
    private String FotoLic2;

    public Choferes() {
    }

    public Choferes(String nombreChofer, String apellidosChofer) {
        NombreChofer = nombreChofer;
        ApellidosChofer = apellidosChofer;
    }

    public String getIDChofer() {
        return IDChofer;
    }

    public void setIDChofer(String IDChofer) {
        this.IDChofer = IDChofer;
    }

    public String getFecha_nac() {
        return Fecha_nac;
    }

    public void setFecha_nac(String fecha_nac) {
        Fecha_nac = fecha_nac;
    }

    public String getFecha_Ing() {
        return Fecha_Ing;
    }

    public void setFecha_Ing(String fecha_Ing) {
        Fecha_Ing = fecha_Ing;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreChofer() {
        return NombreChofer;
    }

    public void setNombreChofer(String nombreChofer) {
        NombreChofer = nombreChofer;
    }

    public String getApellidosChofer() {
        return ApellidosChofer;
    }

    public void setApellidosChofer(String apellidosChofer) {
        ApellidosChofer = apellidosChofer;
    }

    public String getDomicilio() {
        return Domicilio;
    }

    public void setDomicilio(String domicilio) {
        Domicilio = domicilio;
    }

    public String getCURP() {
        return CURP;
    }

    public void setCURP(String CURP) {
        this.CURP = CURP;
    }

    public String getTipoSangre() {
        return TipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        TipoSangre = tipoSangre;
    }

    public String getFotoChofer() {
        return FotoChofer;
    }

    public void setFotoChofer(String fotoChofer) {
        FotoChofer = fotoChofer;
    }

    public String getFotoLic1() {
        return FotoLic1;
    }

    public void setFotoLic1(String fotoLic1) {
        FotoLic1 = fotoLic1;
    }

    public String getFotoLic2() {
        return FotoLic2;
    }

    public void setFotoLic2(String fotoLic2) {
        FotoLic2 = fotoLic2;
    }

    @Override
    public String toString() {
        return NombreChofer;
    }
}
