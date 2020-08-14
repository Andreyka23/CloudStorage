package com.geekbrains.cloud_server;

public class ServerApp {

    private static final int DEFAULT_PORT = 8189;

    public static void main(String[] args) throws Exception {
        new NetworkServer(DEFAULT_PORT).start();
    }
}
