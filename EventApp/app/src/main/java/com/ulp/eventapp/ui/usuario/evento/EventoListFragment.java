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
import com.ulp.eventapp.databinding.FragmentEventoListBinding;
import com.ulp.eventapp.model.Event;

import java.util.List;

public class EventoListFragment extends Fragment {
    private FragmentEventoListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventoListViewModel vm = new ViewModelProvider(this).get(EventoListViewModel.class);
        binding = FragmentEventoListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        vm.getListaMutable().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (events == null || events.isEmpty()) {
                    binding.listaEventos.setVisibility(View.GONE);
                    binding.emptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    binding.listaEventos.setVisibility(View.VISIBLE);

                    EventoAdapter eventoAdapter = new EventoAdapter(events, getContext(), getLayoutInflater());
                    GridLayoutManager glm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                    RecyclerView rc = binding.listaEventos;
                    rc.setLayoutManager(glm);
                    rc.setAdapter(eventoAdapter);
                }
            }
        });
        vm.crearLista();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}