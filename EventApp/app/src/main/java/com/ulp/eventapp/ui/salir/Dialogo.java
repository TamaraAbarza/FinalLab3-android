package com.ulp.eventapp.ui.salir;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.ulp.eventapp.LoginActivity;
import com.ulp.eventapp.request.ApiClient;

public class Dialogo {

    public static void mostrarDialogo(Context context){
        new AlertDialog.Builder(context)
                .setTitle("Cierre de sesión")
                .setMessage("¿Está seguro de que desea cerrar la sesión?")
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cerrar sesión
                        ApiClient.logout(context);

                        // Redirigir al Login
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }
}
