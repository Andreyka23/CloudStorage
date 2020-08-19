package com.geekbrains.cloud_client.controllers;

import com.geekbrains.cloud_client.models.LoginService;
import com.geekbrains.common.commands.ServerResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private LoginService loginService;

    @FXML
    public TextField login;

    @FXML
    public TextField password;

    @FXML
    public Button authBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginService = new LoginService(HOST, PORT);
        try {
            loginService.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickAuthButton(ActionEvent actionEvent) throws Exception {
        String login1 = login.getText();
        String pass1 = password.getText();
        if ( ! login1.trim().isEmpty() && ! pass1.trim().isEmpty() ) {
            ServerResponse serverResponse = loginService.sendAuthCommand(login1, pass1);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Авторизация.");
            alert.setHeaderText(serverResponse.getMessage());
            alert.showAndWait();

            if ( serverResponse.getResult()) {
                //Show main window
                Stage primaryStage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
                Parent root = fxmlLoader.load();
                primaryStage.setTitle("Client");
                primaryStage.setScene(new Scene(root));
                primaryStage.show();

                MainController mainController = fxmlLoader.getController();
                mainController.setLogin(login1);

                //close login window
                Stage stage = (Stage) authBtn.getScene().getWindow();
                stage.close();

            }

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Введите логин и пароль.");
            alert.setHeaderText("Вы забыли ввести логин или пароль.");
            alert.showAndWait();
        }

    }

}
