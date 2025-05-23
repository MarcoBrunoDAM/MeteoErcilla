package com.example.meteoercilla.dao;

import com.example.meteoercilla.database.SQLDatabaseManagerRemota;
import com.example.meteoercilla.models.Alerta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlertasDAO {
    private Connection connection;

    public boolean initDBConnection() {
        try{
            //this.connection = SQLDatabaseManager.connect();
            this.connection = SQLDatabaseManagerRemota.connect();
            if(connection != null){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    private boolean closeDBConnection() {
        try {
            // SQLDatabaseManager.cerrarConexion(connection);
            SQLDatabaseManagerRemota.cerrarConexion(connection);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public ArrayList<Integer> getIDsProvinciaByID(int idUsuario) throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        ResultSet resultSet = null;
        ArrayList<Integer> listaProvincias = new ArrayList<>();

        try{
            String query = "SELECT usuario_provincia.id_provincia FROM usuario_provincia WHERE id_usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idUsuario);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                listaProvincias.add(resultSet.getInt(1));
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return listaProvincias;
    }



    public int getIdProvinciaByNombre(String nombreProvncia) throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }
        int idProvincia = 0;
        ResultSet resultSet = null;
        ArrayList<String> listaProvincias = new ArrayList<>();

        try{
            String query = "SELECT id_provincia FROM provincias WHERE nombre_provincia = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,nombreProvncia);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                idProvincia = resultSet.getInt(1);
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return idProvincia;
    }


    //Esto crea una lista de alertas en funcion de las provincias que el usuario tenga registradas
    //por ejemplo si son Madrid y Toledo guardara todas las alertas de esas 2 provincias
    //que vayan a ocurrir entre el dia actual a 2 dias. Estas alertas seran candidatas
    //a ser notificadas.
    public ArrayList<Alerta> getAlertasServicio(ArrayList<Integer> listaProvincias , int ultimaUbicacion) throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        ResultSet resultSet = null;
        ArrayList<Alerta> resultados = new ArrayList<>();
        String query = "";
        try {
            //En caso de que haya una lista de provincias registrada para ese usuario.
            if (listaProvincias != null) {
                for (int provinciaID : listaProvincias) {
                    query = "SELECT alertas.id_alerta ,TO_CHAR(alertas.fecha_alerta, 'DD-MM-YYYY') AS fecha_alerta, u.nombre_ubicacion,\n" +
                            "p.nombre_provincia , alertas.nivel_peligro , ta.nombre_tipo FROM alertas\n" +
                            "JOIN alertas_tipo a on alertas.id_alerta = a.id_alerta\n" +
                            "JOIN tipo_alerta ta on a.id_tipo = ta.id_tipo\n" +
                            "JOIN ubicaciones u on alertas.id_ubicacion = u.id_ubicacion\n" +
                            "JOIN provincias p on u.id_provincia = p.id_provincia\n" +
                            " WHERE (u.id_provincia = ? OR u.id_provincia = ?) AND fecha_alerta >= CURRENT_DATE\n" +
                            "AND alertas.fecha_alerta <= CURRENT_DATE + INTERVAL '2 days'";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, provinciaID);
                    preparedStatement.setInt(2, ultimaUbicacion);
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        int idAlerta = resultSet.getInt(1);
                        String fecha = resultSet.getString(2);
                        String ubicacion = resultSet.getString(3);
                        String provincia = resultSet.getString(4);
                        String nivelPeligro = resultSet.getString(5);
                        String tipoAlerta = resultSet.getString(6);
                        String icono = "";
                        //Con esto asignamos un nombre de archivo a cada tipo y esto lo usaremos para asignarle
                        // las imagenes
                        switch (tipoAlerta) {
                            case "Lluvias y tormentas":
                                icono = "storm.json";
                                break;
                            case "Vientos":
                                icono = "wind.json";
                                break;
                            case "Granizo":
                                icono = "hail.json";
                                break;
                            case "Nieve":
                                icono = "snow.json";
                                break;
                            case "Derrumbamientos":
                                icono = "landslides.json";
                                break;
                        }
                        Alerta alerta = new Alerta(idAlerta, ubicacion, provincia, nivelPeligro, fecha, tipoAlerta, icono);
                        resultados.add(alerta);
                    }
                }
            }
            //En caso de que no haya una lista de provincias registradas , solo se busca con la ubicacion
            // generalmente esto ocurrre cuando no hay ningun usuario logeado.
            else {
                query = "SELECT alertas.id_alerta ,TO_CHAR(alertas.fecha_alerta, 'DD-MM-YYYY') AS fecha_alerta, u.nombre_ubicacion,\n" +
                        "p.nombre_provincia , alertas.nivel_peligro , ta.nombre_tipo FROM alertas\n" +
                        "JOIN alertas_tipo a on alertas.id_alerta = a.id_alerta\n" +
                        "JOIN tipo_alerta ta on a.id_tipo = ta.id_tipo\n" +
                        "JOIN ubicaciones u on alertas.id_ubicacion = u.id_ubicacion\n" +
                        "JOIN provincias p on u.id_provincia = p.id_provincia\n" +
                        " WHERE (u.id_provincia = ?) AND fecha_alerta >= CURRENT_DATE\n" +
                        "AND alertas.fecha_alerta <= CURRENT_DATE + INTERVAL '2 days'";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, ultimaUbicacion);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int idAlerta = resultSet.getInt(1);
                    String fecha = resultSet.getString(2);
                    String ubicacion = resultSet.getString(3);
                    String provincia = resultSet.getString(4);
                    String nivelPeligro = resultSet.getString(5);
                    String tipoAlerta = resultSet.getString(6);
                    String icono = "";
                    //Con esto asignamos un nombre de archivo a cada tipo y esto lo usaremos para asignarle
                    // las imagenes
                    switch (tipoAlerta) {
                        case "Lluvias y tormentas":
                            icono = "storm.json";
                            break;
                        case "Vientos":
                            icono = "wind.json";
                            break;
                        case "Granizo":
                            icono = "hail.json";
                            break;
                        case "Nieve":
                            icono = "snow.json";
                            break;
                        case "Derrumbamientos":
                            icono = "landslides.json";
                            break;
                    }
                    Alerta alerta = new Alerta(idAlerta, ubicacion, provincia, nivelPeligro, fecha, tipoAlerta, icono);
                    resultados.add(alerta);
                }
            }
        } catch(Exception e){
            throw new SQLException("Error al realizar la consulta");
        }
        //Antes de devolver las alertas , comprobamos que no existan 2 objetos de tipo alerta
        // que sean identicos ya que si no vamos a recibir varias notificaciones de la misma alerta
        //En este caso para comparar uso tanto la id de la alerta como su tipo ya que el metodo equals()
        //No funciona como esperaba

        for(int i = 0 ; i < resultados.size(); i++){
            for(int j = i + 1 ; j < resultados.size() ;j++){
                if(resultados.get(i).getIdAlerta() == resultados.get(j).getIdAlerta() &&
                        resultados.get(i).getTipoAlerta().equals(resultados.get(j).getTipoAlerta())){
                    resultados.remove(resultados.get(j));
                    j--;
                }
            }
        }
        return resultados;
    }


    public boolean notificarAlerta(int idUsuario,int idAlerta) throws SQLException {
        ArrayList<Integer> idsProvincia = new ArrayList<>();
        if (!initDBConnection()) {
            throw new SQLException("Error al conectar a base de datos");
        }
        ResultSet resultSet = null;
        try {
            String query = "INSERT INTO usuario_alerta(id_usuario, id_alerta, notificada) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idUsuario);
            preparedStatement.setInt(2,idAlerta);
            preparedStatement.setBoolean(3,true);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                return true;
            }
            else{
                return false;
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }



    }


    public boolean comprobarAlertaNotificada(int idUsuario,int idAlerta) throws SQLException {
        ArrayList<Integer> idsProvincia = new ArrayList<>();
        if (!initDBConnection()) {
            throw new SQLException("Error al conectar a base de datos");
        }
        ResultSet resultSet = null;
        try {
            String query = "SELECT notificada FROM usuario_alerta WHERE id_usuario = ? AND id_alerta = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idUsuario);
            preparedStatement.setInt(2,idAlerta);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                if(resultSet.getBoolean(1) == true){
                    return true;
                }
            }
            else {
                return false;
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return false;
    }

}
