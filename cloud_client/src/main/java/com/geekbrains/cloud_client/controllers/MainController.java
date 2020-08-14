package com.geekbrains.cloud_client.controllers;

import com.geekbrains.cloud_client.models.NetworkService;
import com.geekbrains.common.commands.AbstractCommand;
import com.geekbrains.common.commands.FileDataCommand;
import com.geekbrains.common.commands.FileRequestCommand;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private NetworkService networkService;

    @FXML
    ListView<String> clientFilesList;

    @FXML
    ListView<String> serverFilesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = new NetworkService(HOST, PORT);
        try {
            networkService.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractCommand am = networkService.readObject();
                    if (am instanceof FileDataCommand) {
                        FileDataCommand fData = (FileDataCommand) am;
                        Files.write(Paths.get("client_storage/" + fData.getFilename()), fData.getData(), StandardOpenOption.CREATE);
                        refreshClientFilesList();
                        System.out.println("Файл скачан: " + fData.getFilename());
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                networkService.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshClientFilesList();
        refreshServerFilesList();
    }

    public void pressOnUploadBtn(ActionEvent actionEvent) {
        String uploadFile = clientFilesList.getSelectionModel().getSelectedItem();
        if (uploadFile != null) {
            System.out.println("Закачивание файла: " + uploadFile);
        } else
            System.out.println("Выберите файл!");
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        String downloadFile = serverFilesList.getSelectionModel().getSelectedItem();
        System.out.println("Скачивание файла: " + downloadFile);
        networkService.sendCommand(new FileRequestCommand(downloadFile));
    }

    public void refreshClientFilesList() {
        Platform.runLater(() -> {
            try {
                clientFilesList.getItems().clear();
                Files.list(Paths.get("client_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> clientFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void refreshServerFilesList() {
        // TODO изменить на получение файлов с сервера
        Platform.runLater(() -> {
            try {
                serverFilesList.getItems().clear();
                Files.list(Paths.get("server_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> serverFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
