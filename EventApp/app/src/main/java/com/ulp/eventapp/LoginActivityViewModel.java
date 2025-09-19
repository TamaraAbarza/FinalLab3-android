package com.ulp.eventapp;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ulp.eventapp.model.User;
import com.ulp.eventapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> mVerificar;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getmVerificar() {
        if (mVerificar == null) {
            mVerificar = new MutableLiveData<>();
        }
        return mVerificar;
    }

    public void login(String email, String password) {
        ApiClient.MisEndPoints api = ApiClient.getEndPoints();
        Call<String> call = api.login(email, password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("salida", response.body());
                    Log.d("salida", "Se inició sesión correctamente");

                    String token = "Bearer " + response.body();
                    ApiClient.guardarToken(getApplication(), token);

                    ApiClient.MisEndPoints api2 = ApiClient.getEndPoints();
                    Call<User> callUser = api2.getCurrentUser(token);
                    callUser.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                User user = response.body();
                                ApiClient.guardarRol(getApplication(), user.getRole());
                                Log.d("salida", "Rol guardado: " + user.getRole());
                            } else {
                                Log.d("salida", "No se pudo obtener el rol del usuario");
                            }
                            mVerificar.setValue(true);
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d("salida", "Error al obtener usuario: " + t.getMessage());
                            mVerificar.setValue(true);
                        }
                    });

                } else {
                    Log.d("salida", "Error al iniciar sesion");
                    Toast.makeText(getApplication(), "Email o contraseña incorrectos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("salida", "Error en la llamada a la API: " + t.getMessage());
                Toast.makeText(getApplication(), "Error desde el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }
}

