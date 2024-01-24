package com.example.shopytest.aplicacion.carga;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopytest.R;
import com.example.shopytest.administrador.MenuPantallaAdminActivity;
import com.example.shopytest.aplicacion.Inicio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * La clase {@code PantallaDeCarga} representa la actividad de carga al iniciar la aplicación.
 * Muestra una animación de transición, simula un proceso de carga y redirige a la pantalla de inicio
 * o a la pantalla de administrador según el estado del usuario.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class PantallaDeCarga extends AppCompatActivity {


    /** ImageView para mostrar la imagen de transición. */
    private ImageView imageView;

    /** ProgressBar para indicar el progreso de carga. */
    private ProgressBar progressBar;

    /** TextView para mostrar el texto de carga. */
    private TextView loadingText;

    /**
     * Método llamado cuando la actividad se está creando.
     *
     * @param savedInstanceState Datos que pueden haberse guardado del estado anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);

        // Inicialización de vistas
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        // Inicia la animación de transición
        startTransitionAnimation();

        // Simula un proceso de carga con un temporizador
        simulateLoading();
    }


    /**
     * Inicia la animación de transición que muestra la imagen y luego revela el ProgressBar y el TextView.
     */
    private void startTransitionAnimation() {
        // Animación para mostrar la imagen
        AlphaAnimation showImageAnimation = new AlphaAnimation(0.0f, 1.0f);
        showImageAnimation.setDuration(1500); // Duración de la animación en milisegundos
        showImageAnimation.setFillAfter(true); // Mantiene el estado final de la animación

        // Inicia la animación en el ImageView
        imageView.startAnimation(showImageAnimation);

        // Configura una escucha para detectar el final de la animación
        showImageAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // No se necesita implementación en este caso
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Muestra el ProgressBar y el TextView después de mostrar la imagen
                progressBar.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);

                // Limpia las animaciones en el ImageView para evitar interferencias
                imageView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No se necesita implementación en este caso
            }
        });
    }


    /**
     * Simula el proceso de carga y redirige a la pantalla de inicio o de administrador según el estado del usuario.
     */
    private void simulateLoading() {
        // Simula una pausa antes de mostrar la pantalla de inicio (ajusta el tiempo según tus necesidades)
        new Handler().postDelayed(() -> {
            // Oculta la barra de progreso y el texto de carga
            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);

            // Muestra la imagen después de la carga
            imageView.setVisibility(View.VISIBLE);

            // Verifica si el usuario actual es administrador
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                if (userEmail != null && userEmail.matches(".*@shopytest\\.com")) {
                    // Si el correo coincide con el dominio personalizado, abre la pantalla de administrador
                    Intent adminIntent = new Intent(PantallaDeCarga.this, MenuPantallaAdminActivity.class);
                    startActivity(adminIntent);
                } else {
                    // Si no es administrador, abre la pantalla de inicio
                    Intent homeIntent = new Intent(PantallaDeCarga.this, Inicio.class);
                    startActivity(homeIntent);
                }
            } else {
                // Si no hay un usuario, abre la pantalla de inicio
                Intent homeIntent = new Intent(PantallaDeCarga.this, Inicio.class);
                startActivity(homeIntent);
            }

            // Cierra la actividad actual para que no pueda volver a esta pantalla de carga
            finish();
        }, 3500); // Cambiado a 0 para que la pantalla de inicio aparezca de inmediato
    }

}
