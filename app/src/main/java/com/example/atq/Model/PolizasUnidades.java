package com.example.atq.Model;

public class PolizasUnidades extends Unidades {
    private String IDPoliza;
    private String EmpresaPoliza;
    private String NumeroPoliza;
    private String FechaVencimiento;

    public PolizasUnidades() {
    }

    public String getIDPoliza() {
        return IDPoliza;
    }

    public void setIDPoliza(String IDPoliza) {
        this.IDPoliza = IDPoliza;
    }

    public String getEmpresaPoliza() {
        return EmpresaPoliza;
    }

    public void setEmpresaPoliza(String empresaPoliza) {
        EmpresaPoliza = empresaPoliza;
    }

    public String getNumeroPoliza() {
        return NumeroPoliza;
    }

    public void setNumeroPoliza(String numeroPoliza) {
        NumeroPoliza = numeroPoliza;
    }

    public String getFechaVencimiento() {
        return FechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        FechaVencimiento = fechaVencimiento;
    }
}
