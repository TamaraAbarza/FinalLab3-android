package com.ulp.eventapp.ui.usuario.evento;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ulp.eventapp.R;
import com.ulp.eventapp.databinding.FragmentEventoBinding;
import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.request.ApiClient;

public class EventoFragment extends Fragment {

    private FragmentEventoBinding binding;
    private Event evento = new Event();

    public static EventoFragment newInstance() {
        return new EventoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventoViewModel vm = new ViewModelProvider(this).get(EventoViewModel.class);
        binding = FragmentEventoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        vm.getMutableEvento().observe(getViewLifecycleOwner(), new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                evento = event;

                TextView nombre = binding.tvNombreEvento;
                TextView fecha = binding.tvFechaEvento;
                TextView ubicacion = binding.tvUbicacionEvento;
                TextView descripcion = binding.tvDescripcionEvento;
                ImageView foto = binding.ivEventoFoto;

                nombre.setText(event.getName());
                fecha.setText(event.getDate());
                ubicacion.setText(event.getLocation());
                descripcion.setText(event.getDescription());
                //modificar

                String imageUrl= ApiClient.URL+event.getImageUrl();
                // Usa Glide para cargar la imagen
                Glide.with(EventoFragment.this)
                        .load(imageUrl)
                        //.placeholder(R.drawable.placeholder) // Imagen de placeholder mientras se carga la imagen
                        .error(R.drawable.ic_error) // Imagen de error si la carga falla
                        .into(foto);

            }
        });

        // actualizar el texto del botón
        vm.getmAsistir().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isParticipating) {
                if (isParticipating != null) {
                    if (isParticipating) {
                        binding.btnAsistir.setText("Cancelar participación");
                    } else {
                        binding.btnAsistir.setText("Asistir a evento");
                    }
                }
            }
        });

        //BOTON PARA ASISTIR/CANCELAR EVENTO

        binding.btnAsistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.asistirEvento(evento);
            }
        });

        Bundle bundle = getArguments();
        vm.recuperarEvento(bundle);
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}
