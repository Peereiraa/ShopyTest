package com.example.shopytest.aplicacion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shopytest.aplicacion.identificacion.Login;
import com.example.shopytest.R;
import com.example.shopytest.profile.MiPerfil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * La clase {@code Perfil} representa la pantalla de perfil de usuario en la aplicación.
 * Permite a los usuarios ver y gestionar su perfil, así como realizar acciones relacionadas con la cuenta.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class Perfil extends AppCompatActivity {

    /** ImageView para navegar a la pantalla de inicio. */
    private ImageView iconoInicio;

    /** ImageView para navegar a la pantalla de carrito de compras. */
    private ImageView iconoCarrito;

    /** ImageView para navegar a la pantalla de categorías. */
    private ImageView iconoCategorias;

    /** TextView para mostrar el nombre del usuario. */
    private TextView tuNombre;

    /** ImageView para cargar la foto de perfil del usuario. */
    private ImageView fotoperfil;

    /** ImageView para navegar a la pantalla de perfil del usuario. */
    private ImageView miPerfil;

    /** ImageView para realizar la acción de cerrar sesión. */
    private ImageView imageCerrarSesion;

    /** Instancia de FirebaseAuth para la autenticación de Firebase. */
    private FirebaseAuth mAuth;

    /** Cliente de inicio de sesión de Google. */
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicialización de vistas y Firebase
        miPerfil = findViewById(R.id.icono_perfil_ir);
        fotoperfil = findViewById(R.id.fotoperfil);
        tuNombre = findViewById(R.id.tu_nombre);
        mAuth = FirebaseAuth.getInstance();
        iconoInicio = findViewById(R.id.icono_home);
        iconoCarrito = findViewById(R.id.icono_carrito);
        iconoCategorias = findViewById(R.id.icono_categorias);
        imageCerrarSesion = findViewById(R.id.imageCerrarSesion);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        cargarDatosUsuario();

        // Configuración de clics en vistas
        iconoInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, Inicio.class);
                startActivity(intent);
            }
        });

        iconoCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, Carrito.class);
                startActivity(intent);
            }
        });

        iconoCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, Categorias.class);
                startActivity(intent);
            }
        });


        imageCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAlertaCerrarSesion();

            }
        });

        miPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, MiPerfil.class);
                startActivity(intent);
            }
        });


    }

    /**
     * Muestra una alerta para confirmar la acción de cerrar sesión.
     */
    private void mostrarAlertaCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesion();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada, simplemente cerrar el diálogo
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Cierra la sesión actual del usuario.
     */
    private void cerrarSesion() {
        mAuth.signOut();
        revokeAccessGoogle();
        Intent intent = new Intent(Perfil.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Elimina la pila de actividades
        startActivity(intent);
        finish(); // Finaliza la actividad actual
    }

    /**
     * Revoca el acceso de la cuenta de Google al cerrar sesión.
     */
    private void revokeAccessGoogle() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Acción después de revocar el acceso exitosamente
                    // Por ejemplo, redirigir a la pantalla de inicio de sesión o realizar otras acciones necesarias
                    Toast.makeText(Perfil.this, "Se ha revocado el acceso con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    // Manejo de errores si la operación falla
                    Toast.makeText(Perfil.this, "Error al revocar el acceso", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Carga los datos del usuario actual, incluida la foto de perfil y el nombre.
     */
    private void cargarDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            for (UserInfo userInfo : user.getProviderData()) {
                if (userInfo.getProviderId().equals("google.com")) {
                    // Si se ha iniciado sesión con Google
                    String photoUrl = user.getPhotoUrl().toString();

                    // Cargar la imagen de perfil desde la URL utilizando Glide
                    Glide.with(this)
                            .load(photoUrl)
                            .into(fotoperfil);

                    // Obtener el nombre del usuario desde la cuenta de Google
                    String nombreUsuario = user.getDisplayName();
                    tuNombre.setText(nombreUsuario);

                    return; // Salir del método después de cargar la imagen y el nombre de la cuenta de Google
                }
            }

            // Si no se ha iniciado sesión con Google, cargar la imagen y el nombre desde Firestore y Firebase Storage
            String currentUserId = user.getUid();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("fotousuario/" + currentUserId + "/imagenPerfil.jpg");

            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Aquí, 'uri' contiene la URL de descarga de la imagen
                Glide.with(this /* contexto */)
                        .load(uri)
                        .into(fotoperfil);
            }).addOnFailureListener(exception -> {
                // Manejar el caso en el que no se pueda obtener la URL de la imagen
            });

            // Obtener el nombre del usuario desde Firestore
            FirebaseFirestore.getInstance().collection("user").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreUsuario = documentSnapshot.getString("name");
                            tuNombre.setText(nombreUsuario);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el caso en el que no se pueda obtener el nombre del usuario
                    });
        }
    }

}