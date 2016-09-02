package Objects;

import java.util.ArrayList;

/**
 * Created by Vaerys on 02/09/2016.
 */
public class ServerObject {
    String name;
    String creatorID;
    String serverIP;
    String serverPort;
    ArrayList<String> serverDesc = new ArrayList<>();

    public ServerObject(String name, String uniqueID, String creatorID, String serverIP, String serverPort) {
        this.name = name;
        this.creatorID = creatorID;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public String getName() {
        return name;
    }
    public String getCreatorID() {
        return creatorID;
    }

    public String getServerIP() {
        return serverIP;
    }

    public String getServerPort() {
        return serverPort;
    }

    public ArrayList<String> getServerDesc() {
        return serverDesc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setServerDesc(ArrayList<String> serverDesc) {
        this.serverDesc = serverDesc;
    }
}
