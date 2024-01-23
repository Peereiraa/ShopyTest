package com.example.shopytest.administrador.usuarios;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopytest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;


public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.ViewHolder> {

    private List<DocumentSnapshot> listaUsuarios;
    private List<DocumentSnapshot> listaUsuariosCompleta;

    public ListaUsuariosAdapter(List<DocumentSnapshot> documentos) {

        this.listaUsuarios = documentos;
        this.listaUsuariosCompleta = new ArrayList<>(documentos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_usuarios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot documento = listaUsuarios.get(position);

        // Convertir DocumentSnapshot a objeto Usuario
        Usuario usuario = Usuario.fromDocumentSnapshot(documento);


        // Configura tus Views con los datos del usuario
        holder.nombreUsuario.setText(usuario.getNombre());
        holder.correoUsuario.setText(usuario.getCorreo());


        Glide.with(holder.itemView.getContext())
                .load(usuario.getPhotoUrl())
                .error(R.drawable.logo_default)
                .into(holder.logoUsuario);

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void setDocumentos(List<DocumentSnapshot> nuevosDocumentos) {
        this.listaUsuarios = nuevosDocumentos;
        this.listaUsuariosCompleta = new ArrayList<>(nuevosDocumentos);
        notifyDataSetChanged();
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase();
                List<DocumentSnapshot> filteredList = new ArrayList<>();

                for (DocumentSnapshot usuario : listaUsuariosCompleta) {
                    String nombre = usuario.getString("name").toLowerCase();
                    if (nombre.contains(searchText)) {
                        filteredList.add(usuario);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listaUsuarios.clear();
                listaUsuarios.addAll((List<DocumentSnapshot>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }



    public DocumentSnapshot getDocumentAtPosition(int position) {
        return listaUsuarios.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logoUsuario;
        TextView nombreUsuario;
        TextView correoUsuario;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logoUsuario = itemView.findViewById(R.id.logoUsuario);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            correoUsuario = itemView.findViewById(R.id.correoUsuario);


        }
    }


    }








