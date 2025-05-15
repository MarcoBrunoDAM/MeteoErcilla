package com.example.meteoercilla.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlertasServiceReinicio {
    //Como podemos observar es el mismo metodo que usamos en el MainActivity
    //NOTA: Esto SOLO se ejecuta cuando reiniciamos el dispositivo.
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
}
