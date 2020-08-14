package com.geekbrains.common.commands;

import java.io.IOException;

public class ServerResponse extends AbstractCommand {

    private boolean result;
    private String message;

    public boolean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public ServerResponse(boolean result, String message) throws IOException {
        this.result = result;
        this.message = message;
    }
}
