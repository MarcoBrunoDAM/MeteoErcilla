package com.example.meteoercilla;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meteoercilla.dao.UsuariosDAO;

import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {
    UsuariosDAO usuariosDAO = new UsuariosDAO();
    EditText tx_correo , tx_password;
    Button btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tx_correo = findViewById(R.id.tx_correo_login);
        tx_password = findViewById(R.id.tx_contrasena_login);
        btn_login = findViewById(R.id.btn_loginUsuario);
    }

    public void loginUsuario (View view){
        String correo = tx_correo.getText().toString();
        String password = tx_password.getText().toString();
        try{
            int okLogin = usuariosDAO.loginUsuario(correo,password);
            if(correo.equals("") || password.equals("")){
                Toast.makeText(this,"No puede haber campos vacios",Toast.LENGTH_SHORT).show();
            }
            else {
                if (okLogin == 0) {
                    int idUsuario = usuariosDAO.getIdUsuario(correo);
                    Toast.makeText(this, "LOGIN EXITOSO", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MenuActivity.class);
                    intent.putExtra("id_usuario", idUsuario);
                    guardarSesion(String.valueOf(idUsuario), correo);
                    startActivity(intent);
                    finish();
                } else if (okLogin == 1) {
                    Toast.makeText(this, "La contraseña es incorrecta", Toast.LENGTH_SHORT).show();
                } else if (okLogin == 2) {
                    Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error en el login", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarSesion(String userId, String correo) {
            SharedPreferences sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //No guardamos la contraseña por motivos obvios de seguridad
            editor.putString("userId", userId);
            editor.putString("correo", correo);
            editor.apply();  // o commit() si quieres que sea inmediato
        }
    }