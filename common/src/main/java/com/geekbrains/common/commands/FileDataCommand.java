package com.geekbrains.common.commands;

import java.io.IOException;
import java.io.Serializable;

public class FileDataCommand extends AbstractCommand implements Serializable {

    String filename;
    public int partNumber;
    public int partsCount;
    public byte[] data;

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public FileDataCommand(String filename, int partNumber, int partsCount, byte[] data) throws IOException {
        this.filename = filename;
        this.partNumber = partNumber;
        this.partsCount = partsCount;
        this.data = data;
        // filename = path.getFileName().toString();
        // data = Files.readAllBytes(path);
    }
}
