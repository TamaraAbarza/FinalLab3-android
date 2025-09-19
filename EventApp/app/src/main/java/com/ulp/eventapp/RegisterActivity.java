package com.ulp.eventapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ulp.eventapp.databinding.ActivityRegisterBinding;
import com.ulp.eventapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private RegisterActivityViewModel vm;
    private ActivityRegisterBinding binding;

    // 🔹 Variable para guardar el último email registrado
    private String lastRegisteredEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())
                .create(RegisterActivityViewModel.class);

        vm.getmVerificar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                // Cuando el registro fue exitoso, pasamos el email guardado
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("registered_email", lastRegisteredEmail);
                startActivity(intent);
                finish();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etName.getText().toString().trim();
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();
                String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

                // Validar campos vacíos
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que las contraseñas coincidan
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear el objeto usuario
                User user = new User(username, email, password);

                // Registrar
                vm.registrarUsuario(user);

                // 🔹 Guardamos el email antes de limpiar los campos
                lastRegisteredEmail = email;

                // Limpiar campos después del registro
                binding.etName.setText("");
                binding.etEmail.setText("");
                binding.etPassword.setText("");
                binding.etConfirmPassword.setText("");
            }
        });

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar directamente a LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
