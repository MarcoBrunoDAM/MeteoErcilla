package com.example.meteoercilla;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meteoercilla.adapters.ResultadosBusquedaAdapter;
import com.example.meteoercilla.models.Alerta;

import java.util.ArrayList;

public class ResultadosBusquedaActivity extends AppCompatActivity {
ListView list_resultados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultados_busqueda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Capturamos la lista de resultados
        ArrayList<Alerta> resultados = (ArrayList<Alerta>) getIntent().getSerializableExtra("resultados");
        list_resultados = findViewById(R.id.list_resultados);
        ResultadosBusquedaAdapter resultadosBusquedaAdapter = new ResultadosBusquedaAdapter(this,resultados);
        list_resultados.setAdapter(resultadosBusquedaAdapter);

    }
}