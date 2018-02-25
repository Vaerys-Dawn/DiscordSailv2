package com.github.vaerys.objects;

import java.util.ArrayList;

/**
 * Created by Vaerys on 02/09/2016.
 */
public class ServerObject {
    String name;
    long creatorID;
    String serverIP;
    String serverPort;
    ArrayList<String> serverDesc = new ArrayList<>();

    public ServerObject(String name, long creatorID, String serverIP, String serverPort) {
        this.name = name;
        this.creatorID = creatorID;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatorID() {
        return creatorID;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerDesc() {
        StringBuilder builder = new StringBuilder();
        for (String s : serverDesc) {
            builder.append(s + "\n");
        }
        return builder.toString();
    }

    public void setServerDesc(ArrayList<String> serverDesc) {
        this.serverDesc = serverDesc;
    }
}
