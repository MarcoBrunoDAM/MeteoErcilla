package com.example.meteoercilla.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meteoercilla.R;
import com.example.meteoercilla.models.NivelPeligro;
import com.example.meteoercilla.models.TipoAlerta;

import java.util.List;

public class TiposAlertaAdapter extends ArrayAdapter<TipoAlerta> {
    private Context context;
    public  TiposAlertaAdapter(Context context, List<TipoAlerta> tiposAlerta) {
        super(context, 0, tiposAlerta);
    }

    //Esto se usa para que independientemente de cuantas veces abras y cierras el spinner de provncias
    //El estado de los checkbox sean acorde a si estan elegidas o no , ya que si no puede dar inconsistencias
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TiposAlertaAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tipo_alertas_spinner, parent, false);
            holder = new ViewHolder();
            holder.ck_tipoAlerta = convertView.findViewById(R.id.ck_tipoAlerta);
            holder.tx_tipoAlerta = convertView.findViewById(R.id.tx_tipoAlerta);
            convertView.setTag(holder);
        } else {
            holder = (TiposAlertaAdapter.ViewHolder) convertView.getTag();
        }

        TipoAlerta tipoAlerta = getItem(position);
        if (tipoAlerta != null) {
            holder.tx_tipoAlerta.setText(tipoAlerta.getNombre());
            // Quitar listener temporalmente para evitar disparos no deseados
            holder.ck_tipoAlerta.setOnCheckedChangeListener(null);
            holder.ck_tipoAlerta.setChecked(tipoAlerta.isSeleccionada());
            // Reasignar el listener
            holder.ck_tipoAlerta.setOnCheckedChangeListener((buttonView, isChecked) -> {
                tipoAlerta.setSeleccionada(isChecked);
                if (isChecked) {
                    Toast.makeText(getContext(), tipoAlerta.getNombre(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        return convertView;
    }

    static class ViewHolder {
        CheckBox ck_tipoAlerta;
        TextView tx_tipoAlerta;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Se reutiliza el mismo layout para el dropdown
        return getView(position, convertView, parent);
    }
}
