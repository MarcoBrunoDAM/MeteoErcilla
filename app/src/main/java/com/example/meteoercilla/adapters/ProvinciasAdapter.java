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
import com.example.meteoercilla.models.Provincia;

import java.util.List;

public class ProvinciasAdapter extends ArrayAdapter<Provincia> {

    private Context context;
    public ProvinciasAdapter(Context context, List<Provincia> provincias) {
        super(context, 0, provincias);
    }

    //Esto se usa para que independientemente de cuantas veces abras y cierras el spinner de provncias
    //El estado de los checkbox sean acorde a si estan elegidas o no , ya que si no puede dar inconsistencias
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.provincias_spinner, parent, false);
            holder = new ViewHolder();
            holder.ckProvincia = convertView.findViewById(R.id.ck_tipoAlerta);
            holder.txProvincia = convertView.findViewById(R.id.tx_tipoAlerta);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Provincia provincia = getItem(position);
        if (provincia != null) {
            holder.txProvincia.setText(provincia.getNombre());
              // Quitar listener temporalmente para evitar disparos no deseados
                holder.ckProvincia.setOnCheckedChangeListener(null);
                holder.ckProvincia.setChecked(provincia.isSeleccionada());
                // Reasignar el listener
                holder.ckProvincia.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    provincia.setSeleccionada(isChecked);
                    if (isChecked) {
                        Toast.makeText(getContext(), provincia.getNombre(), Toast.LENGTH_SHORT).show();
                    }
                });

        }
        return convertView;
    }

    static class ViewHolder {
        CheckBox ckProvincia;
        TextView txProvincia;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Se reutiliza el mismo layout para el dropdown
        return getView(position, convertView, parent);
    }


}
