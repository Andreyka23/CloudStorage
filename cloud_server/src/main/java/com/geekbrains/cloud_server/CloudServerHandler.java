package com.geekbrains.cloud_server;

import com.geekbrains.cloud_server.services.NetworkService;
import com.geekbrains.common.commands.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //System.out.println(msg.getClass().getName());
        if (msg instanceof AuthCommand) {
            String login = ((AuthCommand) msg).getLogin();
            String pass = ((AuthCommand) msg).getPassword();
            NetworkService networkService = new NetworkService();
            boolean is_success_login = networkService.checkLogin(login, pass);
            if (is_success_login) {
                ServerResponse serverResponse = new ServerResponse(is_success_login, "Пользователь успешно авторизован!");
                ctx.writeAndFlush(serverResponse);
            } else {
                ServerResponse serverResponse = new ServerResponse(is_success_login, "Неверный логин или пароль!");
                ctx.writeAndFlush(serverResponse);
            }

        } else if (msg instanceof FileRequestCommand) {
            System.out.println("Client text message: " + ((FileRequestCommand) msg).getFilename());
          //  ctx.writeAndFlush(new FileRequestCommand("Hello Client!"));
            FileRequestCommand fr = (FileRequestCommand) msg;
            if (Files.exists(Paths.get("server_storage/" + fr.getLogin() + "/" + fr.getFilename()))) {

                File file = new File("server_storage/" + fr.getLogin() + "/" + fr.getFilename() );
                int bufSize = 1024 * 1024 * 10;
                int partsCount = new Long(file.length() / bufSize).intValue();
                if (file.length() % bufSize != 0) {
                    partsCount++;
                }
                FileDataCommand fmOut = new FileDataCommand(fr.getLogin(), fr.getFilename(), -1, partsCount, new byte[bufSize]);
                FileInputStream in = new FileInputStream(file);
                for (int i = 0; i < partsCount; i++) {
                    int readedBytes = in.read(fmOut.getData());
                    fmOut.partNumber = i + 1;
                    if (readedBytes < bufSize) {
                        fmOut.data = Arrays.copyOfRange(fmOut.data, 0, readedBytes);
                    }
                    ctx.writeAndFlush(fmOut);
                    Thread.sleep(100);
                    System.out.println("Отправлена часть #" + (i + 1));
                }
                in.close();
                /*
                FileDataCommand fData = new FileDataCommand(Paths.get("server_storage/" + fr.getFilename()));
                ctx.writeAndFlush(fData);
                 */
            }

        } else if (msg instanceof FileDataCommand) {
            FileDataCommand fData = (FileDataCommand) msg;
            boolean append = true;
            if (fData.partNumber == 1) {
                append = false;
            }
            System.out.println(fData.partNumber + " / " + fData.partsCount);
            FileOutputStream fos = new FileOutputStream("server_storage/" + fData.getLogin() + "/" + fData.getFilename(), append);
            fos.write(fData.data);
            fos.close();
            if (fData.partNumber == fData.partsCount) {
            //    cdl.countDown();
              //  refreshClientFilesList();
                System.out.println("Загрузка завершена");
            }
        } else if (msg instanceof GetUserFilesCommand) {
            GetUserFilesCommand fData = (GetUserFilesCommand) msg;
            Path path = Paths.get("server_storage/" + fData.getLogin() );
            if ( ! Files.exists(path)) {
                File folder = new File("server_storage/" + fData.getLogin() );
                folder.mkdir();
            }
            Path path_files = Paths.get("server_storage/" + fData.getLogin());
            List<String> filesList = new ArrayList<String>();
            Files.list(path_files)
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.getFileName().toString())
                    .forEach(o -> filesList.add(o) );

            SendUserFilesCommand sendUserFilesCommand = new SendUserFilesCommand( filesList );
            ctx.writeAndFlush(sendUserFilesCommand);
        } else {
            System.out.printf("Server received wrong object!");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
