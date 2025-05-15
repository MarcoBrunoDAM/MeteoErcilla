package com.example.meteoercilla.services;
import android.content.Context;
import android.content.IntentSender;
import android.widget.Toast;

import com.example.meteoercilla.MainActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class CheckUbicationService {
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    private LocationRequest locationRequest;
    private SettingsClient settingsClient;
    private Context context;
    public CheckUbicationService(Context context) {
        this.context = context;
        // 1. Prepara tu LocationRequest
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000)
                .setMinUpdateIntervalMillis(5_000)
                .build();

        // 2. Crea el client
        settingsClient = LocationServices.getSettingsClient(context);
    }

    public SettingsClient getSettingsClient() {
        return settingsClient;
    }

    public void setSettingsClient(SettingsClient settingsClient) {
        this.settingsClient = settingsClient;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    public void setLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
    }
}
