package com.example.meteoercilla.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotifyPermissions {
    //ESTE METODO COMPRUEBA SI TIENES LOS PERMISOS O NO
    //Y EN CASO DE NO TENERLOS LOS SOLICITA
    public static void notifyPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 (Android 13)
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

    }
    //ESTE SOLO COMPRUEBA SI YA LOS TIENES Y DEVUELVE UN TRUE O FALSE
    public static boolean tienePermisoNotificaciones(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }
}
