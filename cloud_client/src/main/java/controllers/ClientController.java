package controllers;

import models.NetworkService;

import java.io.IOException;

public class ClientController {

    private final NetworkService networkService;

    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort);
    }

    public void runApplication() throws IOException {
        connectToServer();
    }

    private void connectToServer() throws IOException {
        try {
            networkService.connect(this);
        } catch (IOException e) {
            System.err.println("Failed to establish server connection");
            throw e;
        }
    }

}
