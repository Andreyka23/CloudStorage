package com.geekbrains.cloud_server.auth;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseAuthService implements AuthService {

    private static class UserData {
        private String login;
        private String password;
        private String username;

        public UserData(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    public static Connection getMySQLConnection() throws SQLException, ClassNotFoundException {
        String hostname = "localhost";
        String dbName = "java_chat";
        String user = "root";
        String password = "";

        return getMySQLConnection(hostname, dbName, user, password);
    }

    private static Connection getMySQLConnection(
            String hostname,
            String dbName,
            String user,
            String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        String connectionURL = "jdbc:mysql://" + hostname + ":3306/" + dbName;
        Connection connection = DriverManager.getConnection(connectionURL, user, password);
        return connection;
    }

    private static final List<UserData> USER_DATA = new ArrayList<>(Arrays.asList( // List.of(
            new UserData("login1", "pass1", "username1"),
            new UserData("login2", "pass2", "username2"),
            new UserData("login3", "pass3", "username3")
    ));

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        /*
        try {
            Connection connect = getMySQLConnection();
            Statement statement = connect.createStatement();
            // statement.execute(" insert into users (user, password, username) values('login1', 'pass1', 'username1');");
            // statement.execute(" insert into users (user, password, username) values('login2', 'pass2', 'username2');");
            // statement.execute(" insert into users (user, password, username) values('login3', 'pass3', 'username3');");

            ResultSet rs = statement.executeQuery(" SELECT * FROM users");
            while (rs.next()) {
                 String loginFromDB = rs.getString("login");
                 String passwordFromDB = rs.getString("password");
                 String usernameFromDB = rs.getString("username");


                if (loginFromDB.equals(login) && passwordFromDB.equals(password)) {
                    return usernameFromDB;
                }

            }
        } catch (Exception e) {
            System.out.println("Ошибка соединения с базой данных");
        }
         */

        for (UserData userDatum : USER_DATA) {
            if (userDatum.login.equals(login) && userDatum.password.equals(password)) {
                return userDatum.username;
            }
        }
        return null;
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации оставлен");
    }

}
