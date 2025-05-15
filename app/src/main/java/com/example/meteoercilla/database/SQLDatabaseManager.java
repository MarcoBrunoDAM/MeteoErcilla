package com.example.meteoercilla.database;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDatabaseManager {
    private static Connection conexion = null;

    private static final String DRIVER = "org.postgresql.Driver";
    //private static final String URL = "jdbc:postgresql://10.0.2.2:5433/meteoercilla";
    private static final String URL = "jdbc:postgresql://192.168.4.26:5433/meteoercilla";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "1234";

    // Creamos nuestra función para conectarnos a PostgreSQL
    public static Connection connect() throws SQLException {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error en la conexión a BBDD");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error con el DRIVER de la base de datos");
            e.printStackTrace();
        }
        return conexion;
    }

    // Creamos la función para cerrar la conexión con la base de datos
    public static void cerrarConexion(Connection conexion) throws SQLException {
        conexion.close();
    }
}
