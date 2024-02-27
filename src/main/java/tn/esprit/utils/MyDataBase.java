package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String URL= "jdbc:mysql://localhost:3307/e_city_modifuser";
    private final String USER= "root";
    private final String PWD= "0000";

    private Connection connection;
    public static MyDataBase instance;
    private MyDataBase(){
        try {
            connection = DriverManager.getConnection(URL,USER,PWD);
            System.out.println("Connected");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance(){
        if(instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

}