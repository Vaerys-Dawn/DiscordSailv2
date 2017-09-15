package com.github.vaerys.pogos;

import com.github.vaerys.interfaces.GuildFile;
import com.github.vaerys.objects.GroupUpObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class ChannelData extends GuildFile {
    public static final String FILE_PATH = "Channel_Data.json";
    private double fileVersion = 1.0;
    ArrayList<Long> pinnedMessages = new ArrayList<>();
    ArrayList<GroupUpObject> groupUpObjects = new ArrayList<>();

    public ArrayList<GroupUpObject> getGroupUpObjects() {
        return groupUpObjects;
    }

    public ArrayList<Long> getPinnedMessages() {
        return pinnedMessages;
    }


}
