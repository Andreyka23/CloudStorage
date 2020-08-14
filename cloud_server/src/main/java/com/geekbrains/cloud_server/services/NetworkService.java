package com.geekbrains.cloud_server.services;

import com.geekbrains.cloud_server.auth.BaseAuthService;

public class NetworkService {


    public boolean checkLogin(String login, String password) {
        BaseAuthService baseAuthService = new BaseAuthService();
        String username = baseAuthService.getUsernameByLoginAndPassword(login, password);
        if (username == null) {
            return false;
        } else {
            return true;
        }
    }

}
