package com.geekbrains.cloud_client.models;

import com.geekbrains.common.commands.AbstractCommand;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class NetworkService {

    private final String host;
    private final int port;

  //  private ClientController controller;

    private Socket socket;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
      //  this.controller = controller;
        /*
        socket = new Socket(host, port);
        //  socket.setSoTimeout(120000);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        runWriteThread();
        runReadThread();
         */

        try  {
            socket = new Socket(host, port);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), 100 * 1024 * 1024);
            /*
            FileRequestCommand textMessage = new FileRequestCommand("Hello Server!!!");
            oeos.writeObject(textMessage);
            oeos.flush();
            FileRequestCommand msgFromServer = (FileRequestCommand) odis.readObject();
            System.out.println("Answer from server: " + msgFromServer.getFilePath());
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean sendCommand(AbstractCommand msg) {
        try {
            out.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public AbstractCommand readObject() throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
        return (AbstractCommand) obj;
    }


    public void stop() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    /*


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
     */

}
