package Objects;


import java.util.ArrayList;

/**
 * Created by Vaerys on 30/09/2016.
 */
public class GuildCoolDownObject {
    public int doAdminMention = 0;
    public int serverCoolDown = 0;
    String guildID;
    ArrayList<WaiterObject> waiterObjects = new ArrayList<>();

    public GuildCoolDownObject(String guildID) {
        this.guildID = guildID;
    }

    public String getGuildID() {
        return guildID;
    }

    public int getServerCoolDown() {
        return serverCoolDown;
    }

    public void setServerCoolDown(int serverCoolDown) {
        this.serverCoolDown = serverCoolDown;
    }

    public int getDoAdminMention() {
        return doAdminMention;
    }

    public void setDoAdminMention(int doAdminMention) {
        this.doAdminMention = doAdminMention;
    }

    public ArrayList<WaiterObject> getWaiterObjects() {
        return waiterObjects;
    }

    public void setWaiterObjects(ArrayList<WaiterObject> waiterObjects) {
        this.waiterObjects = waiterObjects;
    }

    public void addWaiter(String userID) {
        waiterObjects.add(new WaiterObject(userID));
    }

    public void removeWaiter(String userID){
        int i= 0;
        for (WaiterObject w : waiterObjects){
            if (w.getID().equals(userID)){
                waiterObjects.remove(i);
                return;
            }
            i++;
        }
    }

    public void AddWaiterObject(WaiterObject waiterObject) {
        waiterObjects.add(waiterObject);
    }
}
