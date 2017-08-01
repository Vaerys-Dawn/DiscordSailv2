package ChannelSettings;

import ChannelSettings.Settings.*;
import ChannelSettings.Types.*;
import Interfaces.ChannelSetting;

import java.util.ArrayList;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class InitChannels {

    public static ArrayList<ChannelSetting> get() {
        ArrayList<ChannelSetting> channelSettings = new ArrayList<>();

        //Channel Types
        channelSettings.add(new General());
        channelSettings.add(new Info());
        channelSettings.add(new Admin());
        channelSettings.add(new AdminLog());
        channelSettings.add(new ServerLog());
        channelSettings.add(new Art());
        channelSettings.add(new LevelUp());

        //Channel Settings
        channelSettings.add(new Rank());
        channelSettings.add(new Shitpost());
        channelSettings.add(new Servers());
        channelSettings.add(new BotCommands());
        channelSettings.add(new Groups());
        channelSettings.add(new XpDenied());
        channelSettings.add(new LevelUpDenied());
        channelSettings.add(new DontLog());

        return channelSettings;
    }
}
