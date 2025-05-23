package com.example.meteoercilla;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meteoercilla.dao.MeteoErcillaDAO;
import com.example.meteoercilla.database.SQLDatabaseManagerRemota;
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

import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
private static final int REQUEST_CHECK_SETTINGS = 1001;
private LocationRequest locationRequest;
private SettingsClient settingsClient;
private CheckUbicationService checkUbicationService;
int contadorPermisos = 0; //Esta variable existe para evitar problemas de bucles a la hora de solicitar permisos
Button btn_login, btn_registrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btn_login = findViewById(R.id.btn_login);
        btn_registrar = findViewById(R.id.btn_registro);
        //Esta variable esta para evitar problemas de bucles a la hora de solicitar permisos.
        int contadorPermisos = 0;
        try{
            Connection connection = SQLDatabaseManagerRemota.connect();
            if(connection != null){
                Toast.makeText(this,"Exito",Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        checkUbicationService = new CheckUbicationService(this);
        //Creamos lo necesario para el servicio en otra clase pero el servicio se ejecuta aqui
        //Basicamente creamos un objeto de este servicio para poder usar y modificar las variables
        // y usarlas aqui
        this.locationRequest = checkUbicationService.getLocationRequest();
        this.settingsClient = checkUbicationService.getSettingsClient();
        ejecutarServicioAlertas(this);
        if(comprobarLogin()){
            Intent intent = new Intent(this,MenuActivity.class);
            SharedPreferences sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
            //como aqui solo entra cuando tenemos una id , no puede dar nunca null
            String userID = sharedPreferences.getString("userId",null);
            intent.putExtra("userID",userID);
            startActivity(intent);
            //Si el usuario inicia sesion automaticamente y no tiene la ubicacion activada,
            // se pedira activarla en el menu , si no esta logeado , se pedira en la main activity
            // y en caso de rechazarla y despues logearse se le volvera a pedir en el menu, ya que por
            //razones obvias esta app necesita la ubicacion lo maximo posible.
        } else {
            chekActivatedUbication();
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage("+34643406362", "Meteoercilla", "Hola desde tu app!", null, null);
        }


    }


    protected void onStart(){
        super.onStart();
    }

    protected void onResume(){
        super.onResume();
        //Con esto evitamos que se formen bucles si le damos a NO PERMITIR
        //Solicitamos los permisos en caso de que no los tengamos
        if(contadorPermisos == 0) {
            UbicationPermission.ubicationPermission(this);
            contadorPermisos++;
        }
        if (contadorPermisos <= 2) {
            NotifyPermissions.notifyPermission(this);
            contadorPermisos++;
        }
        }



    public void login (View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void register (View view){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }


    //Esto comprueba si SharedPreferences ya tiene datos de un inicio de sesion previo
    public boolean comprobarLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        if(sharedPreferences == null){
            return false;
        }
        else{
        //null es el valor por defecto en caso de no encontrar nada
        String idUsuario = sharedPreferences.getString("userId",null);
        if(idUsuario != null){
            return true;
        }
        else {
            return false;
        }
    }
    }



    public void obtenerUbicacion(){
        ObtenerUbicacionService ubicacionService = new ObtenerUbicacionService(this);
    }


    //Aqui lanzamos el servicio cada x segundos
    public void ejecutarServicioAlertas(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertasService.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_MUTABLE);
        long interval = 1;
        long triggerAt = System.currentTimeMillis() + interval; // la primera ejecución

        //Utilizamos setInexactRepeating para ahorro de batería, o setExactAndAllowWhileIdle para exactitud
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAt, interval, alarmIntent);
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
            Toast.makeText(this,"GPS ACTIVADO",Toast.LENGTH_SHORT).show();
            obtenerUbicacion();
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // Muestra el diálogo estándar de Google Play Services
                    Toast.makeText(this,"DEBES ACTIVAR EL GPS",Toast.LENGTH_SHORT).show();
                    ((ResolvableApiException) e)
                            .startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {

                }
            }
        });

    }
}