package com.example.shopytest.aplicacion;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shopytest.aplicacion.identificacion.Login;
import com.example.shopytest.R;
import com.example.shopytest.adapters.PrendaAdapter;
import com.example.shopytest.adapters.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * La clase {@code CategoriasMujer} representa la actividad que muestra las categorías de prendas
 * para mujeres y permite navegar por las diferentes secciones.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class CategoriasMujer extends AppCompatActivity {

    /** TextView para la categoría de prendas para hombres */
    private TextView hombre;

    /** TextView para la categoría de prendas para niños */
    private TextView ninos;

    /** ImageView para el icono de inicio */
    private ImageView iconoInicio;

    /** ImageView para el icono del carrito */
    private ImageView iconoCarrito;

    /** ImageView para el icono del perfil en la barra */
    private ImageView iconoPerfil;

    /** ImageView para la imagen de categorías */
    private ImageView categoriasImageView;

    /** ImageView para la foto de perfil */
    private ImageView fotoperfil;

    /** ImageView para el logo de inicio */
    private ImageView logoinicio;

    /** RecyclerView para mostrar las prendas */
    private RecyclerView recyclerView;

    /** Adaptador para el RecyclerView de prendas */
    private PrendaAdapter prendaAdapter;

    /** Instancia de Firebase Firestore */
    private FirebaseFirestore db;

    /** Instancia de FirebaseAuth */
    private FirebaseAuth mAuth;

    /** Número de columnas en el RecyclerView */
    private int columnCount = 2;


    /**
     * Método llamado cuando la actividad se está creando.
     *
     * @param savedInstanceState Datos que pueden haberse guardado del estado anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_mujer);


        // Inicialización de componentes y configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        // Define el número de columnas que deseas
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(layoutManager);

        prendaAdapter = new PrendaAdapter(new ArrayList<>());
        recyclerView.setAdapter(prendaAdapter);


        categoriasImageView = findViewById(R.id.categorias);
        hombre = findViewById(R.id.hombre);
        ninos = findViewById(R.id.ninos);
        logoinicio = findViewById(R.id.logoshopy);
        fotoperfil = findViewById(R.id.logo);

        iconoInicio = findViewById(R.id.icono_home);
        iconoCarrito = findViewById(R.id.icono_carrito);
        iconoPerfil = findViewById(R.id.icono_perfilbarra);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        cargarImagenPerfil();
        obtenerPrendasMujer();

        // Definición de acciones para clics en iconos y otros elementos
        iconoInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriasMujer.this, Inicio.class);
                startActivity(intent);
            }
        });

        iconoCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriasMujer.this, Carrito.class);
                startActivity(intent);
            }
        });

        if (currentUser != null) {
            // Si el usuario está autenticado, permite acceder al perfil
            iconoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoriasMujer.this, Perfil.class);
                    startActivity(intent);
                }
            });
        } else {
            // Si el usuario no está autenticado, muestra un mensaje o maneja el flujo de otra manera
            iconoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Por ejemplo, puedes mostrar un mensaje o llevar al usuario a la pantalla de inicio de sesión
                    Toast.makeText(CategoriasMujer.this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CategoriasMujer.this, Login.class));
                }
            });
        }


        logoinicio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(CategoriasMujer.this, Inicio.class);
                startActivity(intent);
            }
        });

        hombre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(CategoriasMujer.this, Categorias.class);
                startActivity(intent);
            }
        });

        ninos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(CategoriasMujer.this, CategoriasNinos.class);
                startActivity(intent);
            }
        });


        if (currentUser != null) {
            // Si el usuario está autenticado, permite acceder al perfil
            fotoperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CategoriasMujer.this, Perfil.class));
                }
            });
        } else {
            // Si el usuario no está autenticado, muestra un mensaje o maneja el flujo de otra manera
            fotoperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Por ejemplo, puedes mostrar un mensaje o llevar al usuario a la pantalla de inicio de sesión
                    Toast.makeText(CategoriasMujer.this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CategoriasMujer.this, Login.class));
                }
            });
        }

        categoriasImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(CategoriasMujer.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_categorias, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_inicio) {
                            // Navegar a la actividad o fragmento de "Inicio"
                            Intent intent = new Intent(CategoriasMujer.this, Inicio.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.menu_categorias) {
                            showAlert("Ya estas en la pantalla categorias");
                            return true;
                        } else if (itemId == R.id.menu_carrito) {
                            // Navegar a la actividad o fragmento de "Carrito"
                            Intent intent = new Intent(CategoriasMujer.this, Carrito.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.menu_perfil) {
                            if (currentUser != null) {
                                // Navegar a la actividad o fragmento de "Perfil"
                                Intent intent = new Intent(CategoriasMujer.this, Perfil.class);
                                startActivity(intent);
                                return true;
                            }else{
                                Toast.makeText(CategoriasMujer.this, "Inicia sesión para acceder a esta función", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CategoriasMujer.this, Login.class));
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
                        Intent intent = new Intent(CategoriasMujer.this, DetallePrendaActivity.class);
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
     * Obtiene las prendas de la categoría "Mujer" desde Firebase Firestore
     */
    private void obtenerPrendasMujer() {

        db.collection("PrendasRopa")
                .whereEqualTo("Genero", "Mujer")
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
     * Muestra un cuadro de diálogo de alerta con el mensaje especificado.
     *
     * @param message Mensaje a mostrar en el cuadro de diálogo.
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoriasMujer.this);
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
     * Carga la imagen de perfil del usuario actual desde Firebase Storage.
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
                Intent intent = new Intent(CategoriasMujer.this, Perfil.class);
                startActivity(intent);
            });
        }
    }


}