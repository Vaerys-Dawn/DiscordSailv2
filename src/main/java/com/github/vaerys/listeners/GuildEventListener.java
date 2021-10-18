package com.github.vaerys.listeners;

import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.PatchHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GuildEventListener extends ListenerAdapter {


    final static Logger logger = LoggerFactory.getLogger(GuildEventListener.class);

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
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

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        Globals.unloadGuild(event.getGuild().getIdLong());
    }
}

