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

    private List<DocumentSnapshot> listaPrendas;

    public PrendaAdapter(List<DocumentSnapshot> documentos) {
        this.listaPrendas = documentos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prenda_categorias, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return listaPrendas.size();
    }

    public void setDocumentos(List<DocumentSnapshot> nuevosDocumentos) {
        this.listaPrendas = nuevosDocumentos;
        notifyDataSetChanged();
    }

    public DocumentSnapshot getDocumentAtPosition(int position) {
        return listaPrendas.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenPrenda;
        TextView nombrePrenda;
        TextView precioPrenda;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPrenda = itemView.findViewById(R.id.prenda);
            nombrePrenda = itemView.findViewById(R.id.nombreprenda);
            precioPrenda = itemView.findViewById(R.id.precioprenda);
        }
    }
}
