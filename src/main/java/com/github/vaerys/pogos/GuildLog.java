package com.github.vaerys.pogos;

import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.GuildLogObject;
import com.github.vaerys.objects.LogObject;
import com.github.vaerys.templates.GuildFile;

import java.util.LinkedList;
import java.util.List;

public class GuildLog extends GuildFile {

    public static final String FILE_PATH = "Guild_Log.json";
    List<GuildLogObject> logs = new LinkedList<>();


    public List<GuildLogObject> getLogs() {
        return logs;
    }

    public void addLog(GuildLogObject logObject, long guildID) {
        logs.add(logObject);
        Globals.addToLog(new LogObject(logObject, guildID));
        if (logs.size() > 1000) {
            logs.remove(0);
        }
    }

    public void resetLogs() {
        logs.clear();
    }
}
