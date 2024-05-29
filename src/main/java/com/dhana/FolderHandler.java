package com.dhana;

import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FolderHandler {

    private static final Logger LOGGER = Logger.getLogger(FolderHandler.class);
    private final String sourceFolderPath;
    private final File tempFolder;

    public FolderHandler(String sourceFolderPath) throws IOException {
        this.sourceFolderPath = sourceFolderPath;
        this.tempFolder = Files.createTempDirectory("backup-temp").toFile();
    }

    public void copyFilesToTemp() throws IOException {
        File sourceFolder = new File(sourceFolderPath);

        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            throw new IOException("Source folder does not exist or is not a directory: " + sourceFolderPath);
        }

        File[] files = sourceFolder.listFiles();
        if (files == null || files.length == 0) {
            throw new IOException("No files found in the source folder: " + sourceFolderPath);
        }

        for (File file : files) {
            if (file.isFile()) {
                Path destinationPath = tempFolder.toPath().resolve(file.getName());
                Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Copied file to temp folder: " + file.getName());
            }
        }
    }

    public File getTempFolder() {
        return tempFolder;
    }
}
