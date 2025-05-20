package com.example.meteoercilla;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meteoercilla.permissions.NotifyPermissions;
import com.example.meteoercilla.permissions.UbicationPermission;
import com.example.meteoercilla.services.AlertasService;
import com.example.meteoercilla.services.CheckUbicationService;
import com.example.meteoercilla.services.ObtenerUbicacionService;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class MenuActivity extends AppCompatActivity {
    //Una variable es cuando iniciamos sesion de forma manual y la otra cuando lo hacemos
    //automaticamente;
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    private LocationRequest locationRequest;
    private SettingsClient settingsClient;
    int contadorPermisos = 0;
    private Context context;
    SharedPreferences sharedPreferences;
    private CheckUbicationService checkUbicationService;
    String ultimaUbicacion;
    String id_usuarioSesion;
    int userId;
    Button btn_buscar, btn_editar, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sharedPreferences = getSharedPreferences("Sesion",Context.MODE_PRIVATE);
        id_usuarioSesion = sharedPreferences.getString("userId",null);
        checkUbicationService = new CheckUbicationService(this);
        //Creamos lo necesario para el servicio en otra clase pero el servicio se ejecuta aqui
        this.locationRequest = checkUbicationService.getLocationRequest();
        this.settingsClient = checkUbicationService.getSettingsClient();
        btn_buscar = findViewById(R.id.btn_buscarActivity);
        btn_editar = findViewById(R.id.btn_editarDatos);
        btn_logout = findViewById(R.id.btn_logout);
        chekActivatedUbication();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Con esto evitamos que se formen bucles si le damos a NO PERMITIR
        //Solicitamos los permisos en caso de que no los tengamos al igual que en el main activity
        // en el caso que pasemos directamente al menu por el login automatico
        if(contadorPermisos == 0) {
            UbicationPermission.UbicationPermission(this);
            contadorPermisos++;
        }
        if (contadorPermisos <= 2) {
            NotifyPermissions.NotifyPermission(this);
            contadorPermisos++;
        }
    }


    protected void onStart() {
        super.onStart();
        //Capturamos primero el dato como string y luego convertimos para evitar
        //NullPointerException
        if(id_usuarioSesion != null){
            userId = Integer.valueOf(id_usuarioSesion);
        }
        Toast.makeText(this,"ID: " +userId,Toast.LENGTH_SHORT).show();
        ultimaUbicacion = obtenerUltimaUbicacion();
        //ejecutarServicioAlertas(this);
        if (ultimaUbicacion != null) {
            Toast.makeText(this, "Ultima ubicacion conocida: " + ultimaUbicacion, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No hay ubicacion", Toast.LENGTH_SHORT).show();
        }
    }

    public void activityBusqueda(View view) {
        Intent intent = new Intent(this, BusquedasActivity.class);
        startActivity(intent);
    }

    public void activityEditar(View view) {
        Intent intent = new Intent(this, EditarDatosActivity.class);
        intent.putExtra("id_usuario", userId);
        startActivity(intent);
    }


    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Esto mira el sharedpreferences y devuelve la ultima ubicacion guardada
    public String obtenerUltimaUbicacion() {
        SharedPreferences sharedPreferences = getSharedPreferences("Ubicacion", Context.MODE_PRIVATE);
        String ubicacion = sharedPreferences.getString("ultimaUbicacion", null);
        return ubicacion;
    }


    public void obtenerUbicacion() {
        ObtenerUbicacionService ubicacionService = new ObtenerUbicacionService(this);
    }

    //En este caso el servicio se ejecuta en la propia activity ya que necesita un dialogo
    // y eso no se puede hacer en una clase normal.
    private void chekActivatedUbication() {
        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build();
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);

        task.addOnSuccessListener(this, response -> {
            Toast.makeText(this, "GPS ACTIVADO", Toast.LENGTH_SHORT).show();
            obtenerUbicacion();
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // Muestra el diálogo estándar de Google Play Services
                    Toast.makeText(this, "DEBES ACTIVAR EL GPS", Toast.LENGTH_SHORT).show();
                    ((ResolvableApiException) e)
                            .startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    Toast.makeText(this, "No se pudo solicitar activar GPS", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Configuración de ubicación no satisfactoria", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed(){

    }

}