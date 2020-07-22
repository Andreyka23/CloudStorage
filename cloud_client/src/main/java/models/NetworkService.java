package models;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import controllers.ClientController;

public class NetworkService {

    private final String host;
    private final int port;

    private ClientController controller;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect(ClientController controller) throws IOException {
        this.controller = controller;
        socket = new Socket(host, port);
        //  socket.setSoTimeout(120000);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        runWriteThread();
        runReadThread();
    }

    private void runWriteThread() {
        new Thread(() -> {
            Scanner input = new Scanner(System.in);
            while (true) {
                try {
                    System.out.print("Введите комманду: ");
                    String command = input.nextLine();
                    out.writeUTF(command);

                    if (command.equals("/exit")) {
                        input.close();
                        close();
                        break;
                    }

                    if (command.startsWith("/upload")) {
                        String[] messageParts = command.split("\\s+");
                        String filename = messageParts[1];
                        System.out.println(filename);
                        try {
                            uploadFile(filename);
                        } catch (IOException e) {
                            System.out.println("Ошибка загрузки файла!");
                        }
                    }

                } catch (IOException e) {
                    input.close();
                    System.out.println("Поток записи был прерван!");
                    return;
                }
            }
        }).start();
    }

    public void uploadFile(String filename) throws IOException {
        File file = new File("./common/" + filename);
        InputStream is = new FileInputStream(file);
        long size = file.length();
        int count = (int) (size / 8192) / 10, readBuckets = 0;
        // /==========/
        try(DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
            byte [] buffer = new byte[8192];
            os.writeUTF(file.getName());
            System.out.print("/");
            while (is.available() > 0) {
                int readBytes = is.read(buffer);
                readBuckets++;
                if (count != 0 && readBuckets % count == 0) {
                    System.out.print("=");
                }
                os.write(buffer, 0, readBytes);
            }
            System.out.println("/");
        }
    }

    private void runReadThread() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = in.readUTF();
                    switch (message) {
                        case "exit": {
                            close();
                            break;
                        }
                        case "ololo": {
                            System.out.println("ololo");
                            break;
                        }
                        default:
                            System.err.println("Unknown type of command: ");
                    }
                } catch (IOException e) {
                    System.out.println("Поток чтения был прерван!");
                    return;
                }
            }
        }).start();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
