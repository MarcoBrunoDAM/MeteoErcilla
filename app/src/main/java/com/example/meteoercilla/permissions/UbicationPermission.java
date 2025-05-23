package com.example.meteoercilla.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UbicationPermission {
public static void ubicationPermission (Context context){
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

        ActivityCompat.requestPermissions((Activity) context,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 102);
    }
}
}
