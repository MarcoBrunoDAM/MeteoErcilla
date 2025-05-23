package com.example.meteoercilla.dao;

import com.example.meteoercilla.database.SQLDatabaseManagerRemota;
import com.example.meteoercilla.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuariosDAO {
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



    //Usamos int como valores de retorno para saber exactamente lo que nos devuelve en caso de haber un error
    public int registroUsuario(Usuario usuario) throws SQLException {
        ResultSet resultSet = null;

        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        try{
            String query = " SELECT registrar_usuario(?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usuario.getNombre());
            preparedStatement.setString(2, usuario.getApellidos());
            preparedStatement.setString(3, usuario.getCorreo());
            preparedStatement.setString(4, usuario.getContrasena());
            preparedStatement.setString(5, usuario.getTelefono());
            resultSet = preparedStatement.executeQuery();
            //Como el procedimiento de registrar devuelve un entero , usamos ese valor para saber exactamente que ha pasado
            if( resultSet.next()){
                if(resultSet.getInt(1) == 0){
                    return 0;
                }
                else if ( resultSet.getInt(1) == 1){
                    return 1;
                }
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }



        return 2;
    }




    //Usamos int como valores de retorno para saber exactamente lo que nos devuelve en caso de haber un error
    public int loginUsuario(String usuario , String password) throws SQLException {
        ResultSet resultSet = null;
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        try{
            String query = "SELECT login_usuario(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            //Como el procedimiento de registrar devuelve un entero , usamos ese valor para saber exactamente que ha pasado
            if( resultSet.next()){
                if(resultSet.getInt(1) == 0){
                    return 0;
                }
                else if ( resultSet.getInt(1) == 1){
                    return 1;
                }
                else if (resultSet.getInt(1) == 2){
                    return 2;
                }
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return 3;
    }


    public int getIdUsuario(String correo) throws SQLException {
        ResultSet resultSet = null;


        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        try{
            String query = "SELECT id_usuario FROM usuarios WHERE correo = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, correo);
            resultSet = preparedStatement.executeQuery();
            //Como el procedimiento de registrar devuelve un entero , usamos ese valor para saber exactamente que ha pasado
            if( resultSet.next()){
                int idUsuario = resultSet.getInt(1);
                return idUsuario;
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return 0;
    }

    //Usamos int como valores de retorno para saber exactamente lo que nos devuelve en caso de haber un error
    public boolean registrarProvincias(ArrayList<Integer> provincias, int idUsuario) throws SQLException {
        ResultSet resultSet = null;
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }
        for (int idProvincia : provincias) {
            try {
                String query = "INSERT INTO usuario_provincia (id_usuario,id_provincia) VALUES (?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1,idUsuario);
                preparedStatement.setInt(2,idProvincia);
                preparedStatement.executeUpdate();

            } catch (Exception e) {
                throw new SQLException("Error al realizar la consulta");
            }

        }
        return true;
    }

    public void eliminarProvincias(int idUsuario) throws SQLException {
        if (!initDBConnection()) {
            throw new SQLException("Error al conectar a base de datos");
        }
        try {
            String query = "DELETE  FROM usuario_provincia WHERE id_usuario = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idUsuario);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


    }


    public ArrayList<String> getNombresProvinciaByID(int idUsuario) throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        ResultSet resultSet = null;
        ArrayList<java.lang.String> listaProvincias = new ArrayList<>();

        try{
            java.lang.String query = "SELECT provincias.nombre_provincia FROM provincias\n" +
                    "JOIN usuario_provincia up on provincias.id_provincia = up.id_provincia\n" +
                    "WHERE id_usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idUsuario);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                listaProvincias.add(resultSet.getString(1));
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return listaProvincias;
    }


    public String getCorreoById(int idUsuario) throws SQLException {
        ResultSet resultSet = null;


        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        try{
            String query = "SELECT correo FROM usuarios WHERE id_usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idUsuario);
            resultSet = preparedStatement.executeQuery();
            //Como el procedimiento de registrar devuelve un entero , usamos ese valor para saber exactamente que ha pasado
            if( resultSet.next()){
                String correo = resultSet.getString(1);
                return correo;
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }


        return null;
    }

    public String getTelefonoById(int idUsuario) throws SQLException {
        ResultSet resultSet = null;


        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        try{
            String query = "SELECT telefono FROM usuarios WHERE id_usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idUsuario);
            resultSet = preparedStatement.executeQuery();
            //Como el procedimiento de registrar devuelve un entero , usamos ese valor para saber exactamente que ha pasado
            if( resultSet.next()){
                String telefono = resultSet.getString(1);
                return telefono;
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }

        return null;
    }


    public int actualizarCorreo(String correo, int idUsuario ) throws SQLException {
        ResultSet resultSet = null;

        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        try{
            String query = " SELECT actualizar_correo(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, correo);
            preparedStatement.setInt(2, idUsuario);
            resultSet = preparedStatement.executeQuery();
            //Como el procedimiento de registrar devuelve un entero , usamos ese valor para saber exactamente que ha pasado
            if( resultSet.next()){
                if(resultSet.getInt(1) == 0){
                    return 0;
                }
                else if ( resultSet.getInt(1) == 1){
                    return 1;
                }
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }



        return 2;
    }


    public int actualizarTelefono(String telefono, int idUsuario ) throws SQLException {
        ResultSet resultSet = null;

        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        try{
            String query = " SELECT actualizar_telefono(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, telefono);
            preparedStatement.setInt(2, idUsuario);
            resultSet = preparedStatement.executeQuery();
            //Como el procedimiento de registrar devuelve un entero , usamos ese valor para saber exactamente que ha pasado
            if( resultSet.next()){
                if(resultSet.getInt(1) == 0){
                    return 0;
                }
                else if ( resultSet.getInt(1) == 1){
                    return 1;
                }
            }

        } catch (Exception e) {
            throw new SQLException("Error al realizar la consulta");
        }



        return 2;
    }

}
