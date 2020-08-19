package com.geekbrains.common.commands;

public class GetUserFilesCommand extends AbstractCommand {
    private String login;

    public String getLogin() {
        return login;
    }

    public GetUserFilesCommand(String login) {
        this.login = login;
    }
}