    package com.example.shopytest.aplicacion.identificacion;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.method.HideReturnsTransformationMethod;
    import android.text.method.PasswordTransformationMethod;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;


    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;

    import com.example.shopytest.R;
    import com.example.shopytest.administrador.MenuPantallaAdminActivity;
    import com.example.shopytest.aplicacion.Inicio;
    import com.google.android.gms.auth.api.signin.GoogleSignIn;
    import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
    import com.google.android.gms.auth.api.signin.GoogleSignInClient;
    import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
    import com.google.android.gms.common.api.ApiException;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.auth.GoogleAuthProvider;
    import com.google.firebase.auth.AuthCredential;


    import java.util.HashMap;
    import java.util.Map;

    /**
     * La clase {@code Login} representa la actividad de inicio de sesión en la aplicación.
     * Permite a los usuarios iniciar sesión mediante correo y contraseña, así como también
     * mediante la autenticación de Google.
     *
     * @author Pablo Pereira
     * @version 1.0
     */
    public class Login extends AppCompatActivity {

        /** TextView para redirigir a la pantalla de registro. */
        private TextView noTienesCuenta, googlelogin;

        /** ImageView para alternar la visibilidad de la contraseña. */
        private ImageView mostrarContraseña, nomostrarContraseña, volveralinicio;

        /** EditText para ingresar el correo y la contraseña. */
        private EditText ponerCorreo, ponerContraseña;

        /** FirebaseAuth para la autenticación de Firebase. */
        FirebaseAuth mAuth;

        /** FirebaseFirestore para interactuar con la base de datos Firestore. */
        FirebaseFirestore mFirestore;

        /** Código de solicitud para la autenticación de Google. */
        int RC_SIGN_IN = 20;

        /** Cliente de inicio de sesión de Google. */
        GoogleSignInClient mGoogleSignInClient;

        /** Vista de inicio de sesión. */
        private View login;

        /** Indicador de visibilidad de la contraseña. */
        private boolean esVisible = false;

        /**Variable para accion de eliminar contrasela */
        private TextView olvidecontraseña;


        /**
         * Método llamado cuando la actividad se está creando.
         *
         * @param savedInstanceState Datos que pueden haberse guardado del estado anterior.
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);


            // Inicialización de vistas y Firebase
            noTienesCuenta = findViewById(R.id.no_tienes_c);
            mostrarContraseña = findViewById(R.id.vercontraseña);
            ponerCorreo = findViewById(R.id.ponercorreo);
            ponerContraseña = findViewById(R.id.ponercontraseña);
            nomostrarContraseña = findViewById(R.id.novercontraseña);
            volveralinicio = findViewById(R.id.volver);
            googlelogin = findViewById(R.id.google);
            olvidecontraseña = findViewById(R.id.olvidecontraseña);

            login = findViewById(R.id.rectangle_2);
            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();

            // Configuración del cliente de inicio de sesión de Google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

            // Configuración de clics en vistas
            googlelogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googleSignIn();
                }
            });


            olvidecontraseña.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!ponerCorreo.getText().toString().equals("")){
                        enviarCorreoRestablecimiento(ponerCorreo.getText().toString().trim());
                    }else{
                        Toast.makeText(Login.this, "Credenciales inválidas. Verifica tu correo y contraseña.", Toast.LENGTH_SHORT).show();
                    }

                }
            });






            volveralinicio.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(Login.this, Inicio.class);
                    startActivity(intent);
                }
            });



            noTienesCuenta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Login.this, Register.class);
                    startActivity(intent);
                }
            });

            mostrarContraseña.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleVisibilityVer();
                }
            });

            nomostrarContraseña.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    toggleVisibilityNoVer();
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLoginClick();
                }
            });
        }


        /**
         * Maneja el clic en el botón de inicio de sesión.
         */
        public void onLoginClick() {
            String correo = ponerCorreo.getText().toString().trim();
            String contraseña = ponerContraseña.getText().toString().trim();

            if (correo.isEmpty() && contraseña.isEmpty()) {
                Toast.makeText(this, "Credenciales inválidas. Verifica tu correo y contraseña.", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(correo, contraseña);
            }
        }

        /**
         * Inicia sesión del usuario mediante correo y contraseña.
         *
         * @param correo Correo electrónico del usuario.
         * @param contraseña Contraseña del usuario.
         */

        private void loginUser(String correo, String contraseña) {
            mAuth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Verificar si el usuario es un administrador
                                String userEmail = user.getEmail();
                                if (userEmail != null) {
                                    if (userEmail.matches(".*@shopytest\\.com")) {
                                        // Si el correo coincide con el dominio personalizado
                                        finish();
                                        startActivity(new Intent(Login.this, MenuPantallaAdminActivity.class));
                                        Toast.makeText(Login.this, "Bienvenido Admin", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Si el correo no coincide con el dominio personalizado, considerarlo como un usuario normal
                                        finish();
                                        startActivity(new Intent(Login.this, Inicio.class));

                                        // Actualizar el correo en Firestore
                                        actualizarCorreoEnFirestore(user.getUid(), userEmail);

                                        Toast.makeText(Login.this, "Bienvenido Usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show());
        }

        private void actualizarCorreoEnFirestore(String userId, String nuevoCorreo) {
            mFirestore.collection("user").document(userId)
                    .update("email", nuevoCorreo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Puedes realizar acciones adicionales después de actualizar el correo en Firestore
                            // Por ejemplo, mostrar un mensaje o realizar otras operaciones.
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar el error al actualizar en Firestore
                            Toast.makeText(getApplicationContext(), "Error al actualizar el correo en Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

// ...


        /**
         * Alterna la visibilidad de la contraseña para mostrar u ocultar el texto.
         */
        public void toggleVisibilityVer() {
            if (esVisible) {
                ponerContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
                esVisible = false;
                // Cambia la imagen para representar que la contraseña está oculta
                mostrarContraseña.setVisibility(View.INVISIBLE);
                nomostrarContraseña.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Alterna la visibilidad de la contraseña para mostrar u ocultar el texto.
         */
        public void toggleVisibilityNoVer() {
            if (!esVisible) {
                ponerContraseña.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                esVisible = true;
                // Cambia la imagen para representar que la contraseña es visible
                mostrarContraseña.setVisibility(View.VISIBLE);
                nomostrarContraseña.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * Inicia el proceso de inicio de sesión con Google.
         */
        private void googleSignIn(){
            Intent intent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent,RC_SIGN_IN);
        }

        /**
         * Maneja el resultado de la actividad de inicio de sesión de Google.
         *
         * @param requestCode Código de solicitud.
         * @param resultCode Código de resultado.
         * @param data Datos de la actividad.
         */
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String idToken = account.getIdToken();
                    firebaseAuthWithGoogle(idToken);
                } catch (ApiException e) {
                    Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * Envía un correo de restablecimiento de contraseña a la dirección de correo proporcionada.
         *
         * @param correo Dirección de correo electrónico a la que se enviará el correo de restablecimiento de contraseña.
         */
        private void enviarCorreoRestablecimiento(String correo) {

            mAuth.sendPasswordResetEmail(correo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Se ha enviado un correo de restablecimiento de contraseña a tu dirección de correo electrónico.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Error al enviar el correo de restablecimiento de contraseña. Verifica tu correo electrónico.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


        /**
         * Autentica al usuario en Firebase utilizando el token de ID de Google.
         *
         * @param idToken Token de ID de Google.
         */
        private void firebaseAuthWithGoogle(String idToken) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String id = user.getUid();
                                    String name = user.getDisplayName();
                                    String email = user.getEmail();
                                    String photoUrl = "";
                                    if (user.getPhotoUrl() != null) {
                                        photoUrl = user.getPhotoUrl().toString();
                                    }



                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("id", id);
                                    userData.put("name", name);
                                    userData.put("photoUrl", photoUrl);
                                    userData.put("email", email);



                                    mFirestore.collection("user").document(id)
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // On successful save, proceed to Login activity
                                                    startActivity(new Intent(Login.this, Inicio.class));
                                                    finish();
                                                    Toast.makeText(Login.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Login.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                }
                            } else {
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }





    }
