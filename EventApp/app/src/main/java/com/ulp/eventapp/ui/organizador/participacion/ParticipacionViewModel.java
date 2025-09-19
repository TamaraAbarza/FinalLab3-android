package com.ulp.eventapp.ui.organizador.participacion;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.eventapp.model.ApiResponseMessage;
import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.model.PaginacionResponse;
import com.ulp.eventapp.model.Participation;
import com.ulp.eventapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipacionViewModel extends AndroidViewModel {

    private MutableLiveData<List<Participation>> listaMutable = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Event> mutableEvento = new MutableLiveData<>();

    // paginación
    private int pageNumber = 1;
    private int pageSize = 100;
    private int totalRegistros = 0;

    public ParticipacionViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Event> getMutableEvento() {
        return mutableEvento;
    }

    public LiveData<List<Participation>> getListaMutable() {
        return listaMutable;
    }

    public void recuperarEvento(Bundle bundle){
        Event evento = (Event) bundle.getSerializable("evento");
        if(evento != null){
            Log.d("Salida","Se obtuvo el evento correctamente");
            mutableEvento.setValue(evento);
        } else {
            Log.e("Salida","No se recibió ningún evento en el bundle");
        }
    }
    public void crearLista(){
        Event evento = mutableEvento.getValue();
        if (evento == null) {
            Log.e("Salida", "Error: el evento es nulo en el ViewModel.");
            return;
        }

        int eventoId = evento.getId();
        String token = ApiClient.leerToken(getApplication());

        if (token != null && !token.isEmpty()) {
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();
            Call<PaginacionResponse<Participation>> call =
                    api.getParticipationsByEvent(token, eventoId, pageNumber, pageSize);

            call.enqueue(new Callback<PaginacionResponse<Participation>>() {
                @Override
                public void onResponse(Call<PaginacionResponse<Participation>> call,
                                       Response<PaginacionResponse<Participation>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("Salida", "Se obtuvo correctamente la lista de participaciones");

                        PaginacionResponse<Participation> paginacion = response.body();
                        listaMutable.setValue(paginacion.getDatos());

                        totalRegistros = paginacion.getTotalRegistros();
                        Log.d("Salida", "Total registros: " + totalRegistros);
                    } else {
                        Log.e("Salida", "Error al obtener la lista de participaciones: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<PaginacionResponse<Participation>> call, Throwable t) {
                    Log.e("Salida", "Falla al obtener las participaciones", t);
                }
            });
        } else {
            Log.e("Salida", "Error: token es nulo o vacío");
        }
    }

    public void actualizarParticipacion(int eventId, boolean estado){
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) return;

        ApiClient.MisEndPoints api = ApiClient.getEndPoints();
        Call<ResponseBody> call = api.updateParticipation(token, eventId, estado);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Salida", "Participación actualizada");
                    crearLista(); // refrescar lista
                } else {
                    Log.e("Salida", "Error al actualizar participación: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Salida", "Error en actualizarParticipation", t);
            }
        });
    }

    public void crearParticipacion(int eventId) {
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) return;

        ApiClient.MisEndPoints api = ApiClient.getEndPoints();
        Call<Participation> call = api.createParticipation(token, eventId);

        call.enqueue(new Callback<Participation>() {
            @Override
            public void onResponse(Call<Participation> call, Response<Participation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Salida", "Participación creada");
                    crearLista(); // refrescar lista
                } else {
                    Log.e("Salida", "Error al crear participación: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Participation> call, Throwable t) {
                Log.e("Salida", "Error en createParticipation", t);
            }
        });
    }

    public void eliminarParticipacion(int eventId) {
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) return;

        ApiClient.MisEndPoints api = ApiClient.getEndPoints();
        Call<ApiResponseMessage> call = api.deleteParticipation(token, eventId);

        call.enqueue(new Callback<ApiResponseMessage>() {
            @Override
            public void onResponse(Call<ApiResponseMessage> call, Response<ApiResponseMessage> response) {
                if (response.isSuccessful()) {
                    Log.d("Salida", "Participación eliminada");
                    crearLista();
                } else {
                    Log.e("Salida", "Error al eliminar participación: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseMessage> call, Throwable t) {
                Log.e("Salida", "Error en deleteParticipation", t);
            }
        });
    }
}

