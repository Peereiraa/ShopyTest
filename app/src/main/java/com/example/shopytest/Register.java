package com.example.shopytest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * La clase {@code Register} representa la actividad de registro en la aplicación.
 * Permite a los usuarios crear una cuenta utilizando correo y contraseña.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class Register extends AppCompatActivity {

    /** TextView para redirigir a la pantalla de inicio de sesión. */
    private TextView yatienescuenta;

    /** Vista del botón de registro. */
    private View botonregistrar;

    /** ImageView para alternar la visibilidad de la contraseña en el primer campo. */
    private ImageView rnomostrarContraseña1, rnomostrarContraseña2, rmostrarcontraseña1, rmostrarcontraseña2;

    /** EditText para ingresar el nombre de usuario, la dirección de correo electrónico , ingresar la primera contraseña, la segunda contraseña y confirmar. */
    private EditText contraseña1, contraseña2, usuario, correo;

    /** Indicador de visibilidad de la primera contraseña. */
    private boolean esVisible = false;

    /** Indicador de visibilidad de la segunda contraseña. */
    private boolean esVisible2 = false;

    /** Instancia de FirebaseFirestore para interactuar con la base de datos Firestore. */
    FirebaseFirestore mFirestore;

    /** Cliente de inicio de sesión de Google. */
    GoogleSignInClient mGoogleSignInClient;

    /** Instancia de FirebaseAuth para la autenticación de Firebase. */
    FirebaseAuth mAuth;

    /**
     * Método llamado cuando la actividad se está creando.
     *
     * @param savedInstanceState Datos que pueden haberse guardado del estado anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicialización de vistas y Firebase
        rnomostrarContraseña1 = findViewById(R.id.novercontraseña1);
        rnomostrarContraseña2 = findViewById(R.id.novercontraseña2);
        rmostrarcontraseña1 = findViewById(R.id.vercontraseña1);
        rmostrarcontraseña2 = findViewById(R.id.vercontraseña2);
        botonregistrar = findViewById(R.id.botonregistrar);
        usuario = findViewById(R.id.nombreusuario);
        correo = findViewById(R.id.correoelectronico);
        contraseña1 = findViewById(R.id.contraseña);
        contraseña2 = findViewById(R.id.repetircontraseña);
        yatienescuenta = findViewById(R.id.ya_tienes_c);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        // Configuración de clics en vistas
        botonregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = usuario.getText().toString().trim();
                String email = correo.getText().toString().trim();
                String contra1 = contraseña1.getText().toString().trim();
                String contra2 = contraseña2.getText().toString().trim();

                registerUser(user, email, contra1, contra2);

            }
        });


        yatienescuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });


        rnomostrarContraseña1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                toggleVisibilityNoVer1();
            }
        });

        rmostrarcontraseña1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                toggleVisibilityVer1();
            }
        });

        rmostrarcontraseña2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                toggleVisibilityVer2();
            }
        });

        rnomostrarContraseña2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                toggleVisibilityNoVer2();
            }
        });

    }

    /**
     * Registra al usuario en Firebase y guarda información adicional en Firestore.
     *
     * @param nombre Nombre del usuario.
     * @param email Correo electrónico del usuario.
     * @param contra1 Contraseña del usuario.
     * @param contra2 Confirmación de la contraseña del usuario.
     */
    private void registerUser(String nombre, String email, String contra1, String contra2){
        if(nombre.isEmpty()){
            Toast.makeText(Register.this, "Datos restantes: Usuario", Toast.LENGTH_SHORT).show();
        } else if(email.isEmpty()){
            Toast.makeText(Register.this, "Datos restantes: Correo Electrónico", Toast.LENGTH_SHORT).show();
        } else if(contra1.isEmpty()){
            Toast.makeText(Register.this, "Datos restantes: Contraseña", Toast.LENGTH_SHORT).show();
        } else if(!contra2.equals(contra1)){
            Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            createUserInFirebase(nombre, email, contra1);
        }
    }

    /**
     * Crea un nuevo usuario en Firebase Authentication y guarda información adicional en Firestore.
     *
     * @param nombre Nombre del usuario.
     * @param email Correo electrónico del usuario.
     * @param contra Contraseña del usuario.
     */
    private void createUserInFirebase(String nombre, String email, String contra){
        mAuth.createUserWithEmailAndPassword(email, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("name", nombre);
                    map.put("email", email);
                    map.put("pass", contra);

                    mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Redirige a la pantalla de inicio de sesión después de un registro exitoso
                            finish();
                            startActivity(new Intent(Register.this, Login.class));
                            Toast.makeText(Register.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Error al guardar en Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Alterna la visibilidad de la primera contraseña para mostrar u ocultar el texto.
     */
    private void toggleVisibilityVer1() {
        if (esVisible) {
            contraseña1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            esVisible = false;
            // Cambia la imagen para representar que la contraseña está oculta
            rmostrarcontraseña1.setVisibility(View.INVISIBLE);
            rnomostrarContraseña1.setVisibility(View.VISIBLE);
            rmostrarcontraseña2.setVisibility(View.INVISIBLE);
            rnomostrarContraseña2.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Alterna la visibilidad de la primera contraseña para mostrar u ocultar el texto.
     */
    private void toggleVisibilityNoVer1() {
        if (!esVisible) {
            contraseña1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            esVisible = true;
            // Cambia la imagen para representar que la contraseña es visible
            rmostrarcontraseña1.setVisibility(View.VISIBLE);
            rnomostrarContraseña1.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Alterna la visibilidad de la segunda contraseña para mostrar u ocultar el texto.
     */
    private void toggleVisibilityVer2(){
        if (esVisible2) {
            contraseña2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            esVisible2 = false;
            // Cambia la imagen para representar que la contraseña está oculta
            rmostrarcontraseña2.setVisibility(View.INVISIBLE);
            rnomostrarContraseña2.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Alterna la visibilidad de la segunda contraseña para mostrar u ocultar el texto.
     */
    public void toggleVisibilityNoVer2() {
        if (!esVisible2) {
            contraseña2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            esVisible2 = true;
            // Cambia la imagen para representar que la contraseña es visible
            rmostrarcontraseña2.setVisibility(View.VISIBLE);
            rnomostrarContraseña2.setVisibility(View.INVISIBLE);
        }
    }
}