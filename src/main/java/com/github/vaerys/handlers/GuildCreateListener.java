package com.github.vaerys.handlers;

import com.github.vaerys.commands.general.NewDailyMessage;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;

public class GuildCreateListener {


    final static Logger logger = LoggerFactory.getLogger(GuildCreateListener.class);

    @EventSubscriber
    public void onGuildCreateEvent(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        String guildID = guild.getStringID();
        logger.debug("Starting Initialisation process for Guild with ID: " + guildID);

        if (new File(Utility.getDirectory(guildID)).exists()) {
            PatchHandler.guildPatches(guild);
        }

        FileHandler.createDirectory(Utility.getDirectory(guildID));
        FileHandler.createDirectory(Utility.getDirectory(guildID, true));
        FileHandler.createDirectory(Utility.getGuildImageDir(guildID));
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_INFO));

        GuildObject guildObject = new GuildObject(guild);
        Globals.initGuild(guildObject);
        logger.info("Finished Initialising Guild With ID: " + guildID);

//
//        //Create POGO templates
//        GuildConfig guildConfig = new GuildConfig();
//        Servers servers = new Servers();
//        CustomCommands customCommands = new CustomCommands();
//        Characters characters = new Characters();
//        Competition competition = new Competition();
//        GuildUsers guildUsers = new GuildUsers();
//        ChannelData channelData = new ChannelData();
//
//        //Preps Objects for initial load
//        customCommands.initCustomCommands();
//
//        //null prevention code. unsure if needed still.
//        guildConfig.setProperlyInit(true);
//        servers.setProperlyInit(true);
//        customCommands.setProperlyInit(true);
//        characters.setProperlyInit(true);
//        competition.setProperlyInit(true);
//        guildUsers.setProperlyInit(true);
//

//
//        //initial load of all files, creates files if they don't already exist
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_SERVERS), servers);
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), customCommands);
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), characters);
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_INFO));
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_COMPETITION), competition);
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_GUILD_USERS), guildUsers);
//        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CHANNEl_DATA), channelData);
//
//        //loads all Files for the guild;
//        guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
//        customCommands = (CustomCommands) Utility.initFile(guildID, Constants.FILE_CUSTOM, CustomCommands.class);
//        servers = (Servers) Utility.initFile(guildID, Constants.FILE_SERVERS, Servers.class);
//        characters = (Characters) Utility.initFile(guildID, Constants.FILE_CHARACTERS, Characters.class);
//        competition = (Competition) Utility.initFile(guildID, Constants.FILE_COMPETITION, Competition.class);
//        guildUsers = (GuildUsers) Utility.initFile(guildID, Constants.FILE_GUILD_USERS, GuildUsers.class);
//        channelData = (ChannelData) Utility.initFile(guildID, Constants.FILE_CHANNEl_DATA, ChannelData.class);
//        //sends com.github.vaerys.objects to globals
//        Globals.initGuild(guildID, guildConfig, servers, customCommands, characters, competition, guildUsers, channelData);
//        logger.info("Finished Initialising Guild With ID: " + guildID);

        NewDailyMessage.checkIsEnabled(Globals.client.getChannelByID(Globals.queueChannelID) != null);
    }
}

