package com.example.meteoercilla.adapters;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.meteoercilla.R;
import com.example.meteoercilla.models.Alerta;

import java.util.ArrayList;

public class ResultadosBusquedaAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Alerta> alertas;

    public ResultadosBusquedaAdapter(Context context, ArrayList<Alerta> alertas) {
        this.context = context;
        this.alertas = alertas;
    }

    @Override
    public int getCount() {
        return alertas.size();
    }

    @Override
    public Object getItem(int position) {
        return alertas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.resultados_busqueda_list, parent, false);
            holder = new ViewHolder();
            holder.main = convertView.findViewById(R.id.main);
            holder.tx_resultadoUbicacion = convertView.findViewById(R.id.tx_resultadoUbicacion);
            holder.tx_resultadoProvincia = convertView.findViewById(R.id.tx_resultadoProvincia);
            holder.tx_resultadoFecha = convertView.findViewById(R.id.tx_resultadoFecha);
            holder.tx_resultadoNivelPeligro = convertView.findViewById(R.id.tx_resultadoNivelPeligro);
            holder.tx_resultadoTipoAlerta = convertView.findViewById(R.id.tx_resultadosTipoAlerta);
            holder.weather_icon = convertView.findViewById(R.id.weather_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Alerta alerta = alertas.get(position);
        //Traducimos los elementos del fichero Strings.xml

        //Empezamos con el tipo de alerta.
        switch (alerta.getTipoAlerta()) {
            case "Lluvias y tormentas":
                holder.tx_resultadoTipoAlerta.setText(context.getString(R.string.lluvias_tormentas));
                break;
            case "Vientos":
                holder.tx_resultadoTipoAlerta.setText(context.getString(R.string.viento));
                break;
            case "Nieve":
                holder.tx_resultadoTipoAlerta.setText(context.getString(R.string.nieve));
                break;
            case "Granizo":
                holder.tx_resultadoTipoAlerta.setText(context.getString(R.string.granizo));
                break;
            case "Derrumbamientos":
                holder.tx_resultadoTipoAlerta.setText(context.getString(R.string.derrumbamientos));
                break;
        }


        //Y ahora con el nivel de peligro
        switch (alerta.getNivelPeligro()) {
            case "Leve":
                holder.tx_resultadoNivelPeligro.setText(context.getString(R.string.nivel_leve));
                break;
            case "Moderado":
                holder.tx_resultadoNivelPeligro.setText(context.getString(R.string.nivel_moderado));
                break;
            case "Importante":
                holder.tx_resultadoNivelPeligro.setText(context.getString(R.string.nivel_importante));
                break;
            case "Grave":
                holder.tx_resultadoNivelPeligro.setText(context.getString(R.string.nivel_grave));
                break;
            case "Extremo":
                holder.tx_resultadoNivelPeligro.setText(context.getString(R.string.nivel_extremo));
                break;
        }
        holder.tx_resultadoUbicacion.setText(alerta.getUbicacion());
        holder.tx_resultadoProvincia.setText(alerta.getProvincia());
        holder.tx_resultadoFecha.setText(String.valueOf(alerta.getFechaAlerta()));
        holder.weather_icon.setAnimation(alerta.getIcono());
        //Obtenemos el fondo del layout el cual es el que tiene un borde personalizado
        //Para que podamos cambiar el color sin perder el borde
        GradientDrawable fondo = (GradientDrawable) holder.main.getBackground();

        if(alerta.getNivelPeligro().equals("Extremo")){
            fondo.setColor(Color.parseColor("#B3ADE6"));
        }
        else if(alerta.getNivelPeligro().equals("Grave")){
            fondo.setColor(Color.parseColor("#E6ADAD"));
        }
        else if(alerta.getNivelPeligro().equals("Importante")){
            fondo.setColor(Color.parseColor("#E6C1AD"));
        }
        else if(alerta.getNivelPeligro().equals("Moderado")){
            fondo.setColor(Color.parseColor("#E6E0AD"));
        }
        else if(alerta.getNivelPeligro().equals("Leve")){
            fondo.setColor(Color.parseColor("#ADD8E6"));
        }
        return convertView;
    }


    static class ViewHolder {
        TextView tx_resultadoUbicacion;
        TextView tx_resultadoProvincia;
        TextView tx_resultadoFecha;
        TextView tx_resultadoNivelPeligro;
        TextView tx_resultadoTipoAlerta;
        LottieAnimationView weather_icon;
        ConstraintLayout main ;
    }
}
