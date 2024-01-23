package com.example.shopytest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shopytest.prendas.PrendaAdapter;
import com.example.shopytest.prendas.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * La clase {@code CategoriasNinos} representa la actividad que muestra las categorías de prendas
 * para niños y permite navegar por las diferentes secciones.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class CategoriasNinos extends AppCompatActivity {

    /**
     * Representa el componente de interfaz de usuario TextView para la categoría "Hombre".
     */
    private TextView hombre;

    /**
     * Representa el componente de interfaz de usuario TextView para la categoría "Mujer".
     */
    private TextView mujer;

    /**
     * Representa el icono de inicio en la barra de navegación.
     */
    private ImageView iconoInicio;

    /**
     * Representa el icono del carrito de compras en la barra de navegación.
     */
    private ImageView iconoCarrito;

    /**
     * Representa el icono de perfil en la barra de navegación.
     */
    private ImageView iconoPerfil;

    /**
     * Representa la imagen de perfil del usuario.
     */
    private ImageView fotoperfil;

    /**
     * Representa la imagen de las categorías.
     */
    private ImageView categoriasImageView;

    /**
     * Representa el logo de inicio.
     */
    private ImageView logoinicio;

    /**
     * Representa el componente de interfaz de usuario RecyclerView para mostrar las prendas.
     */
    private RecyclerView recyclerView;

    /**
     * Representa el adaptador para el RecyclerView que muestra las prendas.
     */
    private PrendaAdapter prendaAdapter;

    /**
     * Lista que contiene los documentos de las prendas de ropa.
     */
    private List<DocumentSnapshot> prendaList;

    /**
     * Instancia de FirebaseFirestore para interactuar con la base de datos Firestore.
     */
    private FirebaseFirestore db;

    /**
     * Número de columnas en el RecyclerView.
     */
    private int columnCount = 2;

    /**
     * Instancia de FirebaseAuth para gestionar la autenticación del usuario.
     */
    private FirebaseAuth mAuth;

    /**
     * Método llamado cuando la actividad es creada. Aquí se inicializan los componentes de la interfaz
     * y se configuran los listeners para los eventos de clic en diferentes elementos.
     *
     * @param savedInstanceState Datos previamente guardados del estado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_ninos);
        recyclerView = findViewById(R.id.recyclerView);
        // Define el número de columnas que deseas
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(layoutManager);

        prendaAdapter = new PrendaAdapter(new ArrayList<>());
        recyclerView.setAdapter(prendaAdapter);



        hombre = findViewById(R.id.hombre);
        mujer = findViewById(R.id.mujer);
        logoinicio = findViewById(R.id.logoshopy);
        fotoperfil = findViewById(R.id.logo);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        cargarImagenPerfil();

        categoriasImageView = findViewById(R.id.categorias);
        db = FirebaseFirestore.getInstance();
        iconoInicio = findViewById(R.id.icono_home);
        iconoCarrito = findViewById(R.id.icono_carrito);
        iconoPerfil = findViewById(R.id.icono_perfilbarra);
        obtenerPrendasNinos();

        iconoInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriasNinos.this, Inicio.class);
                startActivity(intent);
            }
        });

        iconoCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriasNinos.this, Carrito.class);
                startActivity(intent);
            }
        });

        if (currentUser != null) {
            // Si el usuario está autenticado, permite acceder al perfil
            iconoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoriasNinos.this, Perfil.class);
                    startActivity(intent);
                }
            });
        } else {
            // Si el usuario no está autenticado, muestra un mensaje o maneja el flujo de otra manera
            iconoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Por ejemplo, puedes mostrar un mensaje o llevar al usuario a la pantalla de inicio de sesión
                    Toast.makeText(CategoriasNinos.this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CategoriasNinos.this, Login.class));
                }
            });
        }

        if (currentUser != null) {
            // Si el usuario está autenticado, permite acceder al perfil
            fotoperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CategoriasNinos.this, Perfil.class));
                }
            });
        } else {
            // Si el usuario no está autenticado, muestra un mensaje o maneja el flujo de otra manera
            fotoperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Por ejemplo, puedes mostrar un mensaje o llevar al usuario a la pantalla de inicio de sesión
                    Toast.makeText(CategoriasNinos.this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CategoriasNinos.this, Login.class));
                }
            });
        }


        logoinicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriasNinos.this, Inicio.class);
                startActivity(intent);
            }
        });

        hombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriasNinos.this, Categorias.class);
                startActivity(intent);
            }
        });

        mujer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriasNinos.this, CategoriasMujer.class);
                startActivity(intent);
            }
        });

        categoriasImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(CategoriasNinos.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_categorias, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_inicio) {
                            // Navegar a la actividad o fragmento de "Inicio"
                            Intent intent = new Intent(CategoriasNinos.this, Inicio.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.menu_categorias) {
                            showAlert("Ya estas en la pantalla categorias");
                            return true;
                        } else if (itemId == R.id.menu_carrito) {
                            // Navegar a la actividad o fragmento de "Carrito"
                            Intent intent = new Intent(CategoriasNinos.this, Carrito.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.menu_perfil) {
                            if (currentUser != null) {
                                // Navegar a la actividad o fragmento de "Perfil"
                                Intent intent = new Intent(CategoriasNinos.this, Perfil.class);
                                startActivity(intent);
                                return true;
                            } else {
                                Toast.makeText(CategoriasNinos.this, "Inicia sesión para acceder a esta función", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CategoriasNinos.this, Login.class));
                            }
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Manejar el clic en el elemento del RecyclerView
                        DocumentSnapshot documento = prendaAdapter.getDocumentAtPosition(position);

                        // Obtener los datos necesarios (id, nombre, precio, etc.) del DocumentSnapshot
                        String idPrenda = documento.getId();
                        String nombrePrenda = documento.getString("Nombre");
                        Double precioPrenda = documento.getDouble("Precio");
                        String descripcionPrenda = documento.getString("Descripcion");
                        String imageUrlPrenda = documento.getString("UrlImagen");

                        String precioString = String.valueOf(precioPrenda);

                        // Crear un Intent para pasar a la actividad DetallePrendaRopa
                        Intent intent = new Intent(CategoriasNinos.this, DetallePrendaActivity.class);
                        intent.putExtra("id", idPrenda);
                        intent.putExtra("nombre", nombrePrenda);
                        intent.putExtra("precio", precioString);
                        intent.putExtra("descripcion", descripcionPrenda);
                        intent.putExtra("imageUrl", imageUrlPrenda);

                        // Iniciar la nueva actividad
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Manejar clic largo si es necesario
                    }
                })
        );


    }

    /**
     * Obtiene las prendas de ropa para niños de la base de datos y actualiza el adaptador del RecyclerView.
     */
    private void obtenerPrendasNinos() {
        db.collection("PrendasRopa")
                .whereEqualTo("Genero", "Niño")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    prendaAdapter.setDocumentos(queryDocumentSnapshots.getDocuments());
                    prendaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar el error según sea necesario
                });
    }

    /**
     * Muestra un cuadro de diálogo de alerta con el mensaje proporcionado.
     *
     * @param message Mensaje a mostrar en la alerta.
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoriasNinos.this);
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
     * Carga la imagen de perfil del usuario actual, ya sea desde Google o desde el almacenamiento.
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
                                .into(fotoperfil);
                    }
                } else {
                    // Si no es Google, carga la imagen desde el almacenamiento
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                            .child("fotousuario/" + currentUser.getUid() + "/imagenPerfil.jpg");

                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Aquí, 'uri' contiene la URL de descarga de la imagen
                        Glide.with(this)
                                .load(uri)
                                .into(fotoperfil);
                    }).addOnFailureListener(exception -> {
                        // Manejar el caso en el que no se pueda obtener la URL de la imagen
                    });
                }
            }

            // Si el usuario está autenticado, también puedes hacer que el logo sea clicle para ir al perfil
            fotoperfil.setOnClickListener(v -> {
                Intent intent = new Intent(CategoriasNinos.this, Perfil.class);
                startActivity(intent);
            });
        }
    }

}