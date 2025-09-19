package com.ulp.eventapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ulp.eventapp.databinding.ActivityLoginBinding;
import com.ulp.eventapp.model.TokenCallback;
import com.ulp.eventapp.request.ApiClient;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel vm;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recuperar el email si viene desde RegisterActivity
        String registeredEmail = getIntent().getStringExtra("registered_email");
        if (registeredEmail != null && !registeredEmail.isEmpty()) {
            binding.etEmail.setText(registeredEmail);
            binding.etPassword.requestFocus(); // foco directo en contraseña
        }

        // Verificamos el token luego de inicializar la vista
        ApiClient.tokenValido(getApplicationContext(), new TokenCallback() {
            @Override
            public void onResult(boolean valido) {
                Log.d("token_check", "¿Token válido? " + valido);
                if (valido) {
                    Log.d("token_check", "Redirigiendo al MainActivity...");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("token_check", "Token inválido, quedando en LoginActivity");
                }
            }
        });

        vm = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())
                .create(LoginActivityViewModel.class);

        vm.getmVerificar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                vm.login(email, password);

                binding.etEmail.setText("");
                binding.etPassword.setText("");
            }
        });

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}
