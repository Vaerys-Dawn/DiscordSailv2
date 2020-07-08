package com.github.vaerys.handlers;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.Guild;

import java.io.File;

public class GuildCreateListener {


    final static Logger logger = LoggerFactory.getLogger(GuildCreateListener.class);

    @EventSubscriber
    public void onGuildCreateEvent(GuildCreateEvent event) {
        Guild guild = event.getGuild();
        long guildID = guild.getIdLong();
        logger.debug("Starting Loading process for Guild with ID: " + guildID);

        if (new File(Utility.getDirectory(guildID)).exists()) {
            PatchHandler.guildPatches(guild);
        }

        FileHandler.createDirectory(Utility.getDirectory(guildID));
        FileHandler.createDirectory(Utility.getDirectory(guildID, true));
        FileHandler.createDirectory(Utility.getGuildImageDir(guildID));
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_INFO));

        GuildObject guildObject = new GuildObject(guild);
        Globals.initGuild(guildObject);
        logger.info("Finished Loading Guild With ID: " + guildID);
    }
}

