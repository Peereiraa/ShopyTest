package com.example.shopytest.aplicacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.shopytest.aplicacion.identificacion.Login;
import com.example.shopytest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * La clase {@code Inicio} representa la pantalla principal de la aplicación donde los usuarios pueden
 * explorar categorías, ver productos y acceder a otras funciones principales.
 *
 * También gestiona la navegación a otras actividades y muestra imágenes deslizantes en la parte superior.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class Inicio extends AppCompatActivity {


    /** ImageView para acceder a la pantalla de categorías. */
    private ImageView categoriasImageView;

    /** ImageView para acceder a la pantalla de carrito de compras. */
    private ImageView iconoCategorias;

    /** ImageView para acceder a la pantalla de carrito de compras. */
    private ImageView iconoCarrito;

    /** ImageView para acceder a la pantalla de perfil. */
    private ImageView iconoPerfil;

    /** ImageView para mostrar la foto de perfil del usuario. */
    private ImageView fotodeperfil;

    /** Instancia de FirebaseAuth para la autenticación de Firebase. */
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;


    /**
     * Método llamado cuando la actividad se está creando.
     *
     * @param savedInstanceState Datos que pueden haberse guardado del estado anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);


        fotodeperfil = findViewById(R.id.logo);
        ImageSlider imageSlider = findViewById(R.id.pasarimagenes);




        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        cargarImagenPerfil();

        // Asignación de vistas a variables
        categoriasImageView = findViewById(R.id.categorias);
        iconoCategorias = findViewById(R.id.icono_categorias);
        iconoCarrito = findViewById(R.id.icono_carrito);
        iconoPerfil = findViewById(R.id.icono_perfilbarra);

        CollectionReference prendasRef = db.collection("PrendasRopa");

        prendasRef.whereGreaterThan("Rebaja", 0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Itera sobre los documentos y agrega las imágenes al carrusel
                        ArrayList<SlideModel> slideModels = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Verifica si el campo "imageUrl" existe y no es nulo
                            if (document.contains("imageUrl")) {
                                String imageUrl = document.getString("imageUrl");
                                if (imageUrl != null) {
                                    slideModels.add(new SlideModel(imageUrl, ScaleTypes.FIT));
                                }
                            }
                        }

                        // Establece las imágenes en el carrusel
                        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja el caso en el que no se puedan recuperar las prendas de Firestore
                        Toast.makeText(Inicio.this, "Error al cargar las prendas desde Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace(); // Imprime el error en la consola para facilitar la depuración
                    }
                });


        // Configuración de clics en vistas
        iconoCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inicio.this, Categorias.class);
                startActivity(intent);
            }
        });


        if (currentUser != null) {
            iconoCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Inicio.this, Carrito.class);
                    startActivity(intent);
                }
            });
        } else {
            iconoCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Inicio.this, "Inicia sesión para acceder al carrito", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Inicio.this, Login.class));
                }
            });
        }


        if (currentUser != null) {
            // Si el usuario está autenticado, permite acceder al perfil
            iconoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Inicio.this, Perfil.class);
                    startActivity(intent);
                }
            });
        } else {
            // Si el usuario no está autenticado, muestra un mensaje o maneja el flujo de otra manera
            iconoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Por ejemplo, puedes mostrar un mensaje o llevar al usuario a la pantalla de inicio de sesión
                    Toast.makeText(Inicio.this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Inicio.this, Login.class));
                }
            });
        }


        if (currentUser != null) {
            // Si el usuario está autenticado, permite acceder al perfil
            fotodeperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Inicio.this, Perfil.class));
                }
            });
        } else {
            // Si el usuario no está autenticado, muestra un mensaje o maneja el flujo de otra manera
            fotodeperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Por ejemplo, puedes mostrar un mensaje o llevar al usuario a la pantalla de inicio de sesión
                    Toast.makeText(Inicio.this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Inicio.this, Login.class));
                }
            });
        }



        categoriasImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Inicio.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_categorias, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_inicio) {
                            // Navegar a la actividad o fragmento de "Inicio"
                            showAlert("Ya estas en la pantalla de inicio");
                            return true;
                        } else if (itemId == R.id.menu_categorias) {
                            Intent intent = new Intent(Inicio.this, Categorias.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.menu_carrito) {
                            // Navegar a la actividad o fragmento de "Carrito"
                            Intent intent = new Intent(Inicio.this, Carrito.class);
                            startActivity(intent);
                            return true;

                        } else if (itemId == R.id.menu_perfil) {
                            if (currentUser != null) {
                                // Navegar a la actividad o fragmento de "Perfil"
                                Intent intent = new Intent(Inicio.this, Perfil.class);
                                startActivity(intent);
                                return true;
                            }else{
                                Toast.makeText(Inicio.this, "Inicia sesión para acceder a esta función", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Inicio.this, Login.class));

                            }
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });


    }


    /**
     * Muestra una alerta con el mensaje proporcionado.
     *
     * @param message El mensaje a mostrar en la alerta.
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
        builder.setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Acción al hacer clic en Aceptar
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * Carga la imagen de perfil del usuario desde Firebase Storage o Google, según la autenticación.
     */
    private void cargarImagenPerfil() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                // Verifica si el usuario está autenticado con Google
                if (GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    // Si es autenticado con Google, carga la imagen de Google
                    Uri photoUrl = profile.getPhotoUrl();
                    if (photoUrl != null) {
                        Glide.with(this)
                                .load(photoUrl)
                                .into(fotodeperfil);
                    }
                } else {
                    // Si no es Google, carga la imagen desde el almacenamiento
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                            .child("fotousuario/" + currentUser.getUid() + "/imagenPerfil.jpg");

                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Aquí, 'uri' contiene la URL de descarga de la imagen
                        Glide.with(this)
                                .load(uri)
                                .into(fotodeperfil);
                    }).addOnFailureListener(exception -> {
                        // Manejar el caso en el que no se pueda obtener la URL de la imagen
                    });
                }
            }

            // Si el usuario está autenticado, también puedes hacer que el logo sea clicle para ir al perfil
            fotodeperfil.setOnClickListener(v -> {
                Intent intent = new Intent(Inicio.this, Perfil.class);
                startActivity(intent);
            });
        }
    }
}
