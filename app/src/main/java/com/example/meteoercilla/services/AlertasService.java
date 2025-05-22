package com.example.meteoercilla.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.meteoercilla.dao.MeteoErcillaDAO;
import com.example.meteoercilla.models.Alerta;
import com.example.meteoercilla.notificaciones.NotificacionesAlerta;
import com.example.meteoercilla.permissions.NotifyPermissions;

import java.sql.SQLException;
import java.util.ArrayList;

public class AlertasService extends BroadcastReceiver {
MeteoErcillaDAO meteoErcillaDAO = new MeteoErcillaDAO();
Context context;
//Este servicio se lanza cada x segundos cada vez que se inicia la app,
//realiza las comprobaciones en base de datos y en base a lo que devuelva ,
//construye la notificacion

    @Override
        public void onReceive(Context context, Intent intent) {
            this.context = context;
            Toast.makeText(context, "Inicio servicio alertas", Toast.LENGTH_SHORT).show();
            obtenerUbicacion();
            SharedPreferences sharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("userId", null);
            SharedPreferences ubicacion = context.getSharedPreferences("Ubicacion", Context.MODE_PRIVATE);
            String ultimaUbicacion = ubicacion.getString("ultimaUbicacion", null);
            if (id != null) {
                int idUser = Integer.valueOf(id);
                try {
                    //Primero comprueba si hay alertas que deban ser lanzadas
                    Toast.makeText(context, "Ultima ubicacion servicio: " + ultimaUbicacion, Toast.LENGTH_SHORT).show();
                    int idUbicacion = meteoErcillaDAO.getIdProvinciaByNombre(ultimaUbicacion);
                    //Si no hay una ultimaUbicacion la id sera 0 , con lo cual al buscar en base de datos con
                    // la id 0 al no existir solo te va a devolver las alertas de las provincias que el usuario
                    //tenga registradas, esto en caso por ejemplo que la app no tenga permisos de ubicacion
                    ArrayList<Integer> listaProvincias = meteoErcillaDAO.getIDsProvinciaByID(idUser);
                    ArrayList<Alerta> alertas = meteoErcillaDAO.getAlertasServicio(listaProvincias, idUbicacion);
                    if (alertas.size() >= 1) {
                        for (Alerta a : alertas) {
                            //Ahora comprobamos si esas alertas han sido notificadas previamente o no
                            boolean estaNotificada = meteoErcillaDAO.comprobarAlertaNotificada(idUser, a.getIdAlerta());
                            //Si no ha sido notificada , se notifica , en caso contrario , no hace nada
                            if (!estaNotificada) {
                                //En caso de no haber sido notificada , mandamos esa alerta
                                //como intent al creador de notificaciones.
                                //Al ser en bucle nos mandara x notificaciones como x alertas tengamos.

                                //Comprobamos que sigamos teniendo los permisos antes de mandarlas.
                                if(NotifyPermissions.tienePermisoNotificaciones(context)) {
                                    Toast.makeText(context, "Se han encontrado alertas", Toast.LENGTH_SHORT).show();
                                    Intent notificationIntent = new Intent(context, NotificacionesAlerta.class);
                                    notificationIntent.putExtra("alerta", a);
                                    notificationIntent.putExtra("idUsuario", idUser);
                                    context.sendBroadcast(notificationIntent);
                                }
                            }

                        }
                    }
                } catch (SQLException e) {
                    Toast.makeText(context, "Error en las alertas", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            //ESTO SE HACE PARA QUE AUNQUE NO HAYA UN USUARIO LOGEADO , SIGAN APARECIENDO LAS ALERTAS
            //POR UBICACION AUNQUE NOS ASEGURAMOS DE QUE NO SE REPITA LA MISMA NOTIFICACION
            else {
                try {
                    SharedPreferences sharedPreferences2 = context.getSharedPreferences("Alertas",Context.MODE_PRIVATE);
                    String alertasUsuarioDispositivo = sharedPreferences2.getString("alertas",null);
                    //Primero comprueba si hay una ubicacion guardada
                    if(ultimaUbicacion != null) {
                        Toast.makeText(context, "Ultima ubicacion servicio: " + ultimaUbicacion, Toast.LENGTH_SHORT).show();
                        int idUbicacion = meteoErcillaDAO.getIdProvinciaByNombre(ultimaUbicacion);
                        //Aqui como no tenemos un usuario logeado , no tenemos su lista de provincias con lo cual
                        // solo buscara con la ubicacion actual o la ultima registrada-
                        ArrayList<Alerta> alertas = meteoErcillaDAO.getAlertasServicio(null, idUbicacion);
                        ArrayList<String> alertasCheck = new ArrayList<>();
                        if (alertasUsuarioDispositivo != null) {
                            String al[] = alertasUsuarioDispositivo.split(",");
                            for (int i = 0; i < al.length; i++) {
                                if (!al[i].equals(",")) {
                                    alertasCheck.add(al[i]);
                                }
                            }
                        } else {
                            //Le añadimos un espacio vacio para no dejarlo en null y evitar excepciones.
                            alertasCheck.add("");
                        }
                        if (alertas.size() >= 1) {
                            for (Alerta a : alertas) {
                                if (alertasCheck.contains(String.valueOf(a.getIdAlerta()))) {
                                    Toast.makeText(context, "La alerta " + a.getIdAlerta() + " ya ha sido notificada", Toast.LENGTH_SHORT).show();
                                } else {
                                    if(NotifyPermissions.tienePermisoNotificaciones(context)) {
                                        Toast.makeText(context, "Se han encontrado alertas", Toast.LENGTH_SHORT).show();
                                        Intent notificationIntent = new Intent(context, NotificacionesAlerta.class);
                                        notificationIntent.putExtra("alerta", a);
                                        notificationIntent.putExtra("idUsuario", 0);
                                        context.sendBroadcast(notificationIntent);
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(context,"NO HAY NINGUNA UBICACION GUARDADA",Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    Toast.makeText(context, "Error en las alertas", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }
        public void obtenerUbicacion () {
            ObtenerUbicacionService ubicacionService = new ObtenerUbicacionService(this.context);
        }
}



