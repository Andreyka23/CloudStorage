package com.geekbrains.common.commands;

public class FileRequestCommand extends AbstractCommand {

    private String login;
    private String filename;

    public String getLogin() {
        return login;
    }

    public String getFilename() {
        return filename;
    }

    public FileRequestCommand(String login, String filename) {
        this.login = login;
        this.filename = filename;
    }


}