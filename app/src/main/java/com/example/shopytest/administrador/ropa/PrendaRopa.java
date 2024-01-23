package com.example.shopytest.administrador.ropa;

import com.example.shopytest.administrador.usuarios.Usuario;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * La clase PrendaRopa representa una prenda de ropa en el sistema.
 * Contiene información como el ID, nombre, precio y URL de la imagen de la prenda.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class PrendaRopa {

    /**
     * ID único de la prenda de ropa.
     */
    private String id;

    /**
     * Nombre de la prenda de ropa.
     */
    private String nombre;

    /**
     * Precio de la prenda de ropa.
     */
    private Double precio;

    /**
     * URL de la imagen de la prenda de ropa.
     */
    private String urlImagen;

    /**
     * Obtiene el ID único de la prenda de ropa.
     *
     * @return El ID único de la prenda de ropa.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el ID único de la prenda de ropa.
     *
     * @param id El nuevo ID único de la prenda de ropa.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Constructor por defecto de la clase PrendaRopa.
     * Puedes dejarlo vacío o inicializar valores por defecto si es necesario.
     */
    public PrendaRopa() {

    }

    /**
     * Convierte un DocumentSnapshot de Firestore a una instancia de PrendaRopa.
     *
     * @param documentSnapshot DocumentSnapshot de Firestore que contiene datos de la prenda.
     * @return Una instancia de PrendaRopa con datos del DocumentSnapshot.
     */
    public static PrendaRopa fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        PrendaRopa prenda = new PrendaRopa();
        prenda.setId(documentSnapshot.getId());
        prenda.setNombre(documentSnapshot.getString("Nombre"));
        prenda.setPrecio(documentSnapshot.getDouble("Precio"));
        prenda.setUrlImagen(documentSnapshot.getString("UrlImagen"));
        return prenda;
    }

    // Métodos getter y setter

    /**
     * Obtiene el nombre de la prenda de ropa.
     *
     * @return El nombre de la prenda de ropa.
     */
    public String getNombre() {
        return nombre;
    }


    /**
     * Establece el nombre de la prenda de ropa.
     *
     * @param nombre El nuevo nombre de la prenda de ropa.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el precio de la prenda de ropa.
     *
     * @return El precio de la prenda de ropa.
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio de la prenda de ropa.
     *
     * @param precio El nuevo precio de la prenda de ropa.
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene la URL de la imagen de la prenda de ropa.
     *
     * @return La URL de la imagen de la prenda de ropa.
     */
    public String getUrlImagen() {
        return urlImagen;
    }

    /**
     * Establece la URL de la imagen de la prenda de ropa.
     *
     * @param urlImagen La nueva URL de la imagen de la prenda de ropa.
     */
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }


}
