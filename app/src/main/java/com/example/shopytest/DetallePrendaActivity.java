package com.example.shopytest;


import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * La clase {@code DetallePrendaActivity} representa la actividad que muestra detalles de una prenda
 * y permite agregarla al carrito de compras.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class DetallePrendaActivity extends AppCompatActivity {

    /** ImageView para mostrar la imagen de la prenda */
    private ImageView prendaapasar;

    /** TextView para mostrar el título de la prenda */
    private TextView tituloPrenda;

    /** TextView para mostrar el precio de la prenda */
    private TextView precioPrenda;

    /** TextView para mostrar la descripción de la prenda */
    private TextView descripcionPrenda;

    /** Vista para el botón que permite agregar la prenda al carrito */
    private View botonAgregarCarrito;


    /**
     * Método llamado cuando la actividad se está creando.
     *
     * @param savedInstanceState Datos que pueden haberse guardado del estado anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_prenda);

        // Inicialización de vistas
        prendaapasar = findViewById(R.id.imagenPrenda);
        tituloPrenda = findViewById(R.id.tituloPrenda);
        precioPrenda = findViewById(R.id.precioPrenda);
        botonAgregarCarrito = findViewById(R.id.agregarcarrito);
        descripcionPrenda = findViewById(R.id.descripcionPrenda);

        // Obtener datos de la prenda desde el intent
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String nombre = getIntent().getStringExtra("nombre");
        String precio = getIntent().getStringExtra("precio");
        String idPrenda = getIntent().getStringExtra("id");
        String descripcion = getIntent().getStringExtra("descripcion");


        // Cargar la imagen de la prenda utilizando Glide
        Glide.with(this)
                .load(imageUrl)
                .into(prendaapasar);

        // Establecer los datos en los TextViews
        tituloPrenda.setText(nombre);
        precioPrenda.setText(precio+" €");
        descripcionPrenda.setText(descripcion);

        // Configurar el botón para agregar la prenda al carrito
        botonAgregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    agregarPrendaAlCarrito(idPrenda);
                } else {
                    // Si el usuario no está autenticado, muestra un mensaje o redirige al inicio de sesión
                    Toast.makeText(DetallePrendaActivity.this, "Debes iniciar sesión para agregar al carrito", Toast.LENGTH_SHORT).show();

                }
            }
        });



    }


    /**
     * Agrega la prenda al carrito de compras del usuario actual.
     *
     * @param idPrenda Identificador único de la prenda.
     */
    private void agregarPrendaAlCarrito(String idPrenda) {
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String nombre = getIntent().getStringExtra("nombre");
        String precio = getIntent().getStringExtra("precio");

        // Obtén la instancia de Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Referencia a la colección 'prendasCarrito' del usuario actual
            CollectionReference prendasCarritoRef = db.collection("usuarios")
                    .document(currentUser.getUid())
                    .collection("prendasCarrito");

            // Consulta para verificar si la prenda ya está en el carrito
            prendasCarritoRef.document(idPrenda)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // La prenda ya está en el carrito, actualiza la cantidad o realiza cualquier otra acción necesaria
                            actualizarPrendaExistente(idPrenda, prendasCarritoRef);
                        } else {
                            // La prenda no está en el carrito, agrégala
                            agregarNuevaPrenda(prendasCarritoRef, imageUrl, nombre, precio, idPrenda);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error según sea necesario
                        Toast.makeText(DetallePrendaActivity.this, "Error al verificar el carrito", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Agrega una nueva prenda al carrito de compras del usuario actual.
     *
     * @param prendasCarritoRef Referencia a la colección 'prendasCarrito' en Firestore.
     * @param imageUrl          URL de la imagen de la prenda.
     * @param nombre            Nombre de la prenda.
     * @param precio            Precio de la prenda.
     * @param idPrenda          Identificador único de la prenda.
     */
    private void agregarNuevaPrenda(CollectionReference prendasCarritoRef, String imageUrl, String nombre, String precio, String idPrenda) {
        // Crea un mapa con los datos de la prenda
        Map<String, Object> prenda = new HashMap<>();
        prenda.put("imagen", imageUrl);
        prenda.put("nombre", nombre);
        prenda.put("precio", precio);

        // Agrega la prenda a la colección 'prendasCarrito' en Firestore
        prendasCarritoRef.document(idPrenda)
                .set(prenda)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DetallePrendaActivity.this, "Prenda agregada al carrito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetallePrendaActivity.this, "Error al agregar la prenda al carrito", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Actualiza la cantidad de una prenda existente en el carrito.
     *
     * @param idPrenda          Identificador único de la prenda.
     * @param prendasCarritoRef Referencia a la colección 'prendasCarrito' en Firestore.
     */
    private void actualizarPrendaExistente(String idPrenda, CollectionReference prendasCarritoRef) {
        // Realiza las actualizaciones necesarias, por ejemplo, aumentar la cantidad
        prendasCarritoRef.document(idPrenda)
                .update("cantidad", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DetallePrendaActivity.this, "Prenda actualizada en el carrito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetallePrendaActivity.this, "Error al actualizar la prenda en el carrito", Toast.LENGTH_SHORT).show();
                });
    }


}
