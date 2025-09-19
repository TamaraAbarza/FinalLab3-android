package com.ulp.eventapp.ui.organizador.evento;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ulp.eventapp.R;
import com.ulp.eventapp.databinding.FragmentAllEventsBinding;
import com.ulp.eventapp.model.Event;

import java.util.ArrayList;
import java.util.List;

public class AllEventsFragment extends Fragment {

    private FragmentAllEventsBinding binding;
    private AllEventsViewModel vm;

    public static AllEventsFragment newInstance() {
        return new AllEventsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(AllEventsViewModel.class);
        binding = FragmentAllEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observador lista
        vm.getListaMutable().observe(getViewLifecycleOwner(), events -> {
            if (events == null || events.isEmpty()) {
                binding.eventList.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.eventList.setVisibility(View.VISIBLE);

                AdapterEvento eventoAdapter = new AdapterEvento(events, getContext(), getLayoutInflater());
                GridLayoutManager glm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                binding.eventList.setLayoutManager(glm);
                binding.eventList.setAdapter(eventoAdapter);
            }
        });

        // Click listeners de filtros
        binding.btnTodos.setOnClickListener(v -> {
            setActiveFilterButton("all");
            vm.setFilterAndReload("all");
        });

        binding.btnPasados.setOnClickListener(v -> {
            setActiveFilterButton("past");
            vm.setFilterAndReload("past");
        });

        binding.btnFuturos.setOnClickListener(v -> {
            setActiveFilterButton("future");
            vm.setFilterAndReload("future");
        });

        // marcar default (Todos) y cargar
        setActiveFilterButton("all");
        vm.setFilterAndReload("all");

        return root;
    }

    private void setActiveFilterButton(String filter) {
        // simple highlighting: selected = purple background and white text, others = default
        int selectedBg = Color.parseColor("#9C27B0"); // morado
        int selectedText = Color.WHITE;
        int defaultBg = Color.TRANSPARENT;
        int defaultText = Color.BLACK;

        binding.btnTodos.setBackgroundColor(defaultBg);
        binding.btnTodos.setTextColor(defaultText);
        binding.btnPasados.setBackgroundColor(defaultBg);
        binding.btnPasados.setTextColor(defaultText);
        binding.btnFuturos.setBackgroundColor(defaultBg);
        binding.btnFuturos.setTextColor(defaultText);

        switch (filter) {
            case "past":
                binding.btnPasados.setBackgroundColor(selectedBg);
                binding.btnPasados.setTextColor(selectedText);
                break;
            case "future":
                binding.btnFuturos.setBackgroundColor(selectedBg);
                binding.btnFuturos.setTextColor(selectedText);
                break;
            default:
                binding.btnTodos.setBackgroundColor(selectedBg);
                binding.btnTodos.setTextColor(selectedText);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}