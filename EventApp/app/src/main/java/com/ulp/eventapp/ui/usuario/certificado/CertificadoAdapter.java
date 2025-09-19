package com.ulp.eventapp.ui.usuario.certificado;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ulp.eventapp.R;
import com.ulp.eventapp.model.Event;
import com.ulp.eventapp.request.ApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertificadoAdapter extends RecyclerView.Adapter<CertificadoAdapter.ViewHolderVH> {

    private List<Event> listaDeEventos;
    private Context context;
    private LayoutInflater li;

    public CertificadoAdapter(List<Event> listaDeEventos, Context context, LayoutInflater li) {
        this.listaDeEventos = listaDeEventos;
        this.context = context;
        this.li = li;
    }

    @NonNull
    @Override
    public ViewHolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.item_certificado, parent, false);
        return new ViewHolderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVH holder, int position) {
        Event evento = listaDeEventos.get(position);

        // Setear texto
        holder.nombre.setText(evento.getName());
        //holder.fecha.setText(evento.getFecha());

        // Botón
        holder.btn.setOnClickListener(v -> {
            String token = ApiClient.leerToken(context.getApplicationContext());
            if (token != null && !token.isEmpty()) {
                ApiClient.MisEndPoints api = ApiClient.getEndPoints();
                Call<ResponseBody> call = api.downloadCertificate(token, evento.getId());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                Uri uri = savePdfToDownloads(response.body(),
                                        "certificado_evento_" + evento.getId() + ".pdf");

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (uri != null) {
                                        Toast.makeText(context, "Certificado descargado", Toast.LENGTH_SHORT).show();
                                        openPdf(uri);
                                    } else {
                                        Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_LONG).show();
                                    }
                                });
                            });
                        } else {
                            Toast.makeText(context, "Error " + response.code() + ": no se pudo descargar", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("CertificadoAdapter", "Error descargando certificado", t);
                        Toast.makeText(context, "Error en la descarga", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.e("CertificadoAdapter", "Token nulo o vacío");
                Toast.makeText(context, "No autorizado", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaDeEventos.size();
    }

    public static class ViewHolderVH extends RecyclerView.ViewHolder {
        TextView nombre, fecha;
        Button btn;

        public ViewHolderVH(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvTituloCertificado);
           fecha = itemView.findViewById(R.id.tvFechaCertificado);
            btn = itemView.findViewById(R.id.btnDescargar);
        }
    }

    //Guarda el PDF en Downloads/MisCertificados
    private Uri savePdfToDownloads(ResponseBody body, String filename) {
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/MisCertificados");

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            if (uri == null) {
                Log.e("CertificadoAdapter", "No se pudo crear URI para el archivo.");
                return null;
            }

            try (OutputStream os = resolver.openOutputStream(uri);
                 InputStream is = body.byteStream()) {

                if (os == null) {
                    Log.e("CertificadoAdapter", "OutputStream es null.");
                    return null;
                }

                byte[] buffer = new byte[4096];
                int read;

                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
                return uri;
            }

        } catch (IOException e) {
            Log.e("CertificadoAdapter", "Error guardando PDF con MediaStore: ", e);
            return null;
        }
    }

    private void openPdf(Uri uri) {
        if (uri == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No hay aplicación para abrir PDFs", Toast.LENGTH_LONG).show();
        }
    }

}

