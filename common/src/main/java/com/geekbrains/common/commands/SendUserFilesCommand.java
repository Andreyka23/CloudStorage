package com.geekbrains.common.commands;

import java.util.List;

public class SendUserFilesCommand extends AbstractCommand {
    private List<String> userFilesList;

    public List<String> getUserFilesList() {
        return userFilesList;
    }

    public SendUserFilesCommand(List<String> userFilesList) {
        this.userFilesList = userFilesList;
    }
}