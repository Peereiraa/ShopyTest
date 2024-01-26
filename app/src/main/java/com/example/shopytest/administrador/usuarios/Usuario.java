package com.example.shopytest.administrador.usuarios;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;


/**
 * Clase que representa a un usuario en la aplicación. Contiene información como el nombre, correo y URL de la foto del usuario.
 * Proporciona métodos para obtener y establecer estos datos, así como para convertir un DocumentSnapshot de Firestore en un objeto Usuario.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class Usuario {
    /**
     * Identificador único del usuario.
     */
    private String id;

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Correo electrónico del usuario.
     */
    private String correo;

    /**
     * URL de la foto del usuario.
     */
    private String photoUrl;



    /**
     * Método estático que crea y devuelve un objeto Usuario a partir de un DocumentSnapshot de Firestore.
     *
     * @param documentSnapshot DocumentSnapshot que contiene los datos del usuario.
     * @return Objeto Usuario creado a partir del DocumentSnapshot.
     */
    public static Usuario fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Usuario usuario = new Usuario();
        usuario.setId(documentSnapshot.getId());
        usuario.setNombre(documentSnapshot.getString("name"));
        usuario.setCorreo(documentSnapshot.getString("email"));
        usuario.setPhotoUrl(documentSnapshot.getString("photoUrl"));
        return usuario;
    }

    /**
     * Método que devuelve el identificador único del usuario.
     *
     * @return Identificador único del usuario.
     */
    public String getId() {
        return id;
    }

    /**
     * Método que establece el identificador único del usuario.
     *
     * @param id Nuevo identificador único del usuario.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Método que devuelve el nombre del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método que establece el nombre del usuario.
     *
     * @param nombre Nuevo nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método que devuelve el correo electrónico del usuario.
     *
     * @return Correo electrónico del usuario.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Método que establece el correo electrónico del usuario.
     *
     * @param correo Nuevo correo electrónico del usuario.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Método que devuelve la URL de la foto del usuario.
     *
     * @return URL de la foto del usuario.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Método que establece la URL de la foto del usuario.
     *
     * @param photoUrl Nueva URL de la foto del usuario.
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }



}
