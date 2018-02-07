package com.github.vaerys.channelsettings;

import com.github.vaerys.channelsettings.settings.*;
import com.github.vaerys.channelsettings.types.*;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.ChannelSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class InitChannels {

    final static Logger logger = LoggerFactory.getLogger(InitChannels.class);

    public static ArrayList<ChannelSetting> get() {
        ArrayList<ChannelSetting> channelSettings = new ArrayList<>();

        //Channel Types
        channelSettings.add(new General());
        channelSettings.add(new ChannelInfo());
        channelSettings.add(new Admin());
        channelSettings.add(new AdminLog());
        channelSettings.add(new ServerLog());
        channelSettings.add(new Art());
        channelSettings.add(new LevelUp());

        //Channel Settings
        channelSettings.add(new Pixels());
        channelSettings.add(new Shitpost());
        channelSettings.add(new Servers());
        channelSettings.add(new BotCommands());
        channelSettings.add(new Groups());
        channelSettings.add(new XpDenied());
        channelSettings.add(new LevelUpDenied());
        channelSettings.add(new DontLog());

        validate(channelSettings);

        return channelSettings;
    }

    private static void validate(List<ChannelSetting> settings) {
        for (ChannelSetting s : settings) {
            logger.trace("Validating Tag: " + s.getClass().getName());
            String errorReport = s.validate();
            Globals.addToErrorStack(errorReport);
        }
    }
}
