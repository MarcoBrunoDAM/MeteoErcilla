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
import com.example.meteoercilla.dao.MeteoErcillaDAO;
import com.example.meteoercilla.models.Provincia;
import com.example.meteoercilla.models.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    EditText tx_nombre, tx_apellidos, tx_correo, tx_telefono, tx_password, tx_repeatPassword;
    Button btn_registrarUsuario,btn_provincias;
    Spinner sp_provincias;
    MeteoErcillaDAO meteoErcillaDAO = new MeteoErcillaDAO();
    ArrayList<String> provincias = new ArrayList<>();
    ArrayList<Provincia> provinciasSpinner = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tx_nombre = findViewById(R.id.tx_nombre);
        tx_apellidos = findViewById(R.id.tx_apellido);
        tx_telefono = findViewById(R.id.tx_telefono);
        tx_correo = findViewById(R.id.tx_correo);
        tx_password = findViewById(R.id.tx_password);
        tx_repeatPassword = findViewById(R.id.tx_RepeatPassword);
        btn_registrarUsuario = findViewById(R.id.btn_registrarUsuario);
        sp_provincias = findViewById(R.id.sp_provincias);
        try {
            provincias = meteoErcillaDAO.getProvincias();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
         provinciasSpinner = new ArrayList<>();
        for (String p : provincias) {
            Provincia provincia = new Provincia(p, false);
            provinciasSpinner.add(provincia);
        }
        ProvinciasAdapter provinciaAdapter = new ProvinciasAdapter(this, provinciasSpinner);
        sp_provincias.setAdapter(provinciaAdapter);

    }


    public void registrarUsuario(View view){
        String nombre = tx_nombre.getText().toString();
        String apellidos = tx_apellidos.getText().toString();
        String correo = tx_correo.getText().toString();
        String telefono = tx_telefono.getText().toString();
        String password = tx_password.getText().toString();
        String repeatPassword = tx_repeatPassword.getText().toString();
        if (!password.equals(repeatPassword)){
            Toast.makeText(this,R.string.registro_contrasena_invalida,Toast.LENGTH_SHORT).show();
        }
        else if(nombre.equals("") || apellidos.equals("") || correo.equals("") || telefono.equals("")
        || password.equals("") || repeatPassword.equals("")){
            Toast.makeText(this,R.string.registro_vacio,Toast.LENGTH_SHORT).show();
        }
        else{
            Usuario usuario = new Usuario(nombre,apellidos,correo,telefono,password);
            try{
                ArrayList<String> provinciasSeleccionadas = getProvoncias();
                if(provinciasSeleccionadas == null){
                    Toast.makeText(this,"Debes elegir una o mas provincias",Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Integer> listaIdProvincia = meteoErcillaDAO.getListaIdProvincia(provinciasSeleccionadas);
                int okRegistro = meteoErcillaDAO.registroUsuario(usuario);

                if (okRegistro == 1){
                    Toast.makeText(this,R.string.registro_existe,Toast.LENGTH_SHORT).show();
                }
                else if (okRegistro == 0){
                    int idUsuario = meteoErcillaDAO.getIdUsuario(correo);
                    boolean okProvincias = meteoErcillaDAO.registrarProvincias(listaIdProvincia,idUsuario);
                    if (okProvincias){
                        Toast.makeText(this,R.string.registro_exito,Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

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
