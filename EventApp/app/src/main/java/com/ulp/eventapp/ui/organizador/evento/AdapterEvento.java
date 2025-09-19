package com.ulp.eventapp.ui.organizador.evento;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.ulp.eventapp.R;
import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.request.ApiClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterEvento extends RecyclerView.Adapter<AdapterEvento.ViewHolderVH> {

    private List<Event> listaDeEventos;
    private Context context;
    private LayoutInflater li;

    public AdapterEvento(List<Event> listaDeEventos, Context context, LayoutInflater li) {
        this.listaDeEventos = listaDeEventos;
        this.context = context;
        this.li = li;
    }

    @NonNull
    @Override
    public ViewHolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.item_evento_o, parent, false);
        return new ViewHolderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVH holder, int position) {
        Event evento = listaDeEventos.get(position);

        holder.nombre.setText(evento.getName());
        String fechaFormateada = formatDate(evento.getDate());
        holder.fecha.setText(fechaFormateada);
        String imageUrl = ApiClient.URL + evento.getImageUrl();

        // Usar Glide para cargar la imagen
        Glide.with(context)
                .load(imageUrl)
                //.placeholder(R.drawable.placeholder) // Imagen de placeholder mientras se carga la imagen
                .error(R.drawable.imagen) // Imagen de error si la carga falla
                .into(holder.imagen);

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = ApiClient.leerToken(context);
                if (token != null && !token.isEmpty()) {

                    ApiClient.MisEndPoints api = ApiClient.getEndPoints();
                    Call<ResponseBody> call = api.deleteEvento(token, evento.getId());

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    String bodyText = null;
                                    if (response.body() != null) {
                                        bodyText = response.body().string();
                                    }

                                    if (bodyText != null && !bodyText.isEmpty()) {
                                        Log.d("salida", "se eliminó correctamente el evento: " + bodyText);
                                        Toast.makeText(context, bodyText, Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.d("salida", "Evento eliminado correctamente (sin cuerpo). Código: " + response.code());
                                        Toast.makeText(context, "¡Se eliminó exitosamente el evento!", Toast.LENGTH_LONG).show();
                                    }

                                    listaDeEventos.remove(evento);
                                    notifyDataSetChanged();

                                } catch (IOException e) {
                                    Log.e("salida", "Error leyendo cuerpo de respuesta", e);
                                    Toast.makeText(context, "Evento eliminado (error leyendo mensaje).", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e("salida", "error al eliminar el evento. code=" + response.code());
                                Toast.makeText(context, "¡Ocurrió un error al eliminar el evento!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("salida", "falló al eliminar el evento", t);
                            Toast.makeText(context, "Error en la conexión con el servidor", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Log.e("salida", "Error: token es nulo o vacío");
                    Toast.makeText(context, "Error desde el servidor", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("evento", evento);
                Navigation.findNavController(v).navigate(R.id.nav_crear_evento, bundle);
            }
        });

        holder.btnInscripciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("evento", evento);
                Navigation.findNavController(v).navigate(R.id.nav_participacion, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (listaDeEventos != null) ? listaDeEventos.size() : 0;
    }

    public static class ViewHolderVH extends RecyclerView.ViewHolder {
        TextView nombre, fecha;
        Button btnModificar, btnEliminar, btnInscripciones;
        ImageView imagen;

        public ViewHolderVH(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombreE);
            fecha = itemView.findViewById(R.id.tvFechaE);
            imagen = itemView.findViewById(R.id.ivFotoE);
            btnInscripciones = itemView.findViewById(R.id.btnInscripcionesE);
            btnModificar = itemView.findViewById(R.id.btnModificarE);
            btnEliminar = itemView.findViewById(R.id.btnEliminarE);
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm 'hs'", Locale.getDefault());
            return outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }
}


