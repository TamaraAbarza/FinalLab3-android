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
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> mVerificar;

    public RegisterActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getmVerificar() {
        if(mVerificar == null){
            mVerificar = new MutableLiveData<>();
        }
        return mVerificar;
    }

    public void registrarUsuario(User user) {
        ApiClient.MisEndPoints api = ApiClient.getEndPoints();
        Call<ResponseBody> call = api.registerUser(user.getUsername(), user.getEmail(), user.getPassword());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String mensaje = response.body().string(); // convierte el cuerpo en String
                        Log.d("salida", "Se registró el usuario correctamente: " + mensaje);

                        // para ir a la vista de iniciar sesión después
                        Toast.makeText(getApplication(), "¡Se registró exitosamente al usuario!", Toast.LENGTH_LONG).show();
                        mVerificar.setValue(true);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("salida", "Error leyendo el cuerpo de la respuesta");
                    }
                } else {
                    Log.d("salida", "Error al registrar al usuario: " + response.code());
                    Toast.makeText(getApplication(), "Datos incorrectos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("salida", "Error en la llamada a la API: " + t.getMessage());
                Toast.makeText(getApplication(), "Error desde el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }
    
}

