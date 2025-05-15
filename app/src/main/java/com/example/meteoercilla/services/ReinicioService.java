package com.example.meteoercilla.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


//Esto sirve para asegurar que el servicio de alertas siga ejecutando aunque reiniciemos el dispositivo
// ua que de normal quien inicia el servicio es el main activity quien es el que
//posee un alarm manager para ejecutarlo pero al reiniciarse , aunque este puesto en el manifest
//para que se ejecute despues de un reinicio , se ejecuta una vez pero al no tener el alarm manager
//del MainActivity no sigue.

//Nota: Esto solo se ejecuta al reiniciar el dispositivo.
public class ReinicioService extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
          AlertasServiceReinicio alertasServiceReinicio = new AlertasServiceReinicio();
          alertasServiceReinicio.ejecutarServicioAlertas(context);
        }
    }
}
