package com.ulp.eventapp.ui.organizador.usuario;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.eventapp.model.PaginacionResponse;
import com.ulp.eventapp.model.User;
import com.ulp.eventapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioViewModel extends AndroidViewModel {

    private MutableLiveData<List<User>> listaMutable;
    public UsuarioViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<User>> getListaMutable(){
        if(listaMutable==null){
            listaMutable=new MutableLiveData<>();
        }
        return listaMutable;
    }

    public void crearLista(){
        String token = ApiClient.leerToken(getApplication());

        if(token != null && !token.isEmpty()){
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();
            // Pido la primera página (pageNumber = 1) con pageSize = 100 (cambiá si querés otra cantidad)
            Call<PaginacionResponse<User>> call = api.getAllUsers(token, 1, 100);
            call.enqueue(new Callback<PaginacionResponse<User>>() {
                @Override
                public void onResponse(Call<PaginacionResponse<User>> call, Response<PaginacionResponse<User>> response) {
                    if (response.isSuccessful()) {
                        PaginacionResponse<User> pag = response.body();
                        List<User> users = (pag != null && pag.getDatos() != null) ? pag.getDatos() : new ArrayList<>();
                        Log.d("salida", "se obtuvo correctamente la lista de usuarios. total: " + (pag != null ? pag.getTotalRegistros() : "null"));
                        listaMutable.setValue(users);
                    } else {
                        try {
                            String err = response.errorBody() != null ? response.errorBody().string() : "no error body";
                            Log.e("salida", "error al obtener la lista de usuarios code: " + response.code() + " body: " + err);
                        } catch (Exception ex) {
                            Log.e("salida", "error al leer errorBody", ex);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaginacionResponse<User>> call, Throwable t) {
                    Log.e("salida", "Falla al obtener los usuarios", t);
                }
            });
        } else {
            Log.e("salida", "Error: token es nulo o vacío");
        }
    }


}