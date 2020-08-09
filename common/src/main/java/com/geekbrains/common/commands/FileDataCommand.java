package com.geekbrains.common.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDataCommand extends AbstractCommand {
    private String filename;
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public FileDataCommand(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}
