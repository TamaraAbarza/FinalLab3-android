package com.ulp.eventapp.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ulp.eventapp.model.ApiResponseMessage;
import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.model.PaginacionResponse;
import com.ulp.eventapp.model.Participation;
import com.ulp.eventapp.model.TokenCallback;
import com.ulp.eventapp.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public class ApiClient {
    public static final String URL =  "http://192.168.0.7:5000/"; //, "http://192.168.1.106:5000/"
    private static MisEndPoints mep;
    public static MisEndPoints getEndPoints(){
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mep = retrofit.create(MisEndPoints.class);
        return mep;
    };

    public interface MisEndPoints{
        @FormUrlEncoded

        @POST("api/auth/login/")
        Call<String> login(@Field("Email") String u, @Field("Password") String p);

        @FormUrlEncoded
        @POST("api/auth/register")
        Call<ResponseBody> registerUser(
                @Field("Username") String username,
                @Field("Email") String email,
                @Field("PasswordHash") String password
        );

        // RECUPERAR CONTRASEÑA
        @FormUrlEncoded
        @POST("api/auth/email")
        Call<Void> enviarEmail(@Field("email") String email);


        //EVENTOS ------------------------------------------------------------


        //Obtener eventos próximos
        /*
        @GET("api/event/proximos")
        Call<List<Event>> getEventosProx(@Header("Authorization") String token);
         */
        @GET("api/event/proximos")
        Call<PaginacionResponse<Event>> getEventosProx(
                @Header("Authorization") String token,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize
        );
        //PARTICIPACION --------------------------------------------------------

        // Crear participación
        @POST("api/participation/create/{eventId}")
        Call<Participation> createParticipation(@Header("Authorization") String token,
                                                @Path("eventId") int eventId);

        // Eliminar participación por ID de Evento
        @DELETE("api/participation/delete-by-event/{eventId}")
        Call<ApiResponseMessage> deleteParticipation(@Header("Authorization") String token,
                                                     @Path("eventId") int eventId);

        // proximos eventos a los que esta inscripto el usuario

        /*
        @GET("api/participation/upcoming")
        Call<List<Event>> getUpcomingParticipations(@Header("Authorization") String token);
        */
        @GET("api/participation/upcoming")
        Call<PaginacionResponse<Event>> getUpcomingParticipations(
                @Header("Authorization") String token,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize
        );

        // get eventos confirmados- para lista de certificados
        /*
        @GET("api/participation/user")
        Call<List<Event>> getUserParticipations(@Header("Authorization") String token);
         */

        @GET("api/participation/user")
        Call<PaginacionResponse<Event>> getUserParticipations(
                @Header("Authorization") String token,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize
        );

        //USUARIO -------------------------------------
        // GET USUARIO LOGUEADO
        @GET("api/user")
        Call<User> getCurrentUser(@Header("Authorization") String token);
        @FormUrlEncoded
        @PUT("api/user/update")
        Call<ResponseBody> updateUser(
                @Header("Authorization") String token,
                @Field("Username") String username,
                @Field("Email") String email,
                @Field("CurrentPassword") String currentPassword,
                @Field("NewPassword") String newPassword
        );

        //Descargar certificado

         @GET("api/participation/certificate/{id}")
             @Streaming
             Call<ResponseBody> downloadCertificate(@Header("Authorization") String token, @Path("id") int id);

        //organizador
        //lista eventos

        /*
        @GET("api/event/all")
        Call<List<Event>> getAllEvents(@Header("Authorization") String token);

         */

        @GET("api/event/all")
        Call<PaginacionResponse<Event>> getAllEvents(
                @Header("Authorization") String token,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize,
                @Query("filter") String filter // "all", "past", "future"
        );

        //lista participacion x evento
        /*
        @GET("api/participation/event/{id}")
        Call<List<Participation>> getParticipationsByEvent(
                @Header("Authorization") String token,
                @Path("id") int id
        );

         */

        @GET("api/participation/event/{id}")
        Call<PaginacionResponse<Participation>> getParticipationsByEvent(
                @Header("Authorization") String token,
                @Path("id") int id,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize
        );

        //actualizar estado participacion
        @PUT("api/participation/confirm/{id}")
        Call<ResponseBody> updateParticipation(
                @Header("Authorization") String token,
                @Path("id") int participationId,
                @Body Boolean isConfirmed
        );

        //crear evento
        @Multipart
        @POST("api/event/create")
        Call<ResponseBody> createEvent(@Header("Authorization") String token,
                                    @Part("Name") RequestBody name,
                                    @Part("Date") RequestBody date,
                                    @Part("Location") RequestBody location,
                                    @Part("Description") RequestBody description,
                                    @Part MultipartBody.Part imagenFile
        );

        //modificar evento
        @Multipart
        @PUT("api/event/update/{id}")
        Call<ResponseBody> updateEvent(
                @Header("Authorization") String token,
                @Path("id") int id,
                @Part("name") RequestBody name,
                @Part("date") RequestBody date,
                @Part("location") RequestBody location,
                @Part("description") RequestBody description,
                @Part MultipartBody.Part imagenFile
        );

        //eliminar evento
        @DELETE("api/event/delete/{eventId}")
        Call<ResponseBody> deleteEvento(
                @Header("Authorization") String token,
                @Path("eventId") int eventId
        );

        //lista usuarios

        @GET("api/user/all")
        Call<PaginacionResponse<User>> getAllUsers(
                @Header("Authorization") String token,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize
        );

        //cambiar rol usuario
        @FormUrlEncoded
        @PUT("api/user/role/{id}")
        Call<ResponseBody> updateUserRole(
                @Path("id") int userId,
                @Header("Authorization") String token,
                @Field("newRole") int newRoleValue
        );

        //eliminar evento
        @DELETE("api/user/delete/{Id}")
        Call<ResponseBody> deleteUser(
                @Header("Authorization") String token,
                @Path("eventId") int eventId
        );
    }

    //Token
    public static void guardarToken(Context context, String token){
        SharedPreferences sp=context.getSharedPreferences("token.xml",0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("token", token);
        editor.apply();
        Log.d("salida","Token guardado correctamente");
    }

    public static String leerToken(Context context){
        SharedPreferences sp=context.getSharedPreferences("token.xml",0);
        String token = sp.getString("token", "");
        return token;
    }

    public static void logout(Context context){
        SharedPreferences sp=context.getSharedPreferences("token.xml",0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("token", "");
        editor.apply();
        Log.d("salida","Se deslogueo correctamente");
    }
    public static void tokenValido(Context context, TokenCallback callback) {
        String token = leerToken(context);
        if (token == null || token.isEmpty()) {
            callback.onResult(false);
            return;
        }

        Call<User> call = getEndPoints().getCurrentUser(token);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                callback.onResult(response.isSuccessful());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onResult(false);
            }
        });
    }


    public static void guardarRol(Context context, String role){
        SharedPreferences sp = context.getSharedPreferences("token.xml", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("role", role);
        editor.apply();
        Log.d("salida","Rol guardado: " + role);
    }

    public static String leerRol(Context context){
        SharedPreferences sp = context.getSharedPreferences("token.xml",0);
        return sp.getString("role", "Usuario"); // por defecto
    }
}
