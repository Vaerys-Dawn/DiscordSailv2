package com.github.vaerys.interfaces;

import com.github.vaerys.handlers.FileHandler;

import java.io.IOException;

public class GuildFile extends GlobalFile {

    public static Object create(String newPath, long guildID, GuildFile object) {
        String path = storageDir + guildID + "/" + newPath;
        if (!FileHandler.exists(path)) {
            FileHandler.writeToJson(path, object);
        } else {
            object = (GuildFile) FileHandler.readFromJson(path, object.getClass());
        }
        if (object == null) {
            try {
                throw new IOException("File is corrupt: " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        object.setPath(guildID + "/" + newPath);
        return object;
    }
}
