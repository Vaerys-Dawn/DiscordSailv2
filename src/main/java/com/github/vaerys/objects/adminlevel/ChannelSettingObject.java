package com.github.vaerys.objects.adminlevel;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.main.Globals;

import java.util.ArrayList;

/**
 * Created by Vaerys on 07/04/2017.
 */
public class ChannelSettingObject {
    ChannelSetting type;
    ArrayList<Long> channelIDs = new ArrayList<>();

    public ChannelSettingObject(ChannelSetting type, long id) {
        this.type = type;
        channelIDs.add(id);
    }

    public ChannelSettingObject(ChannelSetting type) {
        this.type = type;
    }

    public ChannelSetting getType() {
        return type;
    }

    public ArrayList<Long> getChannelIDs() {
        return channelIDs;
    }

    public ArrayList<String> mentionChannelIDs() {
        ArrayList<String> mentioned = new ArrayList<>();
        for (long s : channelIDs) {
            mentioned.add(Globals.client.getChannelByID(s).mention());
        }
        return mentioned;
    }
}
