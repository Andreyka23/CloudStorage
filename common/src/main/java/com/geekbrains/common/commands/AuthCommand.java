package com.geekbrains.common.commands;

import java.io.IOException;

public class AuthCommand extends AbstractCommand {

    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public AuthCommand(String login, String password) throws IOException {
        this.login = login;
        this.password = password;
    }
}
