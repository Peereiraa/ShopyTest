package com.example.shopytest.administrador.incidencias;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopytest.R;
import com.example.shopytest.aplicacion.Perfil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class IncidenciasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private IncidenciaAdapter incidenciaAdapter;
    private List<Incidencia> listaIncidencias;

    private FirebaseFirestore db;
    private CollectionReference incidenciasRef;

    private ImageView volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias);

        recyclerView = findViewById(R.id.recycler_incidencias);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaIncidencias = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        volver = findViewById(R.id.volver);
        incidenciasRef = db.collection("Incidencias");

        cargarIncidencias();

        incidenciaAdapter = new IncidenciaAdapter(this, listaIncidencias);
        recyclerView.setAdapter(incidenciaAdapter);

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncidenciasActivity.this, Perfil.class);
                startActivity(intent);
            }
        });
    }

    private void cargarIncidencias() {
        incidenciasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String idIncidencia = document.getId(); // Obtener el ID del documento
                    String idUsuario = document.getString("idUsuario");
                    String nombreUsuario = document.getString("nombreUsuario");
                    String photoUrlUsuario = document.getString("photoUrlUsuario");
                    String motivo = document.getString("motivoIncidencia");
                    String seccion = document.getString("tipoIncidencia");

                    Incidencia incidencia = new Incidencia(idIncidencia, idUsuario, nombreUsuario, photoUrlUsuario, motivo, seccion);
                    listaIncidencias.add(incidencia);
                }
                incidenciaAdapter.notifyDataSetChanged();
            }
        });
    }
}
