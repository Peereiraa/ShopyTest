package com.example.shopytest.administrador;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.shopytest.Login;
import com.example.shopytest.R;
import com.example.shopytest.administrador.ropa.AgregarPrenda;
import com.example.shopytest.administrador.ropa.EliminarRopa;
import com.example.shopytest.administrador.usuarios.ListaUsuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MenuPantallaAdminActivity extends AppCompatActivity {


    private View agregarprenda, gestionarusuarios, modificarprenda, borrarprenda;

    private TextView textCerrarSesion, nombredeadmin;
    private ImageView imageCerrarSesion;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pantalla_admin);


        agregarprenda = findViewById(R.id.crearprenda);
        mAuth = FirebaseAuth.getInstance();
        textCerrarSesion = findViewById(R.id.textCerrarSesion);
        imageCerrarSesion = findViewById(R.id.imageCerrarSesion);
        nombredeadmin = findViewById(R.id.nombredeadmin);
        gestionarusuarios = findViewById(R.id.gestionarusuarios);
        modificarprenda = findViewById(R.id.modificarprenda);
        borrarprenda = findViewById(R.id.borrarprenda);
        cargarDatos();


        borrarprenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPantallaAdminActivity.this, EliminarRopa.class);
                startActivity(intent);
            }
        });

        agregarprenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPantallaAdminActivity.this, AgregarPrenda.class);
                startActivity(intent);
            }
        });

        gestionarusuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPantallaAdminActivity.this, ListaUsuarios.class);
                startActivity(intent);
            }
        });

        textCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuPantallaAdminActivity.this);
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
        });

        imageCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuPantallaAdminActivity.this);
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

        });

    }

    private void cerrarSesion() {
        mAuth.signOut();
        Intent intent = new Intent(MenuPantallaAdminActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Elimina la pila de actividades
        startActivity(intent);
        finish(); // Finaliza la actividad actual
    }

    private void cargarDatos() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            String currentUserId = currentUser.getUid();

            FirebaseFirestore.getInstance().collection("user").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreUsuario = documentSnapshot.getString("name");
                            nombredeadmin.setText("Bienvenido "+nombreUsuario);
                        }
                    });
        }
    }



}
