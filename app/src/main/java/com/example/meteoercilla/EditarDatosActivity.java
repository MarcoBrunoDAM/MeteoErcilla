package com.example.meteoercilla;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meteoercilla.adapters.ProvinciasAdapter;
import com.example.meteoercilla.dao.UsuariosDAO;
import com.example.meteoercilla.models.Provincia;

import java.sql.SQLException;
import java.util.ArrayList;

public class EditarDatosActivity extends AppCompatActivity {
Spinner sp_provincias;
EditText tx_correo , tx_telefono;
Button btn_editar;
String correo = "";
String telefono = "";
UsuariosDAO usuariosDAO = new UsuariosDAO();
ArrayList<String> provincias = new ArrayList<>();
ArrayList<String> provinciasUsuario = new ArrayList<>();
ArrayList<Provincia> provinciasSpinner = new ArrayList<>();
int idUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        idUsuario = getIntent().getIntExtra("id_usuario",0);
        Toast.makeText(this, "ID a editar: " +idUsuario, Toast.LENGTH_SHORT).show();
        sp_provincias = findViewById(R.id.sp_provinciasEditar);
        tx_correo = findViewById(R.id.tx_correoEditar);
        tx_telefono = findViewById(R.id.tx_telefonoEditar);
        btn_editar = findViewById(R.id.btn_editarUsuario);
        try {
            correo = usuariosDAO.getCorreoById(idUsuario);
            telefono = usuariosDAO.getTelefonoById(idUsuario);
            provincias = usuariosDAO.getProvincias();
            provinciasUsuario = usuariosDAO.getNombresProvinciaByID(idUsuario);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        provinciasSpinner = new ArrayList<>();
        for (String p : provincias) {
            Provincia provincia = new Provincia(p, false);
            provinciasSpinner.add(provincia);
        }
        //Con esto marcamos como seleccionadas las provincias que el usuario ya tenia elegidas
        for (Provincia p : provinciasSpinner){
            for(String provinciaUsuario : provinciasUsuario){
                if(p.getNombre().equals(provinciaUsuario)){
                    p.setSeleccionada(true);
                }
            }
        }
        ProvinciasAdapter provinciaAdapter = new ProvinciasAdapter(this, provinciasSpinner);
        sp_provincias.setAdapter(provinciaAdapter);
        tx_correo.setText(correo);
        tx_telefono.setText(telefono);
    }

    public void onStart(){
        super.onStart();
    }


    public void editarProvincias(View view){
        ArrayList<String> provinciasSeleccionadas = getProvoncias();
        if(provinciasSeleccionadas == null){
            return;
        }
        try {
            ArrayList<Integer> listaIdProvincia = usuariosDAO.getListaIdProvincia(provinciasSeleccionadas);
            //Borrmaos los registros actuales y creamos los nuevos , hace la misma funcion que actualizar
            usuariosDAO.eliminarProvincias(idUsuario);
            boolean okProvincias = usuariosDAO.registrarProvincias(listaIdProvincia,idUsuario);
            if (okProvincias){
                Toast.makeText(this,"Usuario editado con exito",Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public ArrayList<String> getProvoncias(){
        ArrayList<String> provinciaSeleccionadas = new ArrayList<>();
        for (Provincia provincia : provinciasSpinner ) {
            if (provincia.isSeleccionada()) {
                provinciaSeleccionadas.add(provincia.getNombre());
            }
        }

        // Mostrar un Toast con las provincias seleccionadas
        if (provinciaSeleccionadas.isEmpty()) {
            return null;
        } else {
            return provinciaSeleccionadas;
        }
    }
}