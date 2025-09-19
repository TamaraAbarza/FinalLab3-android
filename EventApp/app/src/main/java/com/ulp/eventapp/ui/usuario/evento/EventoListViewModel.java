package com.ulp.eventapp.ui.usuario.evento;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.model.PaginacionResponse;
import com.ulp.eventapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Event>> listaMutable;
    public EventoListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Event>> getListaMutable(){
        if(listaMutable==null){
            listaMutable=new MutableLiveData<>();
        }
        return listaMutable;
    }

    public void crearLista(){
        String token = ApiClient.leerToken(getApplication());

        if(token != null && !token.isEmpty()){
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();
            Call<PaginacionResponse<Event>> call = api.getEventosProx(token, 1, 10);
            call.enqueue(new Callback<PaginacionResponse<Event>>() {
                @Override
                public void onResponse(Call<PaginacionResponse<Event>> call, Response<PaginacionResponse<Event>> response) {
                    if (response.isSuccessful()) {
                        PaginacionResponse<Event> pag = response.body();
                        List<Event> eventos = (pag != null && pag.getDatos() != null) ? pag.getDatos() : new ArrayList<>();
                        Log.d("salida", "se obtuvo correctamente la lista de eventos. total: " + (pag != null ? pag.getTotalRegistros() : "null"));
                        listaMutable.setValue(eventos);
                    } else {
                        try {
                            String err = response.errorBody() != null ? response.errorBody().string() : "no error body";
                            Log.e("salida", "error al obtener la lista de eventos code: " + response.code() + " body: " + err);
                        } catch (Exception ex) {
                            Log.e("salida", "error al leer errorBody", ex);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaginacionResponse<Event>> call, Throwable t) {
                    Log.e("salida", "Falla al obtener los eventos", t);
                }
            });
        } else {
            Log.e("salida", "Error: token es nulo o vacío");
        }
    }
}