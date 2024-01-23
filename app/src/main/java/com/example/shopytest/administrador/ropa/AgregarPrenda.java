package com.example.shopytest.administrador.ropa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopytest.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Actividad que permite a un administrador agregar una nueva prenda al catálogo de ropa.
 * Proporciona campos para ingresar información sobre la prenda, como nombre, precio, descripción, etc.
 *
 * Esta clase utiliza la base de datos Firestore de Firebase para almacenar la información de la prenda,
 * así como Firebase Storage para gestionar las imágenes asociadas a cada prenda.
 *
 * @version 1.0
 * @autor Pablo Pereira
 */
public class AgregarPrenda extends AppCompatActivity {

    /**
     * CheckBox que indica si la prenda tiene una rebaja aplicada.
     */
    private CheckBox checkBoxRebaja;

    /**
     * Campo de entrada de texto para el nombre de la prenda.
     */
    private EditText editTextNombre;

    /**
     * Campo de entrada de texto para el precio de la prenda.
     */
    private EditText editTextPrecio;

    /**
     * Campo de entrada de texto para la cantidad de rebaja aplicada a la prenda (opcional).
     */
    private EditText editTextRebaja;

    /**
     * Campo de entrada de texto para la descripción de la prenda.
     */
    private EditText editTextDescripcion;

    /**
     * Spinner que permite seleccionar el género al que pertenece la prenda.
     */
    private Spinner spinnerGenero;

    /**
     * ImageView utilizada para mostrar la imagen de la prenda.
     */
    private ImageView imagen;

    /**
     * Constante que representa el código de solicitud para la acción de seleccionar una imagen.
     */
    private static final int SELECCIONAR_IMAGEN_REQUEST = 1;

    /**
     * URI que almacena la ubicación de la imagen seleccionada.
     */
    private Uri imagenUri;

    /**
     * Instancia de la base de datos Firestore utilizada para interactuar con la colección de prendas.
     */
    private FirebaseFirestore db;


