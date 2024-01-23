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

public class PrendaAdminAdapter extends RecyclerView.Adapter<PrendaAdminAdapter.ViewHolder> {

    private List<DocumentSnapshot> listaPrendas;
    private List<PrendaRopa> prendasFull;


    public PrendaAdminAdapter() {
        this.listaPrendas = new ArrayList<>();
        this.prendasFull = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prenda_ropa_admin, parent, false);
        return new ViewHolder(view);
    }

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



    @Override
    public int getItemCount() {
        return listaPrendas.size();
    }

    public void setPrendas(List<DocumentSnapshot> nuevasPrendas) {
        this.listaPrendas = nuevasPrendas;
        notifyDataSetChanged();
    }

    public DocumentSnapshot getPrendaAtPosition(int position) {
        return listaPrendas.get(position);
    }

    public void setDocumentos(List<DocumentSnapshot> nuevosDocumentos) {
        this.listaPrendas = nuevosDocumentos;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenPrenda;
        TextView nombrePrenda;
        TextView precioPrenda;
        Button btnEliminar;
        String prendaId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenPrenda = itemView.findViewById(R.id.imagePrenda);
            nombrePrenda = itemView.findViewById(R.id.nombrePrenda);
            precioPrenda = itemView.findViewById(R.id.precioPrenda);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }


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

    private void removerItem(int position) {
        // Remueve la prenda de la lista y notifica al RecyclerView
        listaPrendas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }


}
