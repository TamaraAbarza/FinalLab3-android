package com.ulp.eventapp.ui.perfil;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.eventapp.model.User;
import com.ulp.eventapp.request.ApiClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
    private MutableLiveData<User> mUsuario;
    private MutableLiveData<String> mGuardar;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<User> getmUsuario() {
        if(mUsuario == null){
            mUsuario = new MutableLiveData<>();
        }
        return mUsuario;
    }
    public LiveData<String> getmGuardar() {
        if(mGuardar == null){
            mGuardar = new MutableLiveData<>();
        }
        return mGuardar;
    }

    public void obtenerUsuario(){
        String token = ApiClient.leerToken(getApplication());

        if (token != null && !token.isEmpty()) {
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();
            Call<User> call = api.getCurrentUser(token);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Log.d("salida", "desde obtenerUsuario: Usuario obtenido correctamente");
                        mUsuario.setValue(response.body());
                    } else {
                        Log.e("salida", "desde obtenerUsuario: Error al obtener Usuario: " + response.code());

                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                    Log.e("salida", "desde  obtenerUsuario Falla al obtener Usuario", t);
                }
            });
        } else {
            Log.e("salida", " desde  obtenerUsuario Error: token es nulo o vacío");
        }
    }

    public void actualizarUsuario(String username, String email, String currentPassword, String newPassword) {
        String token = ApiClient.leerToken(getApplication());
        ApiClient.MisEndPoints api = ApiClient.getEndPoints();

        api.updateUser(token, username, email, currentPassword, newPassword)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            mGuardar.setValue("Perfil guardado");
                            obtenerUsuario();
                        } else {
                            String msg;
                            try {
                                msg = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "Error desconocido";
                            } catch (IOException e) {
                                msg = "Error leyendo el mensaje";
                            }
                            mGuardar.setValue(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mGuardar.setValue("Falla de conexión");
                    }
                });
    }

}