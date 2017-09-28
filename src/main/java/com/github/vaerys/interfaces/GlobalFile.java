package com.github.vaerys.interfaces;

import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class GlobalFile {
    public static final String storageDir = Constants.DIRECTORY_STORAGE;
    public static final String backupDir = Constants.DIRECTORY_BACKUPS;
    public transient String path;
    public transient String backupPath;

    final static Logger logger = LoggerFactory.getLogger(GlobalFile.class);

    public static Object create(String newPath, GlobalFile object) {
        String path = storageDir + newPath;
        if (!FileHandler.exists(path)) {
            FileHandler.writeToJson(path, object);
        } else {
            object = (GlobalFile) FileHandler.readFromJson(path, object.getClass());
        }
        object.setPath(newPath);
        return object;
    }

    public void flushFile() {
        FileHandler.writeToJson(path, this);
    }

    public void backUp() {
        try {
            File backup1 = new File(backupPath + 1);
            File backup2 = new File(backupPath + 2);
            File backup3 = new File(backupPath + 3);
            File toBackup = new File(path);
            if (backup3.exists()) backup3.delete();
            if (backup2.exists()) backup2.renameTo(backup3);
            if (backup1.exists()) backup1.renameTo(backup2);
            if (toBackup.exists())
                Files.copy(Paths.get(toBackup.getPath()), backup1.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.trace(this.getClass().getName() + " - File Backed up.");
        } catch (IOException e) {
            Utility.sendStack(e);
        }
    }

    public void setPath(String newPath) {
        path = storageDir + newPath;
        backupPath = backupDir + newPath;
    }
}
