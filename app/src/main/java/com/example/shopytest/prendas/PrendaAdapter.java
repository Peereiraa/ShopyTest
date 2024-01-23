package com.example.shopytest.prendas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopytest.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class PrendaAdapter extends RecyclerView.Adapter<PrendaAdapter.ViewHolder> {

    /**
     * Lista de documentos que representan prendas.
     */
    private List<DocumentSnapshot> listaPrendas;

    /**
     * Constructor de la clase PrendaAdapter.
     *
     * @param documentos Lista de documentos que representan prendas.
     */
    public PrendaAdapter(List<DocumentSnapshot> documentos) {
        this.listaPrendas = documentos;
    }

    /**
     * Método llamado cuando se necesita crear un nuevo ViewHolder.
     *
     * @param parent   Grupo de vistas principal al que se añadirá la nueva vista después de que se adjunte a una posición de adaptador.
     * @param viewType Tipo de la nueva vista.
     * @return Nuevo ViewHolder que contiene la vista creada.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prenda_categorias, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Método llamado para actualizar la vista de un ViewHolder existente.
     *
     * @param holder   ViewHolder a actualizar.
     * @param position Posición del elemento en el conjunto de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot documento = listaPrendas.get(position);

        // Configura tus Views con los datos del documento
        String nombre = documento.getString("Nombre");
        Double precio = documento.getDouble("Precio");
        String imageUrl = documento.getString("UrlImagen");

        // Manejo de nulos y valores por defecto
        if (nombre != null) {
            holder.nombrePrenda.setText(nombre);
        } else {
            holder.nombrePrenda.setText("Nombre no disponible");
        }

        if (precio != null) {
            holder.precioPrenda.setText(String.valueOf(precio)+" €");
        } else {
            holder.precioPrenda.setText("Precio no disponible");
        }

        // Puedes cargar la imagen aquí utilizando Glide o cualquier otra biblioteca de carga de imágenes
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.imagenPrenda);
        } else {
            // Puedes cargar una imagen de error o dejarlo en blanco según tu lógica
            // Agrega una imagen por defecto en caso de que no haya URL
        }
    }

    /**
     * Método que devuelve la cantidad de elementos en el conjunto de datos.
     *
     * @return Número total de elementos en el conjunto de datos.
     */
    @Override
    public int getItemCount() {
        return listaPrendas.size();
    }

    /**
     * Método que establece una nueva lista de documentos para mostrar en el RecyclerView.
     *
     * @param nuevosDocumentos Nueva lista de documentos que representan prendas.
     */
    public void setDocumentos(List<DocumentSnapshot> nuevosDocumentos) {
        this.listaPrendas = nuevosDocumentos;
        notifyDataSetChanged();
    }

    /**
     * Método que devuelve el documento en la posición dada.
     *
     * @param position Posición del documento en la lista.
     * @return Documento en la posición dada.
     */
    public DocumentSnapshot getDocumentAtPosition(int position) {
        return listaPrendas.get(position);
    }

    /**
     * Clase interna que representa un ViewHolder para los elementos de la lista de prendas.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * ImageView que muestra la imagen de la prenda.
         */
        ImageView imagenPrenda;

        /**
         * TextView que muestra el nombre de la prenda.
         */
        TextView nombrePrenda;

        /**
         * TextView que muestra el precio de la prenda.
         */
        TextView precioPrenda;

        /**
         * Constructor de la clase ViewHolder.
         *
         * @param itemView Vista que representa el elemento de la lista.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPrenda = itemView.findViewById(R.id.prenda);
            nombrePrenda = itemView.findViewById(R.id.nombreprenda);
            precioPrenda = itemView.findViewById(R.id.precioprenda);
        }
    }
}
