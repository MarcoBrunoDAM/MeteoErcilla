package com.example.meteoercilla.services;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ObtenerUbicacionService {
    private final Context context;
    private FusedLocationProviderClient fusedLocationClient;
    boolean permisoUbicacion;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public ObtenerUbicacionService(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();;



        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();


                   //Ahora cuando hayamos obtenido la latitud y longitud llamamos a este metodo
                    // el cual usando la clase Geocoder nos traduce esas coordenadas a
                    //nuestra ciudad actual
                    obtenerCiudad(context, lat, lon);

                }
            }
        };

        //Aunque le hayamos dado el permiso con anterioridad , este metodo requiere comprobar
        //contantemente que la app posea el permiso de ubicacion para obtenerla en tiempo real
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            permisoUbicacion = true;
        }

    }

    public void obtenerCiudad(Context context, double latitud, double longitud){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1); // 1 = máximo 1 resultado
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String ciudad = address.getLocality(); //Obtenemos la localidad (Ocaña)
                String provincia = address.getSubAdminArea(); //Obtenemos la provincia (Toledo)
                String pais = address.getCountryName();
                guardarUltimaUbicacion(provincia);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error obteniendo dirección", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarUltimaUbicacion(String ubicacion){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Ubicacion",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ultimaUbicacion" , ubicacion);
        editor.apply();
    }
}
