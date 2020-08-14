package com.geekbrains.cloud_client.models;

import com.geekbrains.common.commands.AuthCommand;
import com.geekbrains.common.commands.ServerResponse;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class LoginService {

    private final String host;
    private final int port;

  //  private ClientController controller;

    private Socket socket;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    public LoginService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
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

    public ServerResponse sendAuthCommand(String login, String password) {
        try {
            AuthCommand auth = new AuthCommand(login, password);
            out.writeObject(auth);
            out.flush();
            ServerResponse serverResponse = (ServerResponse) in.readObject();
            return serverResponse;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
