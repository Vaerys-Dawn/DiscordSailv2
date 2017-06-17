package POGOs;

import Objects.GroupUpObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class ChannelData {
    ArrayList<Long> pinnedMessages = new ArrayList<>();
    ArrayList<GroupUpObject> groupUpObjects = new ArrayList<>();

    public ArrayList<GroupUpObject> getGroupUpObjects() {
        return groupUpObjects;
    }

    public ArrayList<Long> getPinnedMessages() {
        return pinnedMessages;
    }


}
