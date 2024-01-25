    package com.example.shopytest.profile;

    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.text.InputType;
    import android.text.TextUtils;
    import android.view.View;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.bumptech.glide.Glide;
    import com.example.shopytest.aplicacion.Perfil;
    import com.example.shopytest.R;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.GoogleAuthProvider;
    import com.google.firebase.auth.UserInfo;
    import com.google.firebase.auth.UserProfileChangeRequest;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.google.firebase.storage.UploadTask;

    import org.checkerframework.checker.nullness.qual.NonNull;

    /**
     * La clase {@code MiPerfil} representa la actividad que permite al usuario ver y modificar su perfil.
     * Proporciona la funcionalidad para cambiar el nombre, la imagen de perfil y la información del usuario.
     * Además, muestra la imagen de perfil actual y permite importar una nueva desde la galería.
     *
     * @author Pablo Pereira
     * @version 1.0
     */

    public class MiPerfil extends AppCompatActivity {


        /**
         * Representa un componente de interfaz de usuario que proporciona la funcionalidad para importar una nueva foto de perfil.
         */
        private TextView importarfoto;

        /**
         * Representa un componente de interfaz de usuario que permite al usuario volver a la pantalla anterior.
         */
        private ImageView volverboton;

        /**
         * Representa un componente de interfaz de usuario que muestra la imagen de perfil actual del usuario.
         */
        private ImageView tuimagen;

        /**
         * Objeto de autenticación de Firebase que gestiona la autenticación de usuarios.
         */
        private FirebaseAuth mAuth;

        /**
         * Representa el usuario actualmente autenticado en la aplicación.
         */
        private FirebaseUser user;

        /**
         * Campo de texto para editar o mostrar el nombre del usuario.
         */
        private EditText nombreEditText;

        /**
         * Campo de texto para editar o mostrar el correo electrónico del usuario.
         */
        private EditText emailEditText;

        /**
         * Campo de texto para mostrar el identificador único del usuario.
         */
        private EditText idEditText;

        /**
         * Representa un componente de interfaz de usuario que permite al usuario guardar los cambios realizados en su perfil.
         */
        private View guardarCambiosBtn;

        /**
         * URI que representa la ubicación de la imagen seleccionada desde la galería.
         */
        private Uri selectedImageUri;

        /**
         * Almacena la URL de la imagen de perfil del usuario en la base de datos.
         */
        private String imageUrlFromDatabase = "";

        /**
         * Objeto que proporciona métodos para interactuar con la base de datos Firestore de Firebase.
         */
        private FirebaseFirestore mFirestore;

        /**
         * Variable para cambiar el correo electronico
         */
        private TextView cambiarcorreo;

        private String currentUserId;

        /**
         * Método llamado cuando la actividad es creada. Aquí se inicializan los componentes de la interfaz
         * y se configuran los listeners para los eventos de clic en diferentes elementos.
         *
         * @param savedInstanceState Datos previamente guardados del estado de la actividad.
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mi_perfil);


            mAuth = FirebaseAuth.getInstance();

            mFirestore = FirebaseFirestore.getInstance();
            StorageReference storageRef;

            tuimagen = findViewById(R.id.fotoperfil);
            importarfoto = findViewById(R.id.cambiarfoto);
            nombreEditText = findViewById(R.id.nombrecuadro);
            emailEditText = findViewById(R.id.emailcuadro);
            idEditText = findViewById(R.id.contrasenacuadro);
            guardarCambiosBtn = findViewById(R.id.guardardatos);
            volverboton = findViewById(R.id.volver);
            cambiarcorreo = findViewById(R.id.cambiarcorreo);


            user = FirebaseAuth.getInstance().getCurrentUser();

            currentUserId = user.getUid();


            cambiarcorreo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDialogoCambiarCorreo();

                }
            });


            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    // Verifica si el usuario está autenticado con Google
                    if (GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                        // Si es autenticado con Google, carga la imagen de Google
                        Uri photoUrl = profile.getPhotoUrl();
                        if (photoUrl != null) {
                            Glide.with(MiPerfil.this)
                                    .load(photoUrl)
                                    .into(tuimagen);
                        }
                    } else {
                        // Si no es Google, carga la imagen desde el almacenamiento
                        storageRef = FirebaseStorage.getInstance().getReference()
                                .child("fotousuario/" + user.getUid() + "/imagenPerfil.jpg");

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Cargar la imagen del usuario en tuimagen usando Glide
                                Glide.with(MiPerfil.this)
                                        .load(uri)
                                        .into(tuimagen);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Manejar el caso en el que no hay imagen almacenada o hay un error al obtenerla
                            }
                        });
                    }
                }
            }

            volverboton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MiPerfil.this, Perfil.class);
                    startActivity(intent);
                }
            });


            mFirestore.collection("user").document(currentUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String nombreUsuario = documentSnapshot.getString("name");
                                String emailUsuario = documentSnapshot.getString("email");

                                nombreEditText.setText(nombreUsuario);
                                emailEditText.setText(emailUsuario);
                                idEditText.setText(user.getUid());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            guardarCambiosBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String nuevoNombre = nombreEditText.getText().toString().trim();
                    String nuevoCorreo = emailEditText.getText().toString().trim();


                    if (!TextUtils.isEmpty(nuevoNombre)) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nuevoNombre)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Actualizar el nombre en Firestore
                                        mFirestore.collection("user").document(currentUserId)
                                                .update("name", nuevoNombre)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(MiPerfil.this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(MiPerfil.this, "Error al actualizar el nombre en Firestore", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(MiPerfil.this, "Error al actualizar el nombre", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    if (selectedImageUri != null) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                                .child("fotousuario/" + user.getUid());

                        final StorageReference imagenRef = storageRef.child("imagenPerfil.jpg");

                        UploadTask uploadTask = imagenRef.putFile(selectedImageUri);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Obtener la URL de la imagen después de cargarla en Storage
                                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        // Actualizar el campo photoUrl en Firestore con la nueva URL
                                        mFirestore.collection("user").document(currentUserId)
                                                .update("photoUrl", uri.toString())
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(MiPerfil.this, "Imagen subida y URL actualizada correctamente", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(MiPerfil.this, "Error al actualizar la URL de la imagen en Firestore", Toast.LENGTH_SHORT).show();
                                                });
                                    });
                                } else {
                                    Toast.makeText(MiPerfil.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });



            importarfoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (UserInfo userInfo : user.getProviderData()) {
                        if (userInfo.getProviderId().equals("google.com")) {
                            // Si el usuario ha iniciado sesión con Google, no permitir la carga de imagen
                            Toast.makeText(MiPerfil.this, "No puedes cambiar la imagen si has iniciado sesión con Google", Toast.LENGTH_SHORT).show();
                            return; // Salir del método onClick
                        }
                    }
                    cargarImagen();
                }
            });

            mFirestore.collection("user").document(currentUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String nombreUsuario = documentSnapshot.getString("name");
                                String emailUsuario = documentSnapshot.getString("email");

                                nombreEditText.setText(nombreUsuario);
                                emailEditText.setText(emailUsuario);
                                idEditText.setText(user.getUid());

                                // Verificar el proveedor de autenticación
                                for (UserInfo userInfo : user.getProviderData()) {
                                    if (userInfo.getProviderId().equals("google.com")) {
                                        // Usuario autenticado con Google, deshabilitar la edición del nombre
                                        nombreEditText.setEnabled(false);
                                    } else if (userInfo.getProviderId().equals("firebase")) {
                                        // Usuario autenticado con correo y contraseña, permitir la edición del nombre
                                        nombreEditText.setEnabled(true);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar errores
                        }
                    });
        }

        /**
         * Método utilizado para iniciar la selección de una nueva imagen desde la galería.
         */
        private void cargarImagen(){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicacion"), 10);
        }

        private void mostrarDialogoCambiarCorreo() {
            // Crear un LinearLayout vertical para contener los EditText
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            // Crear un EditText para el nuevo correo
            final EditText inputNuevoCorreo = new EditText(this);
            inputNuevoCorreo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            inputNuevoCorreo.setHint("Nuevo Correo Electrónico");

            // Añadir el primer EditText al layout
            layout.addView(inputNuevoCorreo);

            // Crear un segundo EditText para la confirmación del correo
            final EditText inputConfirmarCorreo = new EditText(this);
            inputConfirmarCorreo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            inputConfirmarCorreo.setHint("Confirmar Correo Electrónico");

            // Añadir el segundo EditText al layout
            layout.addView(inputConfirmarCorreo);

            // Crear el cuadro de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cambiar Correo")
                    .setView(layout)
                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Obtener los correos electrónicos ingresados por el usuario
                            String nuevoCorreo = inputNuevoCorreo.getText().toString().trim();
                            String confirmarCorreo = inputConfirmarCorreo.getText().toString().trim();

                            // Validar que los correos electrónicos sean iguales
                            if (nuevoCorreo.equals(confirmarCorreo)) {
                                // Realizar el cambio de correo electrónico
                                cambiarCorreo(nuevoCorreo);
                            } else {
                                Toast.makeText(MiPerfil.this, "Los correos electrónicos no coinciden", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Cerrar el cuadro de diálogo
                            dialog.dismiss();
                        }
                    });

            // Mostrar el cuadro de diálogo
            builder.show();
        }

        private void cambiarCorreo(String nuevoCorreo) {

            user.updateEmail(nuevoCorreo)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Enviar el correo de verificación
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailVerificationTask -> {
                                        if (emailVerificationTask.isSuccessful()) {
                                            Toast.makeText(MiPerfil.this, "Correo actualizado. Se ha enviado un correo de verificación.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MiPerfil.this, "Error al enviar el correo de verificación.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MiPerfil.this, "Error al actualizar el correo", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        /**
         * Método llamado cuando una actividad iniciada para obtener resultados finaliza.
         * En este caso, se utiliza para manejar el resultado de la selección de una imagen desde la galería.
         *
         * @param requestCode Código de solicitud que identifica la operación que se está completando.
         * @param resultCode  Código de resultado que indica el resultado de la operación.
         * @param data        Intent que contiene los datos devueltos por la actividad.
         */
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    tuimagen.setImageURI(selectedImageUri);

                    imageUrlFromDatabase = selectedImageUri.toString();
                }
            }
        }
        }


