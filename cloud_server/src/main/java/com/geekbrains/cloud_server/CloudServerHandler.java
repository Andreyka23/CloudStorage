package com.geekbrains.cloud_server;

import com.geekbrains.common.commands.FileDataCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.geekbrains.common.commands.FileRequestCommand;

import java.nio.file.Files;
import java.nio.file.Paths;

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
        if (msg instanceof FileRequestCommand) {
            System.out.println("Client text message: " + ((FileRequestCommand) msg).getFilename());
          //  ctx.writeAndFlush(new FileRequestCommand("Hello Client!"));
            FileRequestCommand fr = (FileRequestCommand) msg;
            if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
                FileDataCommand fData = new FileDataCommand(Paths.get("server_storage/" + fr.getFilename()));
                ctx.writeAndFlush(fData);
            }
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
