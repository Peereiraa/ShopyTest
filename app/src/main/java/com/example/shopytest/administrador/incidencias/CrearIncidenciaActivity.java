package com.example.shopytest.administrador.incidencias;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopytest.R;
import com.example.shopytest.aplicacion.Perfil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearIncidenciaActivity extends AppCompatActivity {

    private TextInputLayout motivoIncidenciaLayout;
    private Spinner spinnerGenero;

    private ImageView volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_incidencia);

        motivoIncidenciaLayout = findViewById(R.id.motivoIncidencia);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        volver = findViewById(R.id.volver);

        // Configurar OnClickListener para el botón de guardar datos
        findViewById(R.id.guardardatos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarIncidencia();
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrearIncidenciaActivity.this, Perfil.class);
                startActivity(intent);
            }
        });
    }

    private void guardarIncidencia() {
        String motivoIncidencia = motivoIncidenciaLayout.getEditText().getText().toString().trim();
        String tipoIncidencia = spinnerGenero.getSelectedItem().toString();

        // Verificar si los campos están completos
        if (motivoIncidencia.isEmpty() || tipoIncidencia.equals("Seleccionar tipo de incidencia")) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener usuario actualmente logueado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // El usuario no está autenticado
            return;
        }

        // Obtener ID de usuario actualmente logueado
        String userId = currentUser.getUid();

        // Obtener nombre y photoUrl del usuario (si están disponibles)
        String nombreUsuario = currentUser.getDisplayName();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener la URL de la foto de perfil del usuario de Firestore
                            String photoUrlUsuario = documentSnapshot.getString("photoUrl");

                            // Crear una nueva instancia de FirebaseFirestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Crear un nuevo mapa para almacenar los datos de la incidencia
                            Map<String, Object> incidencia = new HashMap<>();
                            incidencia.put("idUsuario", userId);
                            incidencia.put("nombreUsuario", nombreUsuario);
                            incidencia.put("photoUrlUsuario", photoUrlUsuario);
                            incidencia.put("motivoIncidencia", motivoIncidencia);
                            incidencia.put("tipoIncidencia", tipoIncidencia);

                            // Añadir la incidencia a la colección "Incidencias" en Firestore
                            db.collection("Incidencias")
                                    .add(incidencia)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // La incidencia se guardó exitosamente
                                            Toast.makeText(CrearIncidenciaActivity.this, "¡Tu incidencia fue enviada con éxito!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CrearIncidenciaActivity.this, Perfil.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error al guardar la incidencia
                                            Toast.makeText(CrearIncidenciaActivity.this, "Error al enviar la incidencia", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // No se encontró el documento del usuario en Firestore
                            Toast.makeText(CrearIncidenciaActivity.this, "No se encontró la información del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al obtener la información del usuario de Firestore
                        Toast.makeText(CrearIncidenciaActivity.this, "Error al obtener la información del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
