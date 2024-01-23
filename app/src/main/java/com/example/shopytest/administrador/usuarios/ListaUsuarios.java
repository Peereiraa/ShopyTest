package com.example.shopytest.administrador.usuarios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.shopytest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuarios extends AppCompatActivity {

    private RecyclerView recyclerViewUsuarios;
    private ListaUsuariosAdapter adapter;
    private FirebaseFirestore db;
    private SearchView searchView;
    FloatingActionButton botonaccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        searchView = findViewById(R.id.searchViewUsuarios);
        adapter = new ListaUsuariosAdapter(new ArrayList<>());
         botonaccion = findViewById(R.id.botonaccion);

        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsuarios.setAdapter(adapter);

        // Inicializa FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Cargar usuarios desde Firestore
        cargarUsuariosDesdeFirestore();
        setupSearchView();

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
                            Toast.makeText(ListaUsuarios.this, "Opción 1 seleccionada", Toast.LENGTH_SHORT).show();
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
