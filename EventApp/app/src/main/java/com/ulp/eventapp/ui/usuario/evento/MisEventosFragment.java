package com.ulp.eventapp.ui.usuario.evento;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ulp.eventapp.R;
import com.ulp.eventapp.databinding.FragmentMisEventosBinding;
import com.ulp.eventapp.model.Event;

import java.util.List;

public class MisEventosFragment extends Fragment {

    private FragmentMisEventosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MisEventosViewModel vm = new ViewModelProvider(this).get(MisEventosViewModel.class);
        binding = FragmentMisEventosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        vm.getListaMutable().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (events == null || events.isEmpty()) {
                    // 🔹 Mostrar estado vacío
                    binding.listaEvento2.setVisibility(View.GONE);
                    binding.emptyState.setVisibility(View.VISIBLE);
                } else {
                    // 🔹 Mostrar lista
                    binding.emptyState.setVisibility(View.GONE);
                    binding.listaEvento2.setVisibility(View.VISIBLE);

                    EventoAdapter eventoAdapter = new EventoAdapter(
                            events,
                            getContext(),
                            getLayoutInflater()
                    );

                    GridLayoutManager glm = new GridLayoutManager(
                            getContext(),
                            1,
                            GridLayoutManager.VERTICAL,
                            false
                    );

                    RecyclerView rc = binding.listaEvento2;
                    rc.setLayoutManager(glm);
                    rc.setAdapter(eventoAdapter);
                }
            }
        });

        // 🔹 Pedimos la lista al ViewModel
        vm.crearLista();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}