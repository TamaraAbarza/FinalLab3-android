package com.ulp.eventapp.ui.perfil;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ulp.eventapp.R;
import com.ulp.eventapp.databinding.FragmentPerfilBinding;
import com.ulp.eventapp.model.User;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    public static PerfilFragment newInstance() {
        return new PerfilFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        PerfilViewModel mv = new ViewModelProvider(this).get(PerfilViewModel.class);
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mv.getmUsuario().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                binding.etUsername.setText(user.getUsername());
                binding.etEmail.setText(user.getEmail());
                binding.tvNombre.setText(user.getUsername());
            }
        });

        mv.getmGuardar().observe(getViewLifecycleOwner(), mensaje ->
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show()
        );

        binding.btnGuardar.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String actual = binding.etPasswordActual.getText().toString().trim();
            String nueva = binding.etPasswordNueva.getText().toString().trim();
            String confirmar = binding.etPasswordConfirmar.getText().toString().trim();

            if (!nueva.isEmpty() || !actual.isEmpty() || !confirmar.isEmpty()) {
                if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                    Toast.makeText(getContext(), "Debes completar todos los campos de contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!nueva.equals(confirmar)) {
                    Toast.makeText(getContext(), "La nueva contraseña no coincide", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            mv.actualizarUsuario(username, email, actual, nueva);
        });

        mv.obtenerUsuario();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}