package se.ecutbildning.hala.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    // if you really want to expose your password use this:
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/todo_db?&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root",
                "1234");
        return connection;
    }

    public static Connection altGetConnection() throws SQLException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("connection.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(url,user,password);
    }

}
