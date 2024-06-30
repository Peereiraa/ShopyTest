// IncidenciaAdapter.java
package com.example.shopytest.administrador.incidencias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopytest.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.ViewHolder> {

    private Context mContext;
    private List<Incidencia> mListIncidencias;
    private FirebaseFirestore db;

    public IncidenciaAdapter(Context context, List<Incidencia> incidencias) {
        mContext = context;
        mListIncidencias = incidencias;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lista_incidencias, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia incidencia = mListIncidencias.get(position);

        // Setear los datos de la incidencia en la tarjeta
        holder.nombreUsuario.setText(incidencia.getNombreUsuario());
        holder.motivoIncidencia.setText("Motivo: " + incidencia.getMotivo());
        holder.seccionIncidencia.setText("Sección: " + incidencia.getSeccion());
        Glide.with(mContext).load(incidencia.getPhotoUrlUsuario()).error(R.drawable.logo_default).into(holder.imageUsuario);

        // Añadir el OnClickListener al botón "Solucionar"
        holder.buttonSolucionada.setOnClickListener(v -> {
            // Eliminar la incidencia de la base de datos
            db.collection("Incidencias").document(incidencia.getIdIncidencia())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Eliminar la incidencia de la lista y notificar al adapter
                        int removedPosition = holder.getAdapterPosition();
                        mListIncidencias.remove(removedPosition);
                        notifyItemRemoved(removedPosition);
                        Toast.makeText(mContext, "Incidencia solucionada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(mContext, "Error al eliminar la incidencia", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return mListIncidencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUsuario;
        TextView nombreUsuario, motivoIncidencia, seccionIncidencia;
        Button buttonSolucionada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUsuario = itemView.findViewById(R.id.imageUsuario);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            motivoIncidencia = itemView.findViewById(R.id.motivoIncidencia);
            seccionIncidencia = itemView.findViewById(R.id.seccionIncidencia);
            buttonSolucionada = itemView.findViewById(R.id.buttonSolucionada);
        }
    }
}
