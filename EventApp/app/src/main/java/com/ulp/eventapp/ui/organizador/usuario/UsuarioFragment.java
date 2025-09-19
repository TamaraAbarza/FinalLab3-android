package com.ulp.eventapp.ui.organizador.usuario;

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
import com.ulp.eventapp.databinding.FragmentUsuarioBinding;
import com.ulp.eventapp.model.User;

import java.util.List;

public class UsuarioFragment extends Fragment {

    private FragmentUsuarioBinding binding;

    public static UsuarioFragment newInstance() {
        return new UsuarioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        UsuarioViewModel vm = new ViewModelProvider(this).get(UsuarioViewModel.class);
        binding = FragmentUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        vm.getListaMutable().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users == null || users.isEmpty()) {
                    binding.listaUsuarios.setVisibility(View.GONE);
                    binding.emptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    binding.listaUsuarios.setVisibility(View.VISIBLE);

                    UserAdapter userAdapter = new UserAdapter(users, getContext(), getLayoutInflater());
                    GridLayoutManager glm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                    RecyclerView rc = binding.listaUsuarios;
                    rc.setLayoutManager(glm);
                    rc.setAdapter(userAdapter);
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