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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class MainController implements Initializable {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private NetworkService networkService;

    private String userLogin;

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
        CountDownLatch cdl = new CountDownLatch(1);
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractCommand am = networkService.readObject();
                    if (am instanceof FileDataCommand) {
                        FileDataCommand fData = (FileDataCommand) am;

                        boolean append = true;
                        if (fData.partNumber == 1) {
                            append = false;
                        }
                        System.out.println(fData.partNumber + " / " + fData.partsCount);
                        FileOutputStream fos = new FileOutputStream("client_storage/" + fData.getFilename(), append);
                        fos.write(fData.data);
                        fos.close();
                        if (fData.partNumber == fData.partsCount) {
                            cdl.countDown();
                            refreshClientFilesList();
                        }
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
        // cdl.await();
        refreshClientFilesList();
        refreshServerFilesList();
    }

    public void setLogin(String login) {
        this.userLogin = login;
    }

    public void pressOnUploadBtn(ActionEvent actionEvent) throws IOException, InterruptedException {
        String uploadFile = clientFilesList.getSelectionModel().getSelectedItem();
        if (uploadFile != null) {
            System.out.println("Закачивание файла: " + uploadFile);

            if (Files.exists(Paths.get("client_storage/" + uploadFile))) {

                File file = new File("client_storage/" + uploadFile );
                int bufSize = 1024 * 1024 * 10;
                int partsCount = new Long(file.length() / bufSize).intValue();
                if (file.length() % bufSize != 0) {
                    partsCount++;
                }
                FileDataCommand fmOut = new FileDataCommand(uploadFile, -1, partsCount, new byte[bufSize]);
                FileInputStream in = new FileInputStream(file);
                for (int i = 0; i < partsCount; i++) {
                    int readedBytes = in.read(fmOut.getData());
                    fmOut.partNumber = i + 1;
                    if (readedBytes < bufSize) {
                        fmOut.data = Arrays.copyOfRange(fmOut.data, 0, readedBytes);
                    }
                    networkService.sendCommand(fmOut);
                    System.out.println("Отправлена часть #" + (i + 1));
                }
                in.close();

                Thread.sleep(100);
                refreshServerFilesList();
            }

        } else
            System.out.println("Выберите файл!");
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        String downloadFile = serverFilesList.getSelectionModel().getSelectedItem();
        if (downloadFile != null) {
            System.out.println("Скачивание файла: " + downloadFile);
            networkService.sendCommand(new FileRequestCommand(downloadFile));
        } else
            System.out.println("Выберите файл!");
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
