package com.geekbrains.common.commands;

public class FileRequestCommand extends AbstractCommand {

    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequestCommand(String filename) {
        this.filename = filename;
    }


}