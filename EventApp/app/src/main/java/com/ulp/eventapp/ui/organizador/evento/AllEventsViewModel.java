package com.ulp.eventapp.ui.organizador.evento;

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

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllEventsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Event>> listaMutable;

    // paginación
    private int pageNumber = 1;
    private int pageSize = 100;

    // filtro: "all" | "past" | "future"
    private String filter = "all";

    public AllEventsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Event>> getListaMutable() {
        if (listaMutable == null) {
            listaMutable = new MutableLiveData<>(new ArrayList<>());
        }
        return listaMutable;
    }

    public String getFilter() { return filter; }
    public void setFilterAndReload(String newFilter) {
        if (newFilter == null) newFilter = "all";
        this.filter = newFilter.trim().toLowerCase();
        this.pageNumber = 1;
        listaMutable.setValue(new ArrayList<>());
        crearLista();
    }

    public void crearLista() {
        String token = ApiClient.leerToken(getApplication());

        if (token != null && !token.isEmpty()) {
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();

            String authHeader = token.toLowerCase().startsWith("bearer ") ? token : "Bearer " + token;

            Call<PaginacionResponse<Event>> call = api.getAllEvents(authHeader, pageNumber, pageSize, filter);
            call.enqueue(new Callback<PaginacionResponse<Event>>() {
                @Override
                public void onResponse(Call<PaginacionResponse<Event>> call, Response<PaginacionResponse<Event>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PaginacionResponse<Event> pag = response.body();
                        List<Event> eventos = pag.getDatos() != null ? pag.getDatos() : new ArrayList<>();
                        listaMutable.setValue(eventos);

                        Log.d("salida", "se obtuvo correctamente la lista de eventos (filter=" + filter + ")");
                        Log.d("salida", "Total de registros: " + pag.getTotalRegistros());
                    } else {
                        if (response.code() == 404) {
                            listaMutable.setValue(new ArrayList<>());
                            Log.d("salida", "No hay eventos (404).");
                        } else {
                            Log.e("salida", "Error al obtener la lista de eventos: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaginacionResponse<Event>> call, Throwable t) {
                    Log.e("salida", "falló al obtener los eventos", t);
                }
            });
        } else {
            Log.e("salida", "Error: token es nulo o vacío");
        }
    }
}
