package com.github.vaerys.templates;

import com.github.vaerys.enums.FilePaths;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;

import java.io.IOException;

public class FileFactory {

    public static final String storageDir = Constants.DIRECTORY_STORAGE;

    public static <T extends GlobalFile> T create(long guildID, FilePaths newPath, Class object) {
        T t;
        String path = storageDir + guildID + "/" + newPath.toString();
        if (!FileHandler.exists(path)) {
            t = (T) getObject(object);
            FileHandler.writeToJson(path, t);
        } else {
            t = (T) FileHandler.readFromJson(path, object);
        }
        if (t == null) {
            Utility.sendStack(new IOException("File is corrupt: " + path));
        }
        t.setPath(guildID + "/" + newPath);
        return t;
    }

    public static <T extends GlobalFile> T create(FilePaths newPath, Class<?> object) {
        T t;
        String path = storageDir + newPath.toString();
        if (!FileHandler.exists(path)) {
            t = (T) getObject(object);
            FileHandler.writeToJson(path, t);
        } else {
            t = (T) FileHandler.readFromJson(path, object);
        }
        if (t == null) {
            Utility.sendStack(new IOException("File is corrupt: " + path));
        }
        t.setPath(newPath.toString());
        return t;
    }

    public static Object getObject(Class<?> object) {
        try {
            return object.getConstructor().newInstance();
        } catch (Exception e) {
            Utility.sendStack(e);
        }
        return null;
    }
}
