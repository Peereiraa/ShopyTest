package com.example.shopytest.administrador.usuarios.gestion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopytest.R;
import com.example.shopytest.administrador.usuarios.ListaUsuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CrearUsuario extends AppCompatActivity {

    private TextInputLayout motivoIncidenciaLayout, nombreUsuarioLayout, escribirContraseña1Layout, escribirContraseña2Layout;
    private TextInputEditText motivoIncidenciaEditText, nombreUsuarioEditText, escribirContraseña1EditText, escribirContraseña2EditText;
    private View guardarDatosView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario_pantalla);

        motivoIncidenciaLayout = findViewById(R.id.motivoIncidencia);
        nombreUsuarioLayout = findViewById(R.id.nombreUsuario);
        escribirContraseña1Layout = findViewById(R.id.escribirContraseña1);
        escribirContraseña2Layout = findViewById(R.id.escribirContraseña2);

        motivoIncidenciaEditText = findViewById(R.id.motivoIncidenciaEditText);
        nombreUsuarioEditText = findViewById(R.id.nombreUsuarioEditText);
        escribirContraseña1EditText = findViewById(R.id.escribirContraseña1EditText);
        escribirContraseña2EditText = findViewById(R.id.escribirContraseña2EditText);

        guardarDatosView = findViewById(R.id.guardardatos);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        guardarDatosView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarUsuario();
            }
        });
    }

    private void guardarUsuario() {
        String correo = Objects.requireNonNull(motivoIncidenciaEditText.getText()).toString().trim();
        String nombreUsuario = Objects.requireNonNull(nombreUsuarioEditText.getText()).toString().trim();
        String contrasena1 = Objects.requireNonNull(escribirContraseña1EditText.getText()).toString().trim();
        String contrasena2 = Objects.requireNonNull(escribirContraseña2EditText.getText()).toString().trim();

        if (TextUtils.isEmpty(correo)) {
            motivoIncidenciaLayout.setError("Por favor, ingresa el correo electrónico");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches() ||
                (!correo.endsWith("@gmail.com") && !correo.endsWith("@shopytest.com"))) {
            motivoIncidenciaLayout.setError("Por favor, ingresa un correo electrónico válido");
            return;
        } else {
            motivoIncidenciaLayout.setError(null);
        }

        if (TextUtils.isEmpty(nombreUsuario)) {
            nombreUsuarioLayout.setError("Por favor, ingresa el nombre de usuario");
            return;
        } else {
            nombreUsuarioLayout.setError(null);
        }

        if (TextUtils.isEmpty(contrasena1)) {
            escribirContraseña1Layout.setError("Por favor, ingresa la contraseña");
            return;
        } else {
            escribirContraseña1Layout.setError(null);
        }

        if (TextUtils.isEmpty(contrasena2)) {
            escribirContraseña2Layout.setError("Por favor, repite la contraseña");
            return;
        } else if (!contrasena1.equals(contrasena2)) {
            escribirContraseña2Layout.setError("Las contraseñas no coinciden");
            return;
        } else {
            escribirContraseña2Layout.setError(null);
        }

        mAuth.createUserWithEmailAndPassword(correo, contrasena1)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                guardarDatosUsuarioEnFirestore(user, nombreUsuario, correo);
                            }
                        } else {
                            Toast.makeText(CrearUsuario.this, "Error al crear usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarDatosUsuarioEnFirestore(FirebaseUser user, String nombreUsuario, String correo) {
        String userId = user.getUid();
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("id", userId);
        usuario.put("name", nombreUsuario);
        usuario.put("email", correo);


        db.collection("user").document(userId)
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CrearUsuario.this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CrearUsuario.this, ListaUsuarios.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CrearUsuario.this, "Error al guardar datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
