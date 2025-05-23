package com.example.meteoercilla.notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.example.meteoercilla.R;
import com.example.meteoercilla.dao.AlertasDAO;
import com.example.meteoercilla.dao.MeteoErcillaDAO;
import com.example.meteoercilla.models.Alerta;

import java.sql.SQLException;

public class NotificacionesAlerta extends BroadcastReceiver {

String channelId = "alerta";
AlertasDAO alertasDAO = new AlertasDAO();

    @Override
    public void onReceive(Context context, Intent intent) {
        Alerta alerta = (Alerta) intent.getSerializableExtra("alerta");
        if (alerta != null) {
            int idUsuario = intent.getIntExtra("idUsuario", 0);
            String alertaEn = context.getString(R.string.alerta_en);
            String titulo = context.getString(R.string.alerta_notificacion);
            String por = context.getString(R.string.notificaciones_por);
            String nivelPeligroTitulo = context.getString(R.string.resultados_nivel_peligro);
            String fecha = context.getString(R.string.resultados_fecha);
            String precaucion = context.getString(R.string.precaucion);
            String nivelPeligro = "";

            //Hacemos lo mismo que en el adapter de resultados de busqueda
            switch (alerta.getNivelPeligro()) {
                case "Leve":
                    nivelPeligro = context.getString(R.string.nivel_leve);
                    break;
                case "Moderado":
                    nivelPeligro = context.getString(R.string.nivel_moderado);
                    break;
                case "Importante":
                    nivelPeligro = context.getString(R.string.nivel_importante);
                    break;
                case "Grave":
                    nivelPeligro = context.getString(R.string.nivel_grave);
                    break;
                case "Extremo":
                    nivelPeligro = context.getString(R.string.nivel_extremo);
                    break;
            }

            String tipoAlerta = "";

            //Empezamos con el tipo de alerta.
            switch (alerta.getTipoAlerta()) {
                case "Lluvias y tormentas":
                    tipoAlerta = context.getString(R.string.lluvias_tormentas);
                    break;
                case "Vientos":
                    tipoAlerta = context.getString(R.string.viento);
                    break;
                case "Nieve":
                    tipoAlerta = context.getString(R.string.nieve);
                    break;
                case "Granizo":
                    tipoAlerta = context.getString(R.string.granizo);
                    break;
                case "Derrumbamientos":
                    tipoAlerta = context.getString(R.string.derrumbamientos);
                    break;
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, "alertas", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(titulo + " " + alerta.getProvincia())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(alertaEn + " " + alerta.getUbicacion()
                            + " " + por + " " + tipoAlerta +
                            "\n"+nivelPeligroTitulo+ ": " + nivelPeligro +
                            "\n"+fecha+ ": " + alerta.getFechaAlerta() +
                            "\n"+precaucion))
                    .setPriority(NotificationCompat.PRIORITY_HIGH).setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setAutoCancel(true).setColor(Color.RED);
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
            //esto se hace para dejar constancia de que el usuario ha sido notificado.
            //de ser asi , no se repite la misma notificacion
            try {
                if(idUsuario != 0) {
                    alertasDAO.notificarAlerta(idUsuario, alerta.getIdAlerta());
                }
                else{
                    //Al igual que a un usuario logeado le guardamos su alerta notificada
                    // en base de datos para que no se repita , al no estar logeado, no tenemos id
                    // por lo cual para que no se repita la misma notificacion aunque no estes logeado
                    //guardamos la id de la alerta en el propio SharedPreferences
                    SharedPreferences sharedPreferences = context.getSharedPreferences("Alertas", Context.MODE_PRIVATE);
                    //Cogemos el valor anterior y le concatenamos el nuevo
                    String a = sharedPreferences.getString("alertas",null);
                    if(a == null){
                        a = "";
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String alertaUsuario = a+","+alerta.getIdAlerta();
                    editor.putString("alertas",alertaUsuario);
                    editor.apply();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
