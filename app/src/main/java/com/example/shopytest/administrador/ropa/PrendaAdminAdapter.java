package com.example.shopytest.administrador.ropa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopytest.R;
import com.example.shopytest.administrador.usuarios.Usuario;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * El adaptador de RecyclerView para mostrar y gestionar la lista de prendas de ropa en la interfaz de administrador.
 * Permite la visualización, eliminación y filtrado de las prendas de ropa.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class PrendaAdminAdapter extends RecyclerView.Adapter<PrendaAdminAdapter.ViewHolder> {

    /**
     * Lista de DocumentSnapshot que representa las prendas de ropa en Firestore.
     */
    private List<DocumentSnapshot> listaPrendas;

    /**
     * Lista de todas las prendas de ropa utilizada para el filtrado.
     */
    private List<PrendaRopa> prendasFull;

    /**
     * Constructor de la clase PrendaAdminAdapter.
     * Inicializa las listas de prendas de ropa.
     */
    public PrendaAdminAdapter() {
        this.listaPrendas = new ArrayList<>();
        this.prendasFull = new ArrayList<>();
    }

    /**
     * Crea nuevas instancias de ViewHolder (uno por cada elemento de la lista) cuando se necesita.
     *
     * @param parent   El ViewGroup en el que se inflará el nuevo View.
     * @param viewType El tipo de vista del nuevo View.
     * @return Una nueva instancia del ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prenda_ropa_admin, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Actualiza el contenido del ViewHolder con la prenda de ropa en la posición dada.
     *
     * @param holder   El ViewHolder que debe ser actualizado.
     * @param position La posición del elemento en la lista de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot documento = listaPrendas.get(position);

        // Convertir DocumentSnapshot a objeto Usuario
        PrendaRopa prenda = PrendaRopa.fromDocumentSnapshot(documento);

        holder.nombrePrenda.setText(prenda.getNombre());

        // Formatea el precio como deseado
        holder.precioPrenda.setText(prenda.getPrecio().toString());

        // Carga la imagen utilizando Glide o cualquier otra biblioteca de carga de imágenes
        Glide.with(holder.itemView.getContext())
                .load(prenda.getUrlImagen())
                .into(holder.imagenPrenda);

        // Configura el botón de eliminar
        holder.prendaId = documento.getId();
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPrenda(holder.prendaId);
                removerItem(position);
            }
        });
    }


    /**
     * Devuelve el número total de elementos en el conjunto de datos que el adaptador maneja.
     *
     * @return El número total de elementos en el conjunto de datos.
     */
    @Override
    public int getItemCount() {
        return listaPrendas.size();
    }

    /**
     * Establece la lista de prendas de ropa y notifica al adaptador que los datos han cambiado.
     *
     * @param nuevasPrendas Lista de DocumentSnapshot que representa las nuevas prendas de ropa.
     */
    public void setPrendas(List<DocumentSnapshot> nuevasPrendas) {
        this.listaPrendas = nuevasPrendas;
        notifyDataSetChanged();
    }

    /**
     * Obtiene el DocumentSnapshot de la prenda de ropa en la posición dada.
     *
     * @param position La posición de la prenda de ropa en la lista.
     * @return El DocumentSnapshot de la prenda de ropa.
     */
    public DocumentSnapshot getPrendaAtPosition(int position) {
        return listaPrendas.get(position);
    }


    /**
     * Establece la lista de DocumentSnapshot y notifica al adaptador que los datos han cambiado.
     *
     * @param nuevosDocumentos Lista de DocumentSnapshot que representa las nuevas prendas de ropa.
     */
    public void setDocumentos(List<DocumentSnapshot> nuevosDocumentos) {
        this.listaPrendas = nuevosDocumentos;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para mantener las vistas de un elemento de la lista de prendas de ropa.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * ImageView utilizada para mostrar la imagen de la prenda de ropa.
         */
        ImageView imagenPrenda;

        /**
         * TextView utilizado para mostrar el nombre de la prenda de ropa.
         */
        TextView nombrePrenda;

        /**
         * TextView utilizado para mostrar el precio de la prenda de ropa.
         */
        TextView precioPrenda;

        /**
         * Button utilizado para eliminar la prenda de ropa.
         */
        Button btnEliminar;

        /**
         * String que almacena el ID único de la prenda de ropa.
         */
        String prendaId;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPrenda = itemView.findViewById(R.id.imagePrenda);
            nombrePrenda = itemView.findViewById(R.id.nombrePrenda);
            precioPrenda = itemView.findViewById(R.id.precioPrenda);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    /**
     * Elimina una prenda de ropa de la base de datos utilizando su ID y actualiza el adaptador.
     *
     * @param prendaId El ID de la prenda de ropa que se va a eliminar.
     */
    public void eliminarPrenda(String prendaId) {
        // Obtén la referencia de la colección "PrendasRopa" y elimina la prenda por su ID
        FirebaseFirestore.getInstance().collection("PrendasRopa")
                .document(prendaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Maneja el éxito de la eliminación
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Maneja el fallo en la eliminación
                });
    }

    /**
     * Remueve una prenda de la lista y notifica al adaptador que los datos han cambiado.
     *
     * @param position La posición de la prenda de ropa en la lista.
     */
    private void removerItem(int position) {
        // Remueve la prenda de la lista y notifica al RecyclerView
        listaPrendas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }


}
