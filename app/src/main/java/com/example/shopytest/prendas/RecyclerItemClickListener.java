package com.example.shopytest.prendas;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Clase que implementa la interfaz RecyclerView.OnItemTouchListener para manejar clics y clics largos en elementos de RecyclerView.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    /**
     * Interfaz para manejar eventos de clic y clic largo.
     */
    public interface OnItemClickListener {

        /**
         * Se llama cuando se realiza un clic en un elemento.
         *
         * @param view     La vista del elemento clicado.
         * @param position La posición del elemento en el RecyclerView.
         */
        void onItemClick(View view, int position);

        /**
         * Se llama cuando se realiza un clic largo en un elemento.
         *
         * @param view     La vista del elemento clicado largamente.
         * @param position La posición del elemento en el RecyclerView.
         */
        void onLongItemClick(View view, int position);
    }

    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    /**
     * Constructor de la clase RecyclerItemClickListener.
     *
     * @param context   Contexto de la aplicación.
     * @param recyclerView El RecyclerView al que se aplica este listener.
     * @param listener  El objeto que implementa la interfaz OnItemClickListener.
     */
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    /**
     * Método que se llama cuando se intercepta un evento táctil en el RecyclerView.
     *
     * @param rv El RecyclerView al que se aplica este listener.
     * @param e  El evento táctil.
     * @return True si se maneja el evento, false de lo contrario.
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(child, rv.getChildAdapterPosition(child));
            return true;
        }
        return false;
    }

    /**
     * Método que se llama cuando un evento táctil del RecyclerView está en progreso.
     *
     * @param rv El RecyclerView al que se aplica este listener.
     * @param e  El evento táctil.
     */
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // No implementado
    }

    /**
     * Método que se llama cuando se solicita no interceptar más eventos táctiles.
     *
     * @param disallowIntercept True si se debe evitar la interceptación, false de lo contrario.
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // No implementado
    }
}
