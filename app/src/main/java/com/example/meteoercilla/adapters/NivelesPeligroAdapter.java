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

import java.util.List;

public class NivelesPeligroAdapter extends ArrayAdapter<NivelPeligro> {
    private Context context;
    public  NivelesPeligroAdapter(Context context, List<NivelPeligro> nivelesPeligro) {
        super(context, 0, nivelesPeligro);
    }

    //Esto se usa para que independientemente de cuantas veces abras y cierras el spinner de provncias
    //El estado de los checkbox sean acorde a si estan elegidas o no , ya que si no puede dar inconsistencias
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NivelesPeligroAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.niveles_peligro_spinner, parent, false);
            holder = new ViewHolder();
            holder.ck_nivelPeligro = convertView.findViewById(R.id.ck_tipoAlerta);
            holder.tx_nivelPeligro = convertView.findViewById(R.id.tx_nivelPeligro);
            convertView.setTag(holder);
        } else {
            holder = (NivelesPeligroAdapter.ViewHolder) convertView.getTag();
        }

       NivelPeligro nivelPeligro = getItem(position);
        if (nivelPeligro != null) {
            holder.tx_nivelPeligro.setText(nivelPeligro.getNombre());
            // Quitar listener temporalmente para evitar disparos no deseados
            holder.ck_nivelPeligro.setOnCheckedChangeListener(null);
            holder.ck_nivelPeligro.setChecked(nivelPeligro.isSeleccionada());
            // Reasignar el listener
            holder.ck_nivelPeligro.setOnCheckedChangeListener((buttonView, isChecked) -> {
               nivelPeligro.setSeleccionada(isChecked);
                if (isChecked) {
                    Toast.makeText(getContext(), nivelPeligro.getNombre(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        return convertView;
    }

    static class ViewHolder {
        CheckBox ck_nivelPeligro;
        TextView tx_nivelPeligro;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Se reutiliza el mismo layout para el dropdown
        return getView(position, convertView, parent);
    }
}
