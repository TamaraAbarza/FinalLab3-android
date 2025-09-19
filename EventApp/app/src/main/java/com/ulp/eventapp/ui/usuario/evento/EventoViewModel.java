package com.ulp.eventapp.ui.usuario.evento;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.eventapp.model.ApiResponseMessage;
import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.model.Participation;
import com.ulp.eventapp.request.ApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoViewModel extends AndroidViewModel {
    private MutableLiveData<Event> mutableEvento;
    private MutableLiveData<Boolean> mAsistir; //mutable para asistir/cancelar asistencia a evento

    public EventoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Event> getMutableEvento() {
        if(mutableEvento == null){
            mutableEvento = new MutableLiveData<>();
        }
        return mutableEvento;
    }

    public LiveData<Boolean> getmAsistir() {
        if(mAsistir == null){
            mAsistir = new MutableLiveData<>();
        }
        return mAsistir;
    }

    public void recuperarEvento(Bundle bundle){
        Event evento = (Event) bundle.getSerializable("evento");
        if(evento!= null){
            Log.d("Salida","Se obtuvo el evento correctamente");
            mutableEvento.setValue(evento);
            // Establece el estado de asistencia inicial del evento cargado
            mAsistir.setValue(evento.isParticipating());
        }
    }

    //CONFIRMAR - CANCELAR
    public void asistirEvento(Event evento){
        String token = ApiClient.leerToken(getApplication());

        if(token != null && !token.isEmpty()){
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();

            if(!evento.isParticipating()){ // Si NO está participando, va a ASISTIR
                Call<Participation> call = api.createParticipation(token,evento.getId());
                call.enqueue(new Callback<Participation>() {
                    @Override
                    public void onResponse(Call<Participation> call, Response<Participation> response) {
                        if (response.isSuccessful()) {
                            Log.d("salida", "Se registró correctamente la participación");
                            // --- ¡IMPORTANTE! Actualizar el estado de asistencia ---
                            mAsistir.setValue(true);
                            evento.setParticipating(true);
                            mutableEvento.setValue(evento);
                            // --- MOSTRAR TOAST DE ÉXITO ---
                            Toast.makeText(getApplication(), "¡Te has inscrito al evento!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("salida", "Error al registrar la participación: " + response.code());
                            // --- MOSTRAR TOAST DE ERROR ---
                            String errorMessage = "Error al inscribirte al evento.";
                            try {
                                if (response.errorBody() != null) {
                                    // Intenta leer el cuerpo del error si tu API devuelve mensajes de error JSON
                                    errorMessage += " " + response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e("salida", "Error leyendo errorBody", e);
                            }
                            Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Participation> call, Throwable t) {
                        Log.e("salida", "Desde evento: Falla al registrar participación", t);
                        // --- MOSTRAR TOAST DE FALLO DE CONEXIÓN ---
                        Toast.makeText(getApplication(), "Fallo de conexión. Intenta de nuevo.", Toast.LENGTH_LONG).show();
                    }
                });

            }else{ // Si SÍ está participando, va a CANCELAR su participación
                Call<ApiResponseMessage> call = api.deleteParticipation(token, evento.getId());
                call.enqueue(new Callback<ApiResponseMessage>() {
                    @Override
                    public void onResponse(Call<ApiResponseMessage> call, Response<ApiResponseMessage> response) {
                        if (response.isSuccessful()) {
                            Log.d("salida", "Se eliminó correctamente la participación");
                            // --- ¡IMPORTANTE! Actualizar el estado de asistencia ---
                            mAsistir.setValue(false);
                            evento.setParticipating(false);
                            mutableEvento.setValue(evento);
                            // --- MOSTRAR TOAST DE ÉXITO ---
                            Toast.makeText(getApplication(), "Participación cancelada.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("salida", "Error al eliminar la participación: " + response.code());
                            // --- MOSTRAR TOAST DE ERROR ---
                            String errorMessage = "Error al cancelar la participación.";
                            try {
                                if (response.errorBody() != null) {
                                    // Asumiendo que response.errorBody() podría contener un mensaje útil
                                    // Asegúrate de parsear esto si tu API devuelve JSON en errores
                                    errorMessage += " " + response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e("salida", "Error leyendo errorBody", e);
                            }
                            Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseMessage> call, Throwable t) {
                        Log.e("salida", "Desde evento: Falla al eliminar participación", t);
                        // --- MOSTRAR TOAST DE FALLO DE CONEXIÓN ---
                        Toast.makeText(getApplication(), "Fallo de conexión. Intenta de nuevo.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else {
            Log.e("salida", "Desde evento: Error: token es nulo o vacío");
            Toast.makeText(getApplication(), "Error: sesión no válida. Por favor, inicia sesión de nuevo.", Toast.LENGTH_LONG).show();
        }
    }
}