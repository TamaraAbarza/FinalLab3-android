package com.ulp.eventapp.ui.organizador.participacion;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ulp.eventapp.databinding.FragmentParticipacionBinding;
import com.ulp.eventapp.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ParticipacionFragment extends Fragment {

    private FragmentParticipacionBinding binding;
    private ParticipacionViewModel vm;
    private Event evento;
    private boolean switchesHabilitados; // 👈 flag global

    public static ParticipacionFragment newInstance() {
        return new ParticipacionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        vm = new ViewModelProvider(this).get(ParticipacionViewModel.class);
        binding = FragmentParticipacionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle bundle = getArguments();
        if (bundle != null) {
            vm.recuperarEvento(bundle);
        }

        vm.getMutableEvento().observe(getViewLifecycleOwner(), e -> {
            if (e != null) {
                evento = e;
                binding.textView2.setText(evento.getName());

                Date hoy = new Date();
                Date fechaEvento = parsearFechaEvento(evento.getDate());

                if (fechaEvento != null && fechaEvento.after(hoy)) {
                    // Evento en el FUTURO → switches deshabilitados
                    switchesHabilitados = false;
                    binding.textView3.setText("La asistencia podrá confirmarse posteriormente a la fecha del evento");
                    binding.textView3.setTextColor(Color.GRAY);
                } else {
                    // Evento en el pasado o presente → switches habilitados
                    switchesHabilitados = true;
                    binding.textView3.setText("Confirmar asistencia");
                    binding.textView3.setTextColor(Color.parseColor("#757575"));
                }

                vm.crearLista();
            }
        });

        vm.getListaMutable().observe(getViewLifecycleOwner(), participations -> {
            if (participations == null || participations.isEmpty()) {
                binding.listaParticipacion.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.listaParticipacion.setVisibility(View.VISIBLE);

                ParticipacionAdapter adapter = new ParticipacionAdapter(
                        participations,
                        getLayoutInflater(),
                        (participation, isChecked) -> {
                            int participationId = participation.getId();
                            vm.actualizarParticipacion(participationId, isChecked);
                        },
                        switchesHabilitados // 👈 le pasamos la bandera
                );

                binding.listaParticipacion.setLayoutManager(
                        new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false)
                );
                binding.listaParticipacion.setAdapter(adapter);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static Date parsearFechaEvento(String fechaEvento) {
        if (fechaEvento == null) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            return sdf.parse(fechaEvento);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}