    /**
     * Método llamado cuando la actividad se crea por primera vez. Aquí se inicializan los componentes de la interfaz de usuario.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_prenda);

        checkBoxRebaja = findViewById(R.id.checkBoxRebaja);
        editTextRebaja = findViewById(R.id.editTextRebaja);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        imagen = findViewById(R.id.prenda);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        TextInputEditText editTextRebaja = findViewById(R.id.editTextRebaja);

        final EditText editTextPrecio = findViewById(R.id.editTextPrecio);
        Button btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        Button btnCargarImagen = findViewById(R.id.btnCargarImagen);
        imagenUri = null;
        db = FirebaseFirestore.getInstance();

        checkBoxRebaja.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Si el checkbox está marcado, hacer visible el EditText de rebaja, si no, hacerlo invisible
            if (checkBoxRebaja.isChecked()) {
                textInputLayout.setVisibility(View.VISIBLE);
                editTextRebaja.setVisibility(View.VISIBLE);
            } else {
                textInputLayout.setVisibility(View.GONE);
                editTextRebaja.setVisibility(View.GONE);
            }
        });

        btnGuardarCambios.setOnClickListener(view -> {
            // Obtener el valor del EditText de rebaja solo si el checkbox está marcado
            String rebajaText = checkBoxRebaja.isChecked() ? editTextRebaja.getText().toString().trim() : "";

            try {
                // Validar que el valor de rebaja sea un número entero entre 1 y 100
                if (!rebajaText.isEmpty()) {
                    int rebaja = Integer.parseInt(rebajaText);
                    if (rebaja < 1 || rebaja > 100) {
                        editTextRebaja.setError("La rebaja debe estar entre 1 y 100");
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                Log.e("AgregarPrenda", "Error al convertir rebaja a entero: " + e.getMessage());
                editTextRebaja.setError("Ingrese un número válido para la rebaja");
                return;
            }

            // Validar que el precio sea un número y no sea inferior a 0
            String precioText = editTextPrecio.getText().toString().trim();
            if (precioText.isEmpty()) {
                Log.e("AgregarPrenda", "Precio está vacío");
                editTextPrecio.setError("Ingrese un precio");
                return;
            }

            try {
                double precio = Double.parseDouble(precioText);
                if (precio < 0) {
                    Log.e("AgregarPrenda", "Precio es inferior a 0");
                    editTextPrecio.setError("El precio no puede ser inferior a 0");
                    return;
                }
            } catch (NumberFormatException e) {
                Log.e("AgregarPrenda", "Error al convertir precio a double: " + e.getMessage());
                editTextPrecio.setError("Ingrese un número válido para el precio");
                return;
            }

            // Validar que se haya seleccionado una imagen
            if (imagenUri == null) {
                mostrarToast("Seleccione una imagen antes de guardar cambios");
                return;
            }

            // Validar que el nombre y la descripción no estén vacíos
            String nombre = editTextNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                editTextNombre.setError("Ingrese un nombre para la prenda");
                return;
            }

            String descripcion = editTextDescripcion.getText().toString().trim();
            if (descripcion.isEmpty()) {
                editTextDescripcion.setError("Ingrese una descripción para la prenda");
                return;
            }

            guardarPrendaEnFirestore();
        });

        btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });
    }

    /**
     * Método que guarda la información de la prenda en la base de datos Firestore.
     * Realiza validaciones antes de realizar el guardado.
     */
    private void guardarPrendaEnFirestore() {
        String nombre = editTextNombre.getText().toString().trim();
        String precioText = editTextPrecio.getText().toString().trim();
        String rebajaText = checkBoxRebaja.isChecked() ? editTextRebaja.getText().toString().trim() : "";
        String generoSeleccionado = spinnerGenero.getSelectedItem().toString();
        String descripcion = editTextDescripcion.getText().toString().trim();

        try {
            double precio = Double.parseDouble(precioText);
            int rebaja = checkBoxRebaja.isChecked() ? Integer.parseInt(rebajaText) : 0;

            // Crear un mapa con los datos de la prenda
            Map<String, Object> prenda = new HashMap<>();
            prenda.put("Nombre", nombre);
            prenda.put("Precio", precio);
            prenda.put("Descripcion", descripcion);
            prenda.put("Rebaja", rebaja);
            prenda.put("Genero", generoSeleccionado);

            // Añadir la prenda a Firestore
            db.collection("PrendasRopa")
                    .add(prenda)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("AgregarPrenda", "Prenda agregada con ID: " + documentReference.getId());

                        // Actualizar el documento con la URL de la imagen si existe
                        if (imagenUri != null) {
                            subirImagenAFirebaseStorage(documentReference.getId());
                        }

                        mostrarToast("La prenda se creó correctamente");
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AgregarPrenda", "Error al agregar la prenda", e);
                        // Manejar el error según sea necesario
                    });
        } catch (NumberFormatException e) {
            Log.e("AgregarPrenda", "Error al convertir precio o rebaja", e);
            mostrarToast("Error al agregar la prenda");
        }
    }


    /**
     * Sube la imagen seleccionada a Firebase Storage y actualiza la URL de la imagen en el documento de la prenda en Firestore.
     *
     * @param prendaId Identificador único de la prenda en Firestore.
     */
    private void subirImagenAFirebaseStorage(String prendaId) {
        // Crear una referencia única para la imagen en Firebase Storage
        String nombreImagen = "imagen_" + prendaId + ".jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(nombreImagen);

        // Subir la imagen a Firebase Storage
        storageReference.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de la imagen después de subirla
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Guardar la URL de la imagen en el documento de la prenda
                        Map<String, Object> actualizacion = new HashMap<>();
                        actualizacion.put("UrlImagen", uri.toString());

                        db.collection("PrendasRopa")
                                .document(prendaId)
                                .update(actualizacion)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("AgregarPrenda", "URL de imagen guardada en Firestore");
                                    // Aquí puedes realizar acciones adicionales después de guardar la URL
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("AgregarPrenda", "Error al guardar la URL de imagen en Firestore", e);
                                    // Manejar el error según sea necesario
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("AgregarPrenda", "Error al subir la imagen a Firebase Storage", e);
                    // Manejar el error según sea necesario
                });
    }

    /**
     * Método llamado cuando se completa la acción de seleccionar una imagen desde la galería.
     *
     * @param requestCode Código de solicitud de la acción.
     * @param resultCode  Código de resultado de la acción.
     * @param data        Datos adicionales (en este caso, la URI de la imagen seleccionada).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECCIONAR_IMAGEN_REQUEST && resultCode == Activity.RESULT_OK) {
            // Obtener la URI de la imagen seleccionada
            if (data != null && data.getData() != null) {
                // Establecer la imagen en el ImageView
                imagen.setImageURI(data.getData());
                imagenUri = data.getData();
            } else {
                Log.e("AgregarPrenda", "URI de imagen seleccionada nulo");
            }
        }
    }

    /**
     * Abre la galería para que el usuario seleccione una imagen.
     */
    private void abrirGaleria() {
        // Crear un Intent para seleccionar una imagen de la galería
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_IMAGEN_REQUEST);
    }

    /**
     * Muestra un mensaje de tostada en la interfaz.
     *
     * @param mensaje Mensaje a mostrar.
     */
    private void mostrarToast(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

}
