package com.example.shopytest.administrador.usuarios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.shopytest.R;
import com.example.shopytest.administrador.MenuPantallaAdminActivity;
import com.example.shopytest.administrador.usuarios.gestion.CrearUsuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


/**
 * Clase que representa la actividad para mostrar la lista de usuarios en el sistema de administración.
 * Permite la visualización, búsqueda y realización de acciones específicas en los usuarios.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class ListaUsuarios extends AppCompatActivity {

    /**
     * RecyclerView utilizado para mostrar la lista de usuarios.
     */
    private RecyclerView recyclerViewUsuarios;

    /**
     * Adaptador para el RecyclerView que maneja la lista de usuarios.
     */
    private ListaUsuariosAdapter adapter;

    /**
     * Objeto para interactuar con la base de datos Firestore.
     */
    private FirebaseFirestore db;

    /**
     * Vista para realizar búsquedas dentro de la lista de usuarios.
     */
    private SearchView searchView;

    /**
     * Botón de acción flotante para mostrar opciones adicionales.
     */
    FloatingActionButton botonaccion;

    private ImageView volver;

    /**
     * Método llamado cuando se crea la actividad.
     *
     * @param savedInstanceState Objeto que contiene datos anteriores acerca de la actividad (puede ser nulo).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        // Inicializa el RecyclerView, el adaptador y otros componentes de la interfaz de usuario
        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        searchView = findViewById(R.id.searchViewUsuarios);
        adapter = new ListaUsuariosAdapter(new ArrayList<>());
        botonaccion = findViewById(R.id.botonaccion);
        volver = findViewById(R.id.volver);

        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsuarios.setAdapter(adapter);

        // Inicializa FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Cargar usuarios desde Firestore
        cargarUsuariosDesdeFirestore();
        setupSearchView();

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaUsuarios.this, MenuPantallaAdminActivity.class);
                startActivity(intent);
            }
        });

        botonaccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ListaUsuarios.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_opciones, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.opcion1) {
                            // Acción para la opción 1
                            Intent intent = new Intent(ListaUsuarios.this, CrearUsuario.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.opcion2) {
                            // Acción para la opción 2
                            Toast.makeText(ListaUsuarios.this, "Opción 2 seleccionada", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    /**
     * Método para cargar los usuarios desde Firestore y actualizar el adaptador con la lista de usuarios.
     */
    private void cargarUsuariosDesdeFirestore() {
        // Obtener la referencia de la colección "user" en Firestore
        CollectionReference usuariosRef = db.collection("user");

        // Consultar todos los documentos en la colección
        usuariosRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Obtener la lista de DocumentSnapshot
                    List<DocumentSnapshot> usuarios = queryDocumentSnapshots.getDocuments();

                    // Actualizar el adaptador con la lista de usuarios
                    adapter.setDocumentos(usuarios);
                })
                .addOnFailureListener(e -> {
                    // Manejar errores si la carga falla
                    Toast.makeText(ListaUsuarios.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                    Log.e("ListaUsuarios", "Error al cargar usuarios", e);
                });
    }

    /**
     * Método para configurar el SearchView y gestionar la búsqueda en la lista de usuarios.
     */
    private void setupSearchView() {
        // Configurar el listener para el SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Se llama cuando se envía la búsqueda (puedes ignorarlo si no necesitas manejarlo aquí)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Se llama cuando cambia el texto de búsqueda
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
