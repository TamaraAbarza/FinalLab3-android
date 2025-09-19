package com.ulp.eventapp;

import android.content.Intent;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ulp.eventapp.databinding.ActivitySplashBinding;
import com.ulp.eventapp.request.ApiClient;

public class SplashActivity extends AppCompatActivity {

    private volatile boolean tokenCheckDone = false;
    private boolean tokenValido = false;

    // Delay mínimo para que el splash no “parpadee”
    private static final long MIN_SPLASH_DURATION_MS = 800L;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instala la SplashScreen del sistema (Android 12+ o no-op <12)
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        startTime = SystemClock.uptimeMillis();

        // Mantener el splash visible hasta que termine la verificación
        splashScreen.setKeepOnScreenCondition(() -> !tokenCheckDone);

        ApiClient.tokenValido(getApplicationContext(), valido -> {
            tokenValido = valido;
            long elapsed = SystemClock.uptimeMillis() - startTime;
            long remain = Math.max(0, MIN_SPLASH_DURATION_MS - elapsed);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                tokenCheckDone = true; // libera el splash
                // 2) Navegar según resultado
                Intent intent = new Intent(
                        SplashActivity.this,
                        tokenValido ? MainActivity.class : LoginActivity.class
                );
                startActivity(intent);
                finish();
            }, remain);
        });
    }
}