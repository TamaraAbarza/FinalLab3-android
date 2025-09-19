package com.ulp.eventapp.ui.organizador.evento;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.model.RealPathUtil;
import com.ulp.eventapp.request.ApiClient;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import retrofit2.Call;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class CrearEventoViewModel extends AndroidViewModel {

    private MutableLiveData<Uri> uriMutableLiveData;
    private MutableLiveData<Boolean> mVerificar;
    private MutableLiveData<Event> mutableEvento;
    public CrearEventoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Uri> getUriMutable(){

        if(uriMutableLiveData==null){
            uriMutableLiveData=new MutableLiveData<>();
        }
        return uriMutableLiveData;
    }

    public LiveData<Event> getMutableEvento() {
        if (mutableEvento == null) {
            mutableEvento = new MutableLiveData<>();
        }
        return mutableEvento;
    }

    public LiveData<Boolean> getmVerificar() {
        if (mVerificar == null) {
            mVerificar = new MutableLiveData<>();
        }
        return mVerificar;
    }

    public void recibirFoto(ActivityResult result) {
        if(result.getResultCode() == RESULT_OK){
            Intent data=result.getData();
            Uri uri=data.getData();
            uriMutableLiveData.setValue(uri);
        }
    }

    public void registrarEvento(Event evento,Uri uriFoto){
        String token = ApiClient.leerToken(getApplication());

        if (token != null && !token.isEmpty()) {
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();

            String rutaArchivo = RealPathUtil.getRealPath(getApplication(), uriFoto);
            File archivo = new File(rutaArchivo);
            RequestBody imagen = RequestBody.create(MediaType.parse("multipart/form-data"), archivo);
            MultipartBody.Part imagenFile = MultipartBody.Part.createFormData("imagenFile", archivo.getName(), imagen);

            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), evento.getName());
            RequestBody date = RequestBody.create(MediaType.parse("text/plain"), evento.getDate());
            RequestBody location = RequestBody.create(MediaType.parse("text/plain"), evento.getLocation());
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), evento.getDescription());

            Call<ResponseBody> call = api.createEvent(token,name, date, location, description, imagenFile);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("salida", "se realizo el post correctamnete");
                        Toast.makeText(getApplication(),"Se creo correctamente el evento", Toast.LENGTH_LONG).show();

                    } else {
                        Log.e("salida", "error al realizar el post del evento " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("salida", "desde post de evento Falla", t);
                }
            });

        }else {
        Log.e("salida", " desde post de evento Error: token es nulo o vacío");
        }
    }

    public void actualizarEvento(int id, Event evento, Uri uriFoto) {
        String token = ApiClient.leerToken(getApplication());

        if (token != null && !token.isEmpty()) {
            ApiClient.MisEndPoints api = ApiClient.getEndPoints();

            MultipartBody.Part imagenFile = null;
            if (uriFoto != null) {
                String rutaArchivo = RealPathUtil.getRealPath(getApplication(), uriFoto);
                File archivo = new File(rutaArchivo);
                RequestBody imagen = RequestBody.create(MediaType.parse("multipart/form-data"), archivo);
                imagenFile = MultipartBody.Part.createFormData("imagenFile", archivo.getName(), imagen);
            }

            // Campos de texto como RequestBody
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), evento.getName());
            RequestBody date = RequestBody.create(MediaType.parse("text/plain"), evento.getDate());
            RequestBody location = RequestBody.create(MediaType.parse("text/plain"), evento.getLocation());
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), evento.getDescription());

            // Llamada al endpoint de actualización (asegurate que en tu ApiClient esté definido así)
            Call<ResponseBody> call = api.updateEvent(token, id, name, date, location, description, imagenFile);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("salida", "HTTP code actualizar: " + response.code());
                    try {
                        String body = response.body() != null ? response.body().string() : "(empty)";
                        Log.d("salida", "Body actualizar: " + body);
                    } catch (IOException e) {
                        Log.e("salida", "Error leyendo body actualizar: " + e.getMessage());
                    }

                    if (response.isSuccessful()) {
                        if (mVerificar == null) mVerificar = new MutableLiveData<>();
                        mVerificar.postValue(true);
                        Toast.makeText(getApplication(), "Evento actualizado correctamente", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : null;
                            Log.d("salida", "errorBody actualizar: " + error);
                        } catch (IOException ignored) {}
                        Toast.makeText(getApplication(), "Error desde el servidor: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("salida", "onFailure actualizar: " + t.getMessage());
                    Toast.makeText(getApplication(), "Error en la llamada actualizar", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.e("salida", "actualizarEvento Error: token es nulo o vacío");
        }
    }

    // --- Recuperar evento desde Bundle
    public void recuperarEvento(Bundle bundle){
        if (bundle == null) return;
        Object obj = bundle.get("evento");
        if (obj instanceof Event) {
            Event evento = (Event) obj;
            if (mutableEvento == null) {
                mutableEvento = new MutableLiveData<>();
            }
            mutableEvento.postValue(evento);
        } else {
            Log.e("CrearEventoVM", "recuperarEvento: bundle no contiene Event o es null");
        }
    }
}
