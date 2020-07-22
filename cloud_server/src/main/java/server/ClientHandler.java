package server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ClientHandler {
    private final NetworkServer networkServer;
    private final Socket clientSocket;

    private DataInputStream in;
    private DataOutputStream out;

    static int idClient;
    private int currentClientId;

    public ClientHandler(NetworkServer networkServer, Socket socket) {
        this.networkServer = networkServer;
        this.clientSocket = socket;
    }

    public void run() {
        currentClientId = idClient + 1;
        idClient++;
        doHandle(clientSocket);
    }

    private void doHandle(Socket socket) {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            new Thread(() -> {
                try {
                    System.out.println("Начинаем читать сообщения клиента " + currentClientId);
                    readMessages();
                    System.out.println("Клиент отключился");
                } catch (IOException e) {
                    System.out.println("Соединение с клиентом было закрыто!");
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("Сообщение: " + message);
            if ("/exit".equals(message)) {
                return;
            }
            if (message.startsWith("/upload")) {
                String[] messageParts = message.split("\\s+");
                String filename = messageParts[1];
                System.out.println(filename);
                try {
                    uploadFile(filename);
                } catch (IOException e) {
                    System.out.println("Ошибка загрузки файла!");
                }
            }

        }
    }

    private void uploadFile(String fileName) throws IOException {

        File file = new File("./common/server/" + fileName);
        file.createNewFile();
        try (FileOutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            while (true) {
                int r = in.read(buffer);
                if (r == -1) break;
                os.write(buffer, 0, r);
            }
        }
        System.out.println("File uploaded!");
    }

}
