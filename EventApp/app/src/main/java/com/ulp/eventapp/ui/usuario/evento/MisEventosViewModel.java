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

public class MisEventosViewModel extends AndroidViewModel {

    private MutableLiveData<List<Event>> listaMutable;

    //paginación
    private int pageNumber = 1;
    private int pageSize = 100;

    public MisEventosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Event>> getListaMutable(){
        if(listaMutable == null){
            listaMutable = new MutableLiveData<>(new ArrayList<>());
        }
        return listaMutable;
    }

    public void crearLista(){
        String token = ApiClient.leerToken(getApplication());

        if(token != null && !token.isEmpty()){
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();

            Call<PaginacionResponse<Event>> call = api.getUpcomingParticipations(token, pageNumber, pageSize);
            call.enqueue(new Callback<PaginacionResponse<Event>>() {
                @Override
                public void onResponse(Call<PaginacionResponse<Event>> call, Response<PaginacionResponse<Event>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("salida", "se obtuvo correctamente la lista de eventos inscriptos (paginado)");

                        PaginacionResponse<Event> pag = response.body();
                        List<Event> eventos = pag.getDatos() != null ? pag.getDatos() : new ArrayList<>();

                        for (Event e : eventos) {
                            e.setParticipating(true);
                        }

                        listaMutable.setValue(eventos);
                        Log.d("salida", "Total registros upcoming: " + pag.getTotalRegistros());
                    } else {
                        if (response.code() == 404) {
                            listaMutable.setValue(new ArrayList<>());
                            Log.d("salida", "No hay eventos inscriptos (404).");
                        } else {
                            Log.e("salida", "error al obtener la lista de eventos inscriptos: " + response.code());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e("salida", "errorBody: " + response.errorBody().string());
                                }
                            } catch (Exception ex) {
                                Log.e("salida", "error leyendo errorBody", ex);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaginacionResponse<Event>> call, Throwable t) {
                    Log.e("salida", "Falla al obtener los eventos inscriptos", t);
                }
            });
        } else {
            Log.e("salida", "Error: token es nulo o vacío");
        }
    }

    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
}