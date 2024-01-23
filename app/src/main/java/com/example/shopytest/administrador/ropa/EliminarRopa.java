package com.example.shopytest.administrador.ropa;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopytest.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EliminarRopa extends AppCompatActivity {

    private RecyclerView recyclerViewHombre;
    private RecyclerView recyclerViewMujer;
    private RecyclerView recyclerViewNinos;

    private PrendaAdminAdapter adapterHombre;
    private PrendaAdminAdapter adapterMujer;
    private PrendaAdminAdapter adapterNinos;

    private CheckBox checkHombre;
    private CheckBox checkMujer;
    private CheckBox checkNinos;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_ropa);



        SearchView searchView = findViewById(R.id.searchViewUsuarios);

        // Inicializar RecyclerViews
        recyclerViewHombre = findViewById(R.id.recyclerViewHombre);
        recyclerViewMujer = findViewById(R.id.recyclerViewMujer);
        recyclerViewNinos = findViewById(R.id.recyclerViewNinos);

        // Inicializar adaptadores
        adapterHombre = new PrendaAdminAdapter();
        adapterMujer = new PrendaAdminAdapter();
        adapterNinos = new PrendaAdminAdapter();
        db = FirebaseFirestore.getInstance();

        checkHombre = findViewById(R.id.checkhombre);
        checkMujer = findViewById(R.id.checkmujer);
        checkNinos = findViewById(R.id.checkniño);

        // Configurar LayoutManagers
        recyclerViewHombre.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMujer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNinos.setLayoutManager(new LinearLayoutManager(this));

        // Establecer adaptadores
        recyclerViewHombre.setAdapter(adapterHombre);
        recyclerViewMujer.setAdapter(adapterMujer);
        recyclerViewNinos.setAdapter(adapterNinos);


        obtenerPrendasHombre(prendasHombre -> {
            // Ahora puedes trabajar con la lista de prendasHombre
            adapterHombre.setPrendas(prendasHombre);
        });

        obtenerPrendasMujer(prendasMujer ->{
            adapterMujer.setPrendas(prendasMujer);
        });

        checkHombre.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ViewGroup.LayoutParams layoutParams = recyclerViewHombre.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                recyclerViewHombre.setLayoutParams(layoutParams);
                recyclerViewHombre.setVisibility(RecyclerView.VISIBLE);
                recyclerViewMujer.setVisibility(RecyclerView.INVISIBLE);
                recyclerViewNinos.setVisibility(RecyclerView.INVISIBLE);
                checkMujer.setChecked(false);
                checkNinos.setChecked(false);

            }else{
                recyclerViewHombre.setVisibility(RecyclerView.INVISIBLE);
                ViewGroup.LayoutParams params = recyclerViewHombre.getLayoutParams();
                params.height = 0;
                recyclerViewHombre.setLayoutParams(params);
            }
        });

        checkMujer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si Mujer está seleccionado, deseleccionar los demás
                ViewGroup.LayoutParams layoutParams = recyclerViewMujer.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                recyclerViewMujer.setLayoutParams(layoutParams);
                recyclerViewHombre.setVisibility(RecyclerView.INVISIBLE);
                recyclerViewMujer.setVisibility(RecyclerView.VISIBLE);
                recyclerViewNinos.setVisibility(RecyclerView.INVISIBLE);
                checkHombre.setChecked(false);
                checkNinos.setChecked(false);
                // Lógica adicional según sea necesario...
            }else{
                recyclerViewMujer.setVisibility(RecyclerView.INVISIBLE);
                ViewGroup.LayoutParams params = recyclerViewMujer.getLayoutParams();
                params.height = 0;
                recyclerViewMujer.setLayoutParams(params);
            }
        });

        checkNinos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ViewGroup.LayoutParams layoutParams = recyclerViewNinos.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                recyclerViewNinos.setLayoutParams(layoutParams);
                recyclerViewHombre.setVisibility(RecyclerView.INVISIBLE);
                recyclerViewMujer.setVisibility(RecyclerView.INVISIBLE);
                recyclerViewNinos.setVisibility(RecyclerView.VISIBLE);
                checkHombre.setChecked(false);
                checkMujer.setChecked(false);
                // Lógica adicional según sea necesario...
            }else{
                recyclerViewNinos.setVisibility(RecyclerView.INVISIBLE);
                ViewGroup.LayoutParams params = recyclerViewNinos.getLayoutParams();
                params.height = 0;
                recyclerViewNinos.setLayoutParams(params);
            }
        });






        obtenerPrendasHombre(prendasHombre -> {
            // Ahora puedes trabajar con la lista de prendasHombre
            adapterHombre.setPrendas(prendasHombre);
        });

        obtenerPrendasMujer(prendasMujer ->{
            adapterMujer.setPrendas(prendasMujer);
        });

        obtenerPrendasNinos(prendasNinos ->{
            adapterNinos.setPrendas(prendasNinos);
        });




    }



    private void obtenerPrendasHombre(OnPrendasLoadedListener onPrendasLoadedListener) {
        // Obtener la referencia de la colección "PrendasRopa" en Firestore
        CollectionReference prendasRef = db.collection("PrendasRopa");

        // Aplicar filtro para obtener solo las prendas con género "Hombre"
        prendasRef.whereEqualTo("Genero", "Hombre")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Obtener la lista de DocumentSnapshot
                    List<DocumentSnapshot> prendasHombre = queryDocumentSnapshots.getDocuments();

                    // Llamar al listener con la lista de prendas de hombre
                    onPrendasLoadedListener.onPrendasLoaded(prendasHombre);

                    // Actualizar el adaptador con la lista de prendas de hombre
                    adapterHombre.setDocumentos(prendasHombre);
                })
                .addOnFailureListener(e -> {
                    // Manejar errores si la carga falla
                    Toast.makeText(EliminarRopa.this, "Error al cargar prendas de hombre", Toast.LENGTH_SHORT).show();
                    Log.e("EliminarRopa", "Error al cargar prendas de hombre", e);
                });
    }

    private void obtenerPrendasMujer(OnPrendasLoadedListener onPrendasLoadedListener){
        CollectionReference prendasRef = db.collection("PrendasRopa");

        // Aplicar filtro para obtener solo las prendas con género "Mujer"
        prendasRef.whereEqualTo("Genero", "Mujer")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Obtener la lista de DocumentSnapshot
                    List<DocumentSnapshot> prendasMujer = queryDocumentSnapshots.getDocuments();

                    // Llamar al listener con la lista de prendas de mujer
                    onPrendasLoadedListener.onPrendasLoaded(prendasMujer);

                    // Actualizar el adaptador con la lista de prendas de mujer
                    adapterMujer.setDocumentos(prendasMujer);
                })
                .addOnFailureListener(e -> {
                    // Manejar errores si la carga falla
                    Toast.makeText(EliminarRopa.this, "Error al cargar prendas de mujer", Toast.LENGTH_SHORT).show();
                    Log.e("EliminarRopa", "Error al cargar prendas de mujer", e);
                });


    }

    private void obtenerPrendasNinos(OnPrendasLoadedListener onPrendasLoadedListener){
        CollectionReference prendasRef = db.collection("PrendasRopa");

        // Aplicar filtro para obtener solo las prendas con género "Mujer"
        prendasRef.whereEqualTo("Genero", "Niño")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Obtener la lista de DocumentSnapshot
                    List<DocumentSnapshot> prendasNinos = queryDocumentSnapshots.getDocuments();

                    // Llamar al listener con la lista de prendas de mujer
                    onPrendasLoadedListener.onPrendasLoaded(prendasNinos);

                    // Actualizar el adaptador con la lista de prendas de mujer
                    adapterNinos.setDocumentos(prendasNinos);
                })
                .addOnFailureListener(e -> {
                    // Manejar errores si la carga falla
                    Toast.makeText(EliminarRopa.this, "Error al cargar prendas de mujer", Toast.LENGTH_SHORT).show();
                    Log.e("EliminarRopa", "Error al cargar prendas de mujer", e);
                });


    }







    private List<PrendaRopa> obtenerPrendasMujer() {
        // Obtener prendas de la categoría "Mujer" de tu base de datos
        // Devuelve una lista vacía como simulación.
        return new ArrayList<>();
    }

    private List<PrendaRopa> obtenerPrendasNinos() {
        // Obtener prendas de la categoría "Niños" de tu base de datos
        // Devuelve una lista vacía como simulación.
        return new ArrayList<>();
    }

    public interface OnPrendasLoadedListener {
        void onPrendasLoaded(List<DocumentSnapshot> prendas);
    }


}


