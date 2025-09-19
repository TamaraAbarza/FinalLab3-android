package com.ulp.eventapp.ui.usuario.evento;

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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.ViewHolderVH> {

    private List<Event> listaDeEventos;
    private Context context;
    private LayoutInflater li;

    public EventoAdapter(List<Event> listaDeEventos, Context context, LayoutInflater li) {
        this.listaDeEventos = listaDeEventos;
        this.context = context;
        this.li = li;
    }

    @NonNull
    @Override
    public EventoAdapter.ViewHolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.item_evento, parent, false);
        return new EventoAdapter.ViewHolderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoAdapter.ViewHolderVH holder, int position) {
        Event evento = listaDeEventos.get(position);

        holder.nombre.setText(evento.getName());
        holder.fecha.setText(evento.getDate());
        String imageUrl = ApiClient.URL + evento.getImageUrl();

        Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.imagen)
                .into(holder.imagen);

        holder.btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Envío el evento serializado y además envío explícitamente
                // la bandera isParticipating para evitar pérdidas por serialización.
                Bundle bundle = new Bundle();
                bundle.putSerializable("evento", evento);
                bundle.putBoolean("isParticipating", evento.isParticipating());
                // también podés enviar eventId si preferís consultar backend en detalle:
                bundle.putInt("eventId", evento.getId());

                Navigation.findNavController(v).navigate(R.id.eventoFragment, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (listaDeEventos != null) ? listaDeEventos.size() : 0;
    }

    public static class ViewHolderVH extends RecyclerView.ViewHolder {
        TextView nombre, fecha;
        Button btnDetalles;
        ImageView imagen;

        public ViewHolderVH(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvTitle);
            fecha = itemView.findViewById(R.id.tvDate);
            btnDetalles = itemView.findViewById(R.id.btnDetails);
            imagen = itemView.findViewById(R.id.ivEvent);
        }
    }
}

