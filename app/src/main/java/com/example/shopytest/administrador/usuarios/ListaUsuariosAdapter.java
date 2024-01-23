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

/**
 * Clase adaptadora para el RecyclerView que muestra la lista de usuarios en la interfaz de usuario.
 * Permite la visualización y filtrado de usuarios, así como la realización de acciones específicas.
 *
 * @author Pablo Pereira
 * @version 1.0
 */
public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.ViewHolder> {

    /**
     * Lista de DocumentSnapshot que representa la lista de usuarios a mostrar.
     */
    private List<DocumentSnapshot> listaUsuarios;

    /**
     * Lista completa de DocumentSnapshot que se utiliza para el filtrado de búsqueda.
     */
    private List<DocumentSnapshot> listaUsuariosCompleta;

    /**
     * Constructor de la clase adaptadora.
     *
     * @param documentos Lista inicial de DocumentSnapshot que representa la lista de usuarios.
     */
    public ListaUsuariosAdapter(List<DocumentSnapshot> documentos) {

        this.listaUsuarios = documentos;
        this.listaUsuariosCompleta = new ArrayList<>(documentos);
    }

    /**
     * Método llamado para crear un nuevo ViewHolder cuando se necesita una nueva fila en el RecyclerView.
     *
     * @param parent   El ViewGroup en el que se inflará la nueva vista.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Nuevo ViewHolder que contiene la vista inflada.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_usuarios, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Método llamado para actualizar una fila del RecyclerView con los datos del usuario correspondiente.
     *
     * @param holder   ViewHolder que debe actualizarse.
     * @param position La posición del elemento en el conjunto de datos.
     */
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

    /**
     * Método que devuelve el número total de elementos en el conjunto de datos.
     *
     * @return Número total de elementos en el conjunto de datos.
     */
    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    /**
     * Método para establecer nuevos documentos en la lista y actualizar el conjunto de datos.
     *
     * @param nuevosDocumentos Lista de nuevos DocumentSnapshot que representan la lista actualizada de usuarios.
     */
    public void setDocumentos(List<DocumentSnapshot> nuevosDocumentos) {
        this.listaUsuarios = nuevosDocumentos;
        this.listaUsuariosCompleta = new ArrayList<>(nuevosDocumentos);
        notifyDataSetChanged();
    }

    /**
     * Método que devuelve un filtro para realizar búsquedas en la lista de usuarios.
     *
     * @return Filtro para realizar búsquedas en la lista de usuarios.
     */
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


    /**
     * Método que devuelve el DocumentSnapshot en la posición especificada.
     *
     * @param position La posición del elemento en el conjunto de datos.
     * @return DocumentSnapshot en la posición especificada.
     */
    public DocumentSnapshot getDocumentAtPosition(int position) {
        return listaUsuarios.get(position);
    }

    /**
     * Clase que representa un ViewHolder que contiene las vistas para mostrar información de un usuario.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * ImageView para mostrar el logo o imagen del usuario.
         */
        ImageView logoUsuario;

        /**
         * TextView para mostrar el nombre del usuario.
         */
        TextView nombreUsuario;

        /**
         * TextView para mostrar el correo electrónico del usuario.
         */
        TextView correoUsuario;

        /**
         * Constructor de la clase ViewHolder.
         *
         * @param itemView La vista que contiene las subvistas para mostrar la información del usuario.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logoUsuario = itemView.findViewById(R.id.logoUsuario);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            correoUsuario = itemView.findViewById(R.id.correoUsuario);


        }
    }
}