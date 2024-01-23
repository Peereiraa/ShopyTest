package com.example.shopytest.administrador.ropa;

import com.example.shopytest.administrador.usuarios.Usuario;
import com.google.firebase.firestore.DocumentSnapshot;

public class PrendaRopa {

    private String id;
    private String nombre;
    private Double precio;
    private String urlImagen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PrendaRopa() {
        // Puedes dejarlo vacío o inicializar valores por defecto si es necesario
    }

    public static PrendaRopa fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        PrendaRopa prenda = new PrendaRopa();
        prenda.setId(documentSnapshot.getId());
        prenda.setNombre(documentSnapshot.getString("Nombre"));
        prenda.setPrecio(documentSnapshot.getDouble("Precio"));
        prenda.setUrlImagen(documentSnapshot.getString("UrlImagen"));
        return prenda;
    }

    // Métodos getter y setter
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }


}
