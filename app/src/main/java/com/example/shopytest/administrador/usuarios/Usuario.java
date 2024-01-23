package com.example.shopytest.administrador.usuarios;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Usuario.java
public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String photoUrl; // Puedes cambiar a URL si lo prefieres




    public static Usuario fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Usuario usuario = new Usuario();
        usuario.setId(documentSnapshot.getId());
        usuario.setNombre(documentSnapshot.getString("name"));
        usuario.setCorreo(documentSnapshot.getString("email"));
        usuario.setPhotoUrl(documentSnapshot.getString("photoUrl"));
        return usuario;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }



}
