package com.example.meteoercilla.dao;

import com.example.meteoercilla.database.SQLDatabaseManagerRemota;
import com.example.meteoercilla.models.Alerta;
import com.example.meteoercilla.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MeteoErcillaDAO {
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







    public ArrayList<String> getProvincias() throws SQLException {
        if(!initDBConnection()){
            return null;
        }

        ResultSet resultSet = null;
        ArrayList<String> provincias = new ArrayList<>();

        try{
            String query = "SELECT * FROM provincias ORDER BY id_provincia ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                String provincia = resultSet.getString("nombre_provincia");
                provincias.add(provincia);
            }

        } catch (Exception e) {
            return null;
           // throw new SQLException("Error al realizar la consulta");
        }


        return provincias;
    }



    public ArrayList<String> getTiposAlerta() throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        ResultSet resultSet = null;
        ArrayList<String> tiposAlerta = new ArrayList<>();

        try{
            String query = "SELECT * FROM tipo_alerta";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                String tipoAlerta = resultSet.getString("nombre_tipo");
                tiposAlerta.add(tipoAlerta);
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return tiposAlerta;
    }


    public ArrayList<String> getNivelesPeligro() throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        ResultSet resultSet = null;
        ArrayList<String> nivelesPeligro = new ArrayList<>();
        ArrayList<String> nivelesPeligroOrdenada = new ArrayList<>();
        //Rellenamos el array con texto para que no salte excepcion cuando lo rellenemos
        nivelesPeligroOrdenada.add("");
        nivelesPeligroOrdenada.add("");
        nivelesPeligroOrdenada.add("");
        nivelesPeligroOrdenada.add("");
        nivelesPeligroOrdenada.add("");

        try{
            String query = "SELECT DISTINCT nivel_peligro FROM alertas";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                String nivelPeligro = resultSet.getString("nivel_peligro");
                nivelesPeligro.add(nivelPeligro);
            }
            for(int i = 0; i < nivelesPeligro.size();i++){
                if(nivelesPeligro.get(i).equals("Leve")){
                    nivelesPeligroOrdenada.set(0,nivelesPeligro.get(i));
                }
                if(nivelesPeligro.get(i).equals("Moderado")){
                    nivelesPeligroOrdenada.set(1,nivelesPeligro.get(i));
                }
                if(nivelesPeligro.get(i).equals("Importante")){
                    nivelesPeligroOrdenada.set(2,nivelesPeligro.get(i));
                }
                if(nivelesPeligro.get(i).equals("Grave")){
                    nivelesPeligroOrdenada.set(3,nivelesPeligro.get(i));
                }
                if(nivelesPeligro.get(i).equals("Extremo")){
                    nivelesPeligroOrdenada.set(4,nivelesPeligro.get(i));
                }
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return nivelesPeligroOrdenada;
    }

    public ArrayList<Integer> getListaIdProvincia(ArrayList<String> provincias) throws SQLException {
        ArrayList<Integer> idsProvincia = new ArrayList<>();
        if (!initDBConnection()) {
            throw new SQLException("Error al conectar a base de datos");
        }
        for (String provincia : provincias) {
            ResultSet resultSet = null;
            try {
                String query = "SELECT id_provincia FROM provincias WHERE nombre_provincia = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,provincia);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int idProvincia = resultSet.getInt(1);
                    idsProvincia.add(idProvincia);
                }

            } catch (Exception e) {
                throw new SQLException("Error al realizar la consulta");
            }

        }
        return idsProvincia;
    }



}
