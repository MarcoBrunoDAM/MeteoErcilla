package com.example.meteoercilla;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.example.meteoercilla.adapters.NivelesPeligroAdapter;
import com.example.meteoercilla.adapters.ProvinciasAdapter;
import com.example.meteoercilla.adapters.TiposAlertaAdapter;
import com.example.meteoercilla.dao.UsuariosDAO;
import com.example.meteoercilla.models.Alerta;
import com.example.meteoercilla.models.NivelPeligro;
import com.example.meteoercilla.models.Provincia;
import com.example.meteoercilla.models.TipoAlerta;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class BusquedasActivity extends AppCompatActivity {
EditText tx_fechaDesde , tx_fechaHasta;
Spinner sp_provincias , sp_nivelPeligro , sp_tipoAlerta;
Button btn_buscarAlertas;
ArrayList<String> provincias = new ArrayList<>();
ArrayList<Provincia> provinciasSpinner = new ArrayList<>();
ArrayList<String> nivelesPeligro = new ArrayList<>();
ArrayList<NivelPeligro> nivelesPeligroSpinner = new ArrayList<>();
ArrayList<String> tiposAlerta = new ArrayList<>();
ArrayList<TipoAlerta> tiposAlertaSpinner = new ArrayList<>();
UsuariosDAO usuariosDAO = new UsuariosDAO();
int contadorFechas = 0;
int contadorProvincias = 0;
int contadorNiveles = 0;
int contadorTipos = 0;
//Esta es la query base , los filtros se crearan a partir de esta
String query = "SELECT alertas.id_alerta ,TO_CHAR(alertas.fecha_alerta, 'DD-MM-YYYY') AS fecha_alerta, u.nombre_ubicacion," +
        " p.nombre_provincia , alertas.nivel_peligro , ta.nombre_tipo FROM alertas " +
        " JOIN alertas_tipo a on alertas.id_alerta = a.id_alerta " +
        " JOIN tipo_alerta ta on a.id_tipo = ta.id_tipo " +
        " JOIN ubicaciones u on alertas.id_ubicacion = u.id_ubicacion " +
        " JOIN provincias p on u.id_provincia = p.id_provincia " +
        " WHERE ";



//Creamos 2 formatos de fechas ya que nos vendran bien para formatear las fechas
DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd-MM-yyyy");
DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_busquedas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tx_fechaDesde = findViewById(R.id.tx_fechaDesde);
        tx_fechaHasta = findViewById(R.id.tx_fechaHasta);
        sp_nivelPeligro = findViewById(R.id.sp_nivelPeligro);
        sp_provincias = findViewById(R.id.sp_provinciasBusqueda);
        sp_tipoAlerta = findViewById(R.id.sp_tipoAlerta);
        btn_buscarAlertas = findViewById(R.id.btn_buscarAlertas);
        //Creamos 2 listeners para cada edit text y nos mostrara un dialogo con un calendario
        //para mostrar las fechas.
        tx_fechaDesde.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int día = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    BusquedasActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String fechaSeleccionada = "";
                        String day , monthFormat;
                    if(dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3 || dayOfMonth == 4
                    || dayOfMonth == 5 || dayOfMonth == 6 || dayOfMonth == 7 || dayOfMonth == 8 || dayOfMonth == 9){
                        day = "0"+dayOfMonth;
                    }
                    else {
                        day = String.valueOf(dayOfMonth);
                    }
                    //Los meses tienen de indice de 0 a 11
                    if(month != 9 && month != 10 && month != 11 ){
                        monthFormat = "0"+(month+1);
                    }
                    else{
                        monthFormat = String.valueOf(month+1);
                    }
                    fechaSeleccionada = day + "-" +monthFormat+ "-" +year;
                    tx_fechaDesde.setText(fechaSeleccionada);
                    },
                    año, mes, día
            );

            datePickerDialog.show();
        });



        tx_fechaHasta.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int día = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    BusquedasActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String fechaSeleccionada = "";
                        String day , monthFormat;
                        if(dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3 || dayOfMonth == 4
                                || dayOfMonth == 5 || dayOfMonth == 6 || dayOfMonth == 7 || dayOfMonth == 8 || dayOfMonth == 9){
                            day = "0"+dayOfMonth;
                        }
                        else {
                            day = String.valueOf(dayOfMonth);
                        }
                        //Los meses tienen de indice de 0 a 11
                        if(month != 9 && month != 10 && month != 11 ){
                            monthFormat = "0"+(month+1);
                        }
                        else{
                            monthFormat = String.valueOf(month+1);
                        }
                        fechaSeleccionada = day + "-" +monthFormat+ "-" +year;
                        tx_fechaHasta.setText(fechaSeleccionada);
                    },
                    año, mes, día
            );

            datePickerDialog.show();
        });


        //Rellenamos ahora los spinners
        try {
            provincias = usuariosDAO.getProvincias();
            nivelesPeligro = usuariosDAO.getNivelesPeligro();
            tiposAlerta = usuariosDAO.getTiposAlerta();
        } catch (SQLException e) {
            Intent intent = new Intent(this,MenuActivity.class);
            Toast.makeText(this,R.string.error_conexion,Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        provinciasSpinner = new ArrayList<>();
        for (String p : provincias) {
            Provincia provincia = new Provincia(p, false);
            provinciasSpinner.add(provincia);
        }
        //Realizamos la traduccion de los niveles de peligro aqui ya que estos adapters no reciben contexto
        //por lo que no podemos acceder a los elementos de string.xml por lo que le pasaremos
        //al adapter los elementos ya traducidos, obviamente si son en español se van a quedar igual.
        for (String n : nivelesPeligro) {
            NivelPeligro nivelPeligro = new NivelPeligro(n,false);
            switch (nivelPeligro.getNombre()) {
                case "Leve":
                   nivelPeligro.setNombre(getString(R.string.nivel_leve));
                    break;
                case "Moderado":
                    nivelPeligro.setNombre(getString(R.string.nivel_moderado));
                    break;
                case "Importante":
                    nivelPeligro.setNombre(getString(R.string.nivel_importante));
                    break;
                case "Grave":
                    nivelPeligro.setNombre(getString(R.string.nivel_grave));
                    break;
                case "Extremo":
                    nivelPeligro.setNombre(getString(R.string.nivel_extremo));
                    break;
            }
            nivelesPeligroSpinner.add(nivelPeligro);
        }

        //Hacemos lo mismo para los tipos de alerta
        for (String t : tiposAlerta){
            TipoAlerta tipoAlerta = new TipoAlerta(t,false);
            switch (tipoAlerta.getNombre()) {
                case "Lluvias y tormentas":
                    tipoAlerta.setNombre(getString(R.string.lluvias_tormentas));
                    break;
                case "Vientos":
                    tipoAlerta.setNombre(getString(R.string.viento));
                    break;
                case "Granizo":
                    tipoAlerta.setNombre(getString(R.string.granizo));
                    break;
                case "Nieve":
                    tipoAlerta.setNombre(getString(R.string.nieve));
                    break;
                case "Derrumbamientos":
                    tipoAlerta.setNombre(getString(R.string.derrumbamientos));
                    break;
            }
            tiposAlertaSpinner.add(tipoAlerta);
        }
        ProvinciasAdapter provinciaAdapter = new ProvinciasAdapter(this, provinciasSpinner);
        NivelesPeligroAdapter nivelesPeligroAdapter = new NivelesPeligroAdapter(this,nivelesPeligroSpinner);
        TiposAlertaAdapter tiposAlertaAdapter = new TiposAlertaAdapter(this,tiposAlertaSpinner);
        sp_provincias.setAdapter(provinciaAdapter);
        sp_nivelPeligro.setAdapter(nivelesPeligroAdapter);
        sp_tipoAlerta.setAdapter(tiposAlertaAdapter);


    }

    public void buscarAlertas(View view){
try{
    if(comprobarVacios()) {
        //Si las fechas son correctas , hacemos el resto
        if (comprobarFecha() == 0 || comprobarFecha() == 2) {
            getProvincias();
            getFechas();
            getNivelesPeligro();
            getTiposAlerta();
            //Reseteamos todos los contadores a 0 para que el usuario pueda volver a hacer consultas
            contadorFechas = 0;
            contadorNiveles = 0;
            contadorProvincias = 0;
            contadorTipos = 0;
            query = query;
            ArrayList<Alerta> resultados = new ArrayList<>();
            resultados = usuariosDAO.getResultadosBusqueda(query);
            defaultQuery();
            Intent intent = new Intent(this, ResultadosBusquedaActivity.class);
            intent.putExtra("resultados", resultados);
            startActivity(intent);
        }
    }
    else {
        Toast.makeText(this,"Debes introducir al menos un filtro",Toast.LENGTH_SHORT).show();
    }
        }catch (SQLException e){
            Toast.makeText(this,"ERROR EN LA BUSQUEDA",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public int comprobarFecha(){
        String fecha1 = tx_fechaDesde.getText().toString();
        String fecha2 = tx_fechaHasta.getText().toString();
        LocalDate fechaDesde = null;
        LocalDate fechaHasta = null;
        if (!fecha1.equals("") || !fecha2.equals("")) {
            if(!fecha1.equals("")) {
               fechaDesde = LocalDate.parse(tx_fechaDesde.getText().toString(), formatoEntrada);
                fechaDesde.format(formatoSalida);
            }
            if(!fecha2.equals("")) {
                fechaHasta = LocalDate.parse(tx_fechaHasta.getText().toString(), formatoEntrada);
                fechaHasta.format(formatoSalida);
            }
            if(!fecha1.equals("") && !fecha2.equals("")) {
                if (fechaDesde.isAfter(fechaHasta)) {
                    Toast.makeText(this, "Fecha hasta no puede ser mayor a fecha desde", Toast.LENGTH_SHORT).show();
                    return 1;
                } else {
                    return 0;
                }
            }
            return 0;
        }
        else{
            //En caso de que las fechas esten vacias
            return 2;
        }
    }

    public ArrayList<String> getProvincias(){
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
            try {

                ArrayList<Integer> listaIdProvincia = usuariosDAO.getListaIdProvincia(provinciaSeleccionadas);
                //Esto se hace para que la query solo se modifique una vez , ya que entramos varias veces al metodo
                if(contadorProvincias == 0) {
                    //Primero agregamos si o si la primera opcion a la query y despues si hay mas
                    // de una opcion agregamos las clausulas OR
                    query = query + "(u.id_provincia = " + listaIdProvincia.get(0);
                    if (provinciaSeleccionadas.size() > 1) {
                        for(int i = 1 ; i < provinciaSeleccionadas.size();i++){
                            query = query + " OR u.id_provincia = " + listaIdProvincia.get(i);
                            if(i == provinciaSeleccionadas.size()-1){
                               query = query+")";
                            }
                        }
                    }
                    else{
                        query = query+")";
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            contadorProvincias++;
            return provinciaSeleccionadas;
        }
    }



    public boolean getFechas() {
        String fecha1 = tx_fechaDesde.getText().toString();
        String fecha2 = tx_fechaHasta.getText().toString();
        LocalDate fechaDesde = null;
        LocalDate fechaHasta = null;

        if(!fecha1.equals("") || !fecha2.equals("")) {
            if (contadorFechas == 0) {
                if (!fecha1.equals("") && !fecha2.equals("")) {
                    fechaDesde = LocalDate.parse(tx_fechaDesde.getText().toString(), formatoEntrada);
                    fechaDesde.format(formatoSalida);
                    fechaHasta = LocalDate.parse(tx_fechaHasta.getText().toString(), formatoEntrada);
                    fechaHasta.format(formatoSalida);
                    if (getProvincias() != null) {
                        query = query + " AND alertas.fecha_alerta BETWEEN " + "'" + fechaDesde + "'" + " AND " + "'" + fechaHasta + "'";
                    } else {
                        query = query + " alertas.fecha_alerta BETWEEN " + "'" + fechaDesde + "'" + " AND " + "'" + fechaHasta + "'";
                    }
                } else {
                    if (!fecha1.equals("")) {
                        fechaDesde = LocalDate.parse(tx_fechaDesde.getText().toString(), formatoEntrada);
                        fechaDesde.format(formatoSalida);
                        if (getProvincias() != null) {
                            query = query + " AND alertas.fecha_alerta >= " + "'" + fechaDesde + "'";
                        } else {
                            query = query + " alertas.fecha_alerta >= " + "'" + fechaDesde + "'";
                        }
                    }
                    if (!fecha2.equals("")) {
                        fechaHasta = LocalDate.parse(tx_fechaHasta.getText().toString(), formatoEntrada);
                        fechaHasta.format(formatoSalida);
                        if (getProvincias() != null) {
                            query = query + " AND alertas.fecha_alerta <= " + "'" + fechaHasta + "'";
                        } else {
                            query = query + " alertas.fecha_alerta <= " + "'" + fechaHasta + "'";
                        }
                    }
                }
                contadorFechas++;
                return true;
            }
        }
        else{
            return false;
        }
        return true;
    }


    public ArrayList<String> getNivelesPeligro(){
        ArrayList<String> nivelesPeligroSeleccionados = new ArrayList<>();
        //En caso de que sean en ingles , los cambiamos a espalol (sin que el usuario lo vea)
        //y asi podemos buscar correctamente en base de datos
        for (NivelPeligro nivelPeligro : nivelesPeligroSpinner ) {
            //Nos hacemos una variable auxiliar ya que si no , nos volvera a traducir todos
            // los elementos del spinner a español, en caso de que este en ingles.
            String nivelTraducir = nivelPeligro.getNombre();
            switch (nivelPeligro.getNombre()){
                case "Mild":
                    nivelTraducir = "Leve";
                    break;
                case "Moderated":
                    nivelTraducir = "Moderado";
                    break;
                case "Important":
                    nivelTraducir = "Importante";
                    break;
                case "Serious":
                    nivelTraducir = "Grave";
                    break;
                case "Extreme":
                    nivelTraducir = "Extremo";
                    break;

            }
            if (nivelPeligro.isSeleccionada()) {
               nivelesPeligroSeleccionados.add(nivelTraducir);
            }
        }

        if (nivelesPeligroSeleccionados.isEmpty()) {
            return null;
        } else {
            if(contadorNiveles == 0) {
                //Esto comprueba que si el usuario no ha elegido provincias, empiece
                // a formar la query a partir de esta opcion
                if (getProvincias() != null || getFechas()) {
                    query = query + " AND (alertas.nivel_peligro = " + "'" + nivelesPeligroSeleccionados.get(0) + "'";
                } else {
                    query = query + " (alertas.nivel_peligro = " + "'" + nivelesPeligroSeleccionados.get(0) + "'";
                }
                //Ponemos el primer nivel seleccionado en la query y si hay mas de uno, va completando
                // con OR la sentencia
                if (nivelesPeligroSeleccionados.size() > 1) {
                    for(int i = 1 ; i < nivelesPeligroSeleccionados.size() ; i++){
                        query = query + " OR alertas.nivel_peligro = " + "'"+nivelesPeligroSeleccionados.get(i)+ "'";
                        if(i == nivelesPeligroSeleccionados.size()-1){
                            query = query+")";
                        }
                    }
                }
                else{
                    query = query+")";
                }
            }
            contadorNiveles++;
            return nivelesPeligroSeleccionados;
        }
    }

    public ArrayList<String> getTiposAlerta(){
        ArrayList<String> tiposAlertaSeleccionados = new ArrayList<>();
        for (TipoAlerta tipoAlerta : tiposAlertaSpinner ) {
            //Nos hacemos una variable auxiliar ya que si no , nos volvera a traducir todos
            // los elementos del spinner a español, en caso de que este en ingles.
            String tipoAlertaTraducir = tipoAlerta.getNombre();
            switch (tipoAlerta.getNombre()){
                case "Rain and storm":
                    tipoAlertaTraducir = "Lluvias y tormentas";
                    break;
                case "Wind":
                    tipoAlertaTraducir = "Vientos";
                    break;
                case "Snow":
                    tipoAlertaTraducir = "Nieve";
                    break;
                case "Landslides":
                    tipoAlertaTraducir = "Derrumbamientos";
                    break;
                case "Hail":
                    tipoAlertaTraducir = "Granizo";
                    break;
            }
            if (tipoAlerta.isSeleccionada()) {
                tiposAlertaSeleccionados.add(tipoAlertaTraducir);
            }
        }

        // Mostrar un Toast con las provincias seleccionadas
        if (tiposAlertaSeleccionados.isEmpty()) {
            return null;
        } else {
            if(contadorTipos == 0) {
                if (getProvincias() != null || getNivelesPeligro() != null || getFechas()) {
                    query = query + " AND (ta.nombre_tipo = " + "'" + tiposAlertaSeleccionados.get(0) + "'";
                } else {
                    query = query + " (ta.nombre_tipo = " + "'" + tiposAlertaSeleccionados.get(0) + "'";
                }
                if (tiposAlertaSeleccionados.size() > 1) {
                    for (int i = 1 ; i < tiposAlertaSeleccionados.size();i++){
                        query = query + " OR ta.nombre_tipo = " + "'"+tiposAlertaSeleccionados.get(i)+ "'";
                        if(i == tiposAlertaSeleccionados.size()-1){
                            query = query+")";
                        }
                    }
                }
                else {
                    query = query+")";
                }
            }
        }
        contadorTipos++;
        return tiposAlertaSeleccionados;
    }

    public void defaultQuery(){
        query = "SELECT alertas.id_alerta ,TO_CHAR(alertas.fecha_alerta, 'DD-MM-YYYY') AS fecha_alerta, u.nombre_ubicacion," +
                " p.nombre_provincia , alertas.nivel_peligro , ta.nombre_tipo FROM alertas " +
                " JOIN alertas_tipo a on alertas.id_alerta = a.id_alerta " +
                " JOIN tipo_alerta ta on a.id_tipo = ta.id_tipo " +
                " JOIN ubicaciones u on alertas.id_ubicacion = u.id_ubicacion " +
                " JOIN provincias p on u.id_provincia = p.id_provincia " +
                " WHERE ";
    }

    public boolean comprobarVacios(){
        if(getTiposAlerta() == null && getProvincias() == null && getNivelesPeligro() == null
       && getFechas() == false ){
            return false;
        }
        else{
            return true;
        }
    }
}