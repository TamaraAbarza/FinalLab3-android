package com.ulp.eventapp.ui.usuario.certificado;

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
import com.ulp.eventapp.databinding.FragmentCertificadoBinding;
import com.ulp.eventapp.model.Event;

import java.util.List;

public class CertificadoFragment extends Fragment {

    private FragmentCertificadoBinding binding;

    public static CertificadoFragment newInstance() {
        return new CertificadoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        CertificadoViewModel certificadoViewModel = new ViewModelProvider(this).get(CertificadoViewModel.class);
        binding = FragmentCertificadoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        certificadoViewModel.getListaMutable().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (events == null || events.isEmpty()) {
                    binding.listaCertificados.setVisibility(View.GONE);
                    binding.emptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    binding.listaCertificados.setVisibility(View.VISIBLE);

                    CertificadoAdapter certificadoAdapter = new CertificadoAdapter(events, getContext(), getLayoutInflater());
                    GridLayoutManager glm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                    RecyclerView rc = binding.listaCertificados;
                    rc.setLayoutManager(glm);
                    rc.setAdapter(certificadoAdapter);
                }
            }
        });

        certificadoViewModel.crearLista();
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}