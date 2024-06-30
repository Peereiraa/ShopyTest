package com.example.shopytest.aplicacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopytest.adapters.CarritoAdapter;
import com.example.shopytest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase {@code Carrito} representa la actividad que muestra los artículos en el carrito de compras
 * del usuario actual y calcula el precio total.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class Carrito extends AppCompatActivity {

    /**
     * Componente de interfaz de usuario que muestra el precio total del carrito.
     */
    private TextView preciototal;

    /**
     * Componente de interfaz de usuario RecyclerView para mostrar los artículos en el carrito.
     */
    private RecyclerView recyclerView;

    /**
     * Adaptador para el RecyclerView que muestra los artículos en el carrito.
     */
    private CarritoAdapter carritoAdapter;

    private ImageView icono_categorias;

    /**
     * Método llamado cuando la actividad es creada. Aquí se inicializan los componentes de la interfaz
     * y se obtienen los artículos del carrito de la base de datos.
     *
     * @param savedInstanceState Datos previamente guardados del estado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        preciototal = findViewById(R.id.preciototal);
        recyclerView = findViewById(R.id.recyclerViewCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        icono_categorias = findViewById(R.id.icono_categorias);

        carritoAdapter = new CarritoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(carritoAdapter);

        // Obtener la referencia a la colección 'prendasCarrito' del usuario actual
        CollectionReference prendasCarritoRef = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("prendasCarrito");

        prendasCarritoRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentos = queryDocumentSnapshots.getDocuments();
                        carritoAdapter.setDocumentos(documentos);
                        carritoAdapter.calcularPrecioTotal();
                        carritoAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar los errores si la consulta falla
                    }
                });
        icono_categorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrito.this, Categorias.class);
                startActivity(intent);
            }
        });

        View botonPagar = findViewById(R.id.botonpagar);
        botonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carritoAdapter.getItemCount() > 0) {
                    // Iniciar la actividad de Pagar
                    Intent intent = new Intent(Carrito.this, Pagar.class);
                    startActivity(intent);
                } else {
                    // Mostrar mensaje
                    Toast.makeText(Carrito.this, "Debes agregar un artículo a la tienda", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Establece el precio total en el componente de interfaz de usuario.
     *
     * @param nuevoPrecioTotal El nuevo precio total del carrito.
     */
    public void setPrecioTotal(double nuevoPrecioTotal) {
        preciototal.setText("Total: " + nuevoPrecioTotal + " €");
    }
}