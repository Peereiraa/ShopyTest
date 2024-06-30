package com.example.shopytest.administrador.incidencias;

public class Incidencia {
    private String idUsuario;
    private String nombreUsuario;
    private String photoUrlUsuario;
    private String motivo;
    private String seccion;

    private String idIncidencia;

    public Incidencia() {
        // Constructor vacío requerido para Firebase
    }

    public Incidencia(String idIncidencia, String idUsuario, String nombreUsuario, String photoUrlUsuario, String motivo, String seccion) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.photoUrlUsuario = photoUrlUsuario;
        this.motivo = motivo;
        this.seccion = seccion;
        this.idIncidencia = idIncidencia;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPhotoUrlUsuario() {
        return photoUrlUsuario;
    }

    public void setPhotoUrlUsuario(String photoUrlUsuario) {
        this.photoUrlUsuario = photoUrlUsuario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(String idIncidencia) {
        this.idIncidencia = idIncidencia;
    }
}
