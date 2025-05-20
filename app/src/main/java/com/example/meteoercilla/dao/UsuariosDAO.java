package com.example.meteoercilla.dao;

import com.example.meteoercilla.R;
import com.example.meteoercilla.database.SQLDatabaseManager;
import com.example.meteoercilla.database.SQLDatabaseManagerRemota;
import com.example.meteoercilla.models.Alerta;
import com.example.meteoercilla.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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



    //Usamos int como valores de retorno para saber exactamente lo que nos devuelve en caso de haber un error
    public boolean registrarProvincias(ArrayList<Integer> provincias,int idUsuario) throws SQLException {
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


    public ArrayList<String> getNombresProvinciaByID(int idUsuario) throws SQLException {
        if(!initDBConnection()){
            throw new SQLException("Error al conectar a base de datos");
        }

        ResultSet resultSet = null;
        ArrayList<String> listaProvincias = new ArrayList<>();

        try{
            String query = "SELECT provincias.nombre_provincia FROM provincias\n" +
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
