    package com.example.shopytest;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.recyclerview.widget.RecyclerView;

    import com.bumptech.glide.Glide;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.CollectionReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;

    import org.checkerframework.checker.nullness.qual.NonNull;

    import java.util.ArrayList;
    import java.util.List;

    /**
     * El adaptador {@code CarritoAdapter} maneja la presentación de los artículos en el carrito de compras
     * y realiza operaciones como la actualización del precio total y la eliminación de artículos.
     *
     * @author Pablo Pereira
     * @version 1.0
     */
    public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

        /**
         * Lista de documentos que representan los artículos en el carrito.
         */
        private List<DocumentSnapshot> documentos;

        /**
         * Referencia a la actividad Carrito para comunicarse y actualizar el precio total.
         */
        private Carrito carrito;

        /**
         * Lista que almacena los precios totales de cada artículo en el carrito.
         */
        private List<Double> preciosTotales;

        /**
         * Precio total acumulado de todos los artículos en el carrito.
         */
        private double precioTotal = 0.0;

        /**
         * Constructor de la clase CarritoAdapter.
         *
         * @param documentos Lista de documentos que representan los artículos en el carrito.
         * @param carrito    Referencia a la actividad Carrito.
         */
        public CarritoAdapter(List<DocumentSnapshot> documentos, Carrito carrito) {
            this.documentos = documentos;
            this.carrito = carrito;
            this.preciosTotales = new ArrayList<>();
        }

        /**
         * Crea una nueva instancia de ViewHolder inflando el diseño de cada elemento del RecyclerView.
         *
         * @param parent   El ViewGroup al que se añadirá la nueva vista después de ser vinculada a una posición.
         * @param viewType El tipo de vista del nuevo elemento.
         * @return La nueva instancia de ViewHolder.
         */
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prenda_carrito, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Vincula los datos del documento a las vistas en el ViewHolder.
         *
         * @param holder   El ViewHolder que debe ser actualizado para representar el contenido del elemento en la posición dada.
         * @param position La posición del elemento en el conjunto de datos.
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DocumentSnapshot documento = documentos.get(position);

            // Configura tus Views con los datos del documento
            String nombre = documento.getString("nombre");
            String precio = documento.getString("precio");
            String imagen = documento.getString("imagen");





            holder.nombreTextView.setText(nombre);
            holder.precioTextView.setText(precio+" €");
            Glide.with(holder.itemView.getContext())
                    .load(imagen)
                    .into(holder.imagenImageView);

            holder.sumarnumero.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(holder.numerodeprenda > 9){
                        Toast.makeText(holder.itemView.getContext(), "Cantidad de prenda excedida", Toast.LENGTH_SHORT).show();
                    }else{
                        holder.numerodeprenda++;
                        holder.numerodeprendas.setText(String.valueOf(holder.numerodeprenda));

                        double precioTotal = Double.parseDouble(precio) * holder.numerodeprenda;
                        holder.precioTextView.setText(String.valueOf(precioTotal)+" €");


                    }
                }
            });

            holder.restarnumero.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.numerodeprenda < 2){

                    }else{
                        holder.numerodeprenda--;
                        holder.numerodeprendas.setText(String.valueOf(holder.numerodeprenda));


                        double precioTotal = Double.parseDouble(precio) * holder.numerodeprenda;
                        holder.precioTextView.setText(String.valueOf(precioTotal)+" €");




                    }


                }
            });



            holder.eliminarPrenda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Implementa la lógica de eliminación aquí
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Obtén el documento correspondiente a la posición
                        DocumentSnapshot documento = documentos.get(position);

                        // Obtén la referencia a la colección 'prendasCarrito' del usuario actual
                        CollectionReference prendasCarritoRef = FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("prendasCarrito");

                        // Elimina el documento correspondiente de la base de datos
                        prendasCarritoRef.document(documento.getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Eliminación exitosa, actualiza la lista
                                        documentos.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Manejar los errores si la eliminación falla
                                    }
                                });
                    }
                }
            });
        }

        /**
         * Calcula el precio total sumando los precios totales de cada artículo en el carrito.
         */
        public void calcularPrecioTotal() {
            precioTotal = 0.0;
            for (Double precioTotalPrenda : preciosTotales) {
                precioTotal += precioTotalPrenda;
            }
            carrito.setPrecioTotal(precioTotal);
            notifyDataSetChanged();
        }

        /**
         * Obtiene la cantidad de elementos en el conjunto de datos.
         *
         * @return La cantidad de elementos en el conjunto de datos.
         */
        @Override
        public int getItemCount() {
            return documentos.size();
        }

        /**
         * La clase interna {@code ViewHolder} representa las vistas de cada elemento en el RecyclerView.
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            /**
             * Componente de interfaz de usuario que muestra el nombre de la prenda.
             */
            TextView nombreTextView;

            /**
             * Componente de interfaz de usuario que muestra el precio de la prenda.
             */
            TextView precioTextView;

            /**
             * Componente de interfaz de usuario que muestra la cantidad de prendas seleccionadas.
             */
            TextView numerodeprendas;

            /**
             * Componente de interfaz de usuario que muestra la imagen de la prenda.
             */
            ImageView imagenImageView;

            /**
             * Botón de interfaz de usuario para eliminar la prenda del carrito.
             */
            ImageView eliminarPrenda;

            /**
             * Botón de interfaz de usuario para aumentar la cantidad de prendas seleccionadas.
             */
            ImageView sumarnumero;

            /**
             * Botón de interfaz de usuario para disminuir la cantidad de prendas seleccionadas.
             */
            ImageView restarnumero;


            /**
             * Número de prendas seleccionadas.
             */
            private int numerodeprenda = 1;

            /**
             * Constructor de la clase ViewHolder.
             *
             * @param itemView La vista del elemento.
             */
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nombreTextView = itemView.findViewById(R.id.nombreprendacarrito);
                precioTextView = itemView.findViewById(R.id.precioprendacarrito);
                imagenImageView = itemView.findViewById(R.id.prenda);
                eliminarPrenda = itemView.findViewById(R.id.eliminarprenda);
                sumarnumero = itemView.findViewById(R.id.sumarprecio);
                restarnumero = itemView.findViewById(R.id.restarprecio);
                numerodeprendas = itemView.findViewById(R.id.numerodeprendas);

            }
        }

        /**
         * Establece una nueva lista de documentos.
         *
         * @param nuevosDocumentos La nueva lista de documentos.
         */
        public void setDocumentos(List<DocumentSnapshot> nuevosDocumentos) {
            this.documentos = nuevosDocumentos;
        }
    }

