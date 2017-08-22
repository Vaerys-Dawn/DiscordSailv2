package com.github.vaerys.interfaces;

import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.pogos.GuildConfig;

import java.util.ArrayList;

/**
 * Created by Vaerys on 09/04/2017.
 */
public interface ChannelSetting {

    String type();

    boolean isSetting();

    default ArrayList<String> getIDs(GuildConfig config) {
        for (ChannelSettingObject o : config.getChannelSettings()) {
            if (o.getType().equals(type())) {
                if (o.getChannelIDs().size() != 0) {
                    return o.getChannelIDs();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    default String toggleSetting(GuildConfig config, String channelID) {
        ArrayList<ChannelSettingObject> objects = config.getChannelSettings();
        boolean isFound = false;
        for (ChannelSettingObject s : objects) {
            if (s.getType().equals(type())) {
                isFound = true;
            }
        }
        if (!isFound) {
            objects.add(new ChannelSettingObject(type()));
        }
        for (ChannelSettingObject object : objects) {
            if (object.getType().equals(type())) {
                if (!isSetting()) {
                    if (object.getChannelIDs().size() == 0 || !object.getChannelIDs().get(0).equals(channelID)) {
                        if (object.getChannelIDs().size() == 0){
                            object.getChannelIDs().add(channelID);
                        }else {
                            object.getChannelIDs().set(0, channelID);
                        }
                        return "> " + Globals.client.getChannelByID(channelID).mention() + " is now the Server's **" + type() + "** channel.";
                    } else {
                        object.getChannelIDs().remove(0);
                        return "> " + Globals.client.getChannelByID(channelID).mention() + " is no longer the Server's **" + type() + "** channel.";
                    }
                } else {
                    if (object.getChannelIDs().size() == 0 || !object.getChannelIDs().contains(channelID)) {
                        object.getChannelIDs().add(channelID);
                        return "> " + Globals.client.getChannelByID(channelID).mention() + ". Channel setting: **" + type() + "** added.";
                    } else {
                        for (int i = 0; i < object.getChannelIDs().size(); i++) {
                            if (channelID.equals(object.getChannelIDs().get(i))) {
                                object.getChannelIDs().remove(i);
                                return "> " + Globals.client.getChannelByID(channelID).mention() + ". Channel setting: **" + type() + "** removed.";
                            }
                        }
                    }
                }
            }
        }
        return "> Error toggling channel setting.";
    }
}
