package com.example.meteoercilla.dao;

import com.example.meteoercilla.database.SQLDatabaseManagerRemota;
import com.example.meteoercilla.models.Alerta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BusquedasDAO {
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


    public ArrayList<Alerta> getResultadosBusqueda(String query) throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        ResultSet resultSet = null;
        ArrayList<Alerta> resultados = new ArrayList<>();

        try{
            //Le añadimos esto a la query para que solo muestre resultados cuya fecha sean mayor a la de hoy
            String querySelect = query + " AND (fecha_alerta >= CURRENT_DATE) ORDER BY alertas.fecha_alerta ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                int idAlerta = resultSet.getInt(1);
                String fecha = resultSet.getString(2);
                String ubicacion = resultSet.getString(3);
                String provincia = resultSet.getString(4);
                String nivelPeligro = resultSet.getString(5);
                String tipoAlerta = resultSet.getString(6);
                String icono = "";
                //Con esto asignamos un numero a cada tipo y esto lo usaremos para asignarle
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
                Alerta alerta = new Alerta(idAlerta,ubicacion,provincia,nivelPeligro,fecha,tipoAlerta,icono);
                resultados.add(alerta);
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return resultados;
    }
}
