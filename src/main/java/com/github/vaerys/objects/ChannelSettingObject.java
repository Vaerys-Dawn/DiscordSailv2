package com.github.vaerys.objects;

import com.github.vaerys.main.Globals;

import java.util.ArrayList;

/**
 * Created by Vaerys on 07/04/2017.
 */
public class ChannelSettingObject {
    String type;
    ArrayList<String> channelIDs = new ArrayList<>();

    public ChannelSettingObject(String type, String id) {
        this.type = type;
        channelIDs.add(id);
    }

    public ChannelSettingObject(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getChannelIDs() {
        return channelIDs;
    }

    public ArrayList<String> mentionChannelIDs(){
        ArrayList<String> mentioned = new ArrayList<>();
        for (String s: channelIDs){
            mentioned.add(Globals.client.getChannelByID(s).mention());
        }
        return mentioned;
    }
}
