package com.github.vaerys.handlers;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.*;
import com.github.vaerys.oldcode.ChannelData;
import com.github.vaerys.oldcode.*;
import com.github.vaerys.pogos.*;
import com.github.vaerys.pogos.CustomCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class PatchHandler {

    final static Logger logger = LoggerFactory.getLogger(PatchHandler.class);

    public static void guildPatches(IGuild guild) {
        List<String> patchesFound = Globals.getGlobalData().getPatches().stream().map(PatchObject::getPatchLevel).collect(Collectors.toList());
        if (!patchesFound.contains(Constants.PATCH_1))
            Globals.getGlobalData().getPatches().add(new PatchObject(Constants.PATCH_1));
        if (!patchesFound.contains(Constants.PATCH_2))
            Globals.getGlobalData().getPatches().add(new PatchObject(Constants.PATCH_2));
        if (!patchesFound.contains(Constants.PATCH_3))
            Globals.getGlobalData().getPatches().add(new PatchObject(Constants.PATCH_3));
        if (!patchesFound.contains(Constants.PATCH_4))
            Globals.getGlobalData().getPatches().add(new PatchObject(Constants.PATCH_4));
        if (!patchesFound.contains(Constants.PATCH_5))
            Globals.getGlobalData().getPatches().add(new PatchObject(Constants.PATCH_5));
//        if (!patchesFound.contains(Constants.PATCH_6))
//            Globals.getGlobalData().getPatches().add(new PatchObject(Constants.PATCH_6));

        ArrayList<PatchObject> patches = Globals.getGlobalData().getPatches();

        for (PatchObject p : patches) {
            boolean exit = false;
            for (String guildID : p.getPatchedGuildIDs()) {
                if (guild.getStringID().equals(guildID)) {
                    exit = true;
                }
            }
            if (!exit) {
                logger.info("Performing Patch " + p.getPatchLevel() + " For guild with ID: " + guild.getStringID() + ". This may take a while.");
            }
            //patch 1
            if (!exit && p.getPatchLevel().equals(Constants.PATCH_1)) {
                CustomCommands customCommands = (CustomCommands) Utility.initFile(guild.getStringID(), Constants.FILE_CUSTOM, CustomCommands.class);
                if (customCommands != null) {
                    if (customCommands != null) {
                        for (CCommandObject c : customCommands.getCommandList()) {
                            if (c.getContents(false).contains("#ifArgs#{;;")) {
                                logger.info("converting code in $$" + c.getName() + " to new system");
                                c.setContents(c.getContents(false).replace("#ifArgs#{;;", "#ifArgsEmpty#{"));
                            }
                        }
                    }
                }
                FileHandler.writeToJson(Utility.getFilePath(guild.getStringID(), Constants.FILE_CUSTOM), customCommands);
                p.getPatchedGuildIDs().add(guild.getStringID());
            }
            //patch 2
            if (!exit && p.getPatchLevel().equals(Constants.PATCH_2)) {
                ChannelData channelData = (ChannelData) Utility.initFile(guild.getStringID(), Constants.FILE_GUILD_CONFIG, ChannelData.class);
                GuildConfig guildconfig = (GuildConfig) Utility.initFile(guild.getStringID(), Constants.FILE_GUILD_CONFIG, GuildConfig.class);
                if (channelData != null) {
                    for (ChannelTypeObject c : channelData.getChannels()) {
                        guildconfig.addChannelSetting(c.getType(), c.getID());
                    }
                }
                FileHandler.writeToJson(Utility.getFilePath(guild.getStringID(), Constants.FILE_GUILD_CONFIG), guildconfig);
                p.getPatchedGuildIDs().add(guild.getStringID());
            }
            //patch 3 - rename all of the toggles to used <tag> instead of #tag# (lots of code that cant be simplified ;_;)
            if (!exit && p.getPatchLevel().equals(Constants.PATCH_3)) {
                CustomCommands customCommands = (CustomCommands) Utility.initFile(guild.getStringID(), Constants.FILE_CUSTOM, CustomCommands.class);
                if (customCommands != null) {
                    if (customCommands != null) {
                        for (CCommandObject c : customCommands.getCommandList()) {
                            c.setContents(toNewSystem(c.getContents(false)));
                        }
                    }
                }
                FileHandler.writeToJson(Utility.getFilePath(guild.getStringID(), Constants.FILE_CUSTOM), customCommands);


                List<String> contents = FileHandler.readFromFile(Utility.getFilePath(guild.getStringID(), Constants.FILE_INFO));
                StringBuilder content = new StringBuilder();
                for (String c : contents) {
                    c = c.replace("#displayName#", "<displayName>");
                    c = c.replace("#channel#", "<channel>");
                    c = c.replace("#spacer#", "<spacer>");
                    c = c.replace("#!break#", "<!break>");
                    c = c.replace("#image#", "<image>");
                    c = c.replace("#split#", "<split>");
                    content.append(c + "\n");
                }
                File newFile = new File(Utility.getFilePath(guild.getStringID(), Constants.FILE_INFO + "x"));
                File oldFile = new File(Utility.getFilePath(guild.getStringID(), Constants.FILE_INFO));
                FileHandler.writeToFile(newFile.getPath(), content.toString(), false);
                oldFile.delete();
                newFile.renameTo(oldFile);

                p.getPatchedGuildIDs().add(guild.getStringID());
            }
            if (!exit && p.getPatchLevel().equalsIgnoreCase(Constants.PATCH_4)) {
                GuildUsers guildUsers = (GuildUsers) Utility.initFile(guild.getStringID(), Constants.FILE_GUILD_USERS, GuildUsers.class);

                for (ProfileObject u : guildUsers.getProfiles()) {
                    u.setXp(0);
//                    u.setRewardID(-1);
                }

                FileHandler.writeToJson(Utility.getFilePath(guild.getStringID(), Constants.FILE_GUILD_USERS), guildUsers);
                p.getPatchedGuildIDs().add(guild.getStringID());
            }
            if (!exit && p.getPatchLevel().equalsIgnoreCase(Constants.PATCH_5)) {
                OldGuildConfig oldGuildConfig = (OldGuildConfig) Utility.initFile(guild.getStringID(), Constants.FILE_GUILD_CONFIG, OldGuildConfig.class);
                GuildConfig guildconfig = (GuildConfig) Utility.initFile(guild.getStringID(), Constants.FILE_GUILD_CONFIG, GuildConfig.class);
                OldCharacters oldCharacters = (OldCharacters) Utility.initFile(guild.getStringID(), Constants.FILE_CHARACTERS, OldCharacters.class);
                Characters characters = (Characters) Utility.initFile(guild.getStringID(), Constants.FILE_CHARACTERS, Characters.class);
                for (RoleTypeObject r : oldGuildConfig.getCosmeticRoles()) {
                    guildconfig.getCosmeticRoleIDs().add(Long.parseLong(r.getRoleID()));
                }
                for (RoleTypeObject r : oldGuildConfig.getModifierRoles()) {
                    guildconfig.getModifierRoleIDs().add(Long.parseLong(r.getRoleID()));
                }
                for (RoleTypeObject r : oldGuildConfig.getTrustedRoles()) {
                    guildconfig.getTrustedRoleIDs().add(Long.parseLong(r.getRoleID()));
                }
                try {

                    long newRoleToMention = Long.parseLong(oldGuildConfig.getRoleToMention().getRoleID());

                    guildconfig.setRoleToMentionID(newRoleToMention);
                } catch (NumberFormatException e) {
                    logger.info("Could not find valid role to mention role");
                }
                try {
                    long newMutedRole = Long.parseLong(oldGuildConfig.getMutedRole().getRoleID());
                    guildconfig.setMutedRoleID(newMutedRole);
                } catch (NumberFormatException e) {
                    logger.info("Could not find valid mute role");
                }
                for (OldCharacterObject oldChar : oldCharacters.getCharacters()) {
                    for (CharacterObject newChar : characters.getCharacters(null)) {
                        if (oldChar.getName().equalsIgnoreCase(newChar.getName())) {
                            ArrayList<Long> roleIDs = new ArrayList<>();
                            for (RoleTypeObject r : oldChar.getRoles()) {
                                roleIDs.add(Long.parseLong(r.getRoleID()));
                            }
                            newChar.setRoleIDs(roleIDs);
                        }
                    }
                }
                Utility.flushFile(guild.getStringID(), Constants.FILE_GUILD_CONFIG, guildconfig, true);
                Utility.flushFile(guild.getStringID(), Constants.FILE_CHARACTERS, characters, true);
                p.getPatchedGuildIDs().add(guild.getStringID());
            }
//            if (!exit && p.getPatchLevel().equalsIgnoreCase(Constants.PATCH_6)) {
//                GuildConfig guildConfig = (GuildConfig) FileHandler.readFromJson(Utility.getFilePath(guild.getStringID(), Constants.FILE_GUILD_CONFIG, true) + "1", GuildConfig.class);
//                Utility.flushFile(guild.getStringID(), Constants.FILE_GUILD_CONFIG, guildConfig, true);
//                Characters characters = (Characters) FileHandler.readFromJson(Utility.getFilePath(guild.getStringID(), Constants.FILE_CHARACTERS, true) + "1", Characters.class);
//                Utility.flushFile(guild.getStringID(), Constants.FILE_CHARACTERS, characters, true);
//                p.getPatchedGuildIDs().add(guild.getStringID());
//            }
        }
    }

    public static void globalPatches() {
        if (!Globals.getGlobalData().getGlobalPatches().contains(Constants.PATCH_GLOBAL_1)) {
            logger.info("Performing patch : " + Constants.PATCH_GLOBAL_1 + ". Please wait...");
            //fix daily messages
            Config config = (Config) FileHandler.readFromJson(Constants.FILE_CONFIG, Config.class);
            for (DailyMessageObject d : Globals.configDailyMessages) {
                StringBuilder content = new StringBuilder();
                for (String s : d.getData()) {
                    content.append(s.replace("#random#", "<random>") + "\n");
                }
                d.setContents(content.toString());
            }
            config.setDailyMessages(Globals.configDailyMessages);
            FileHandler.writeToJson(Constants.FILE_CONFIG, config);
            Globals.getGlobalData().getGlobalPatches().add(Constants.PATCH_GLOBAL_1);
        }
        if (!Globals.getGlobalData().getGlobalPatches().contains(Constants.PATCH_GLOBAL_2)) {
            logger.info("Performing patch : " + Constants.PATCH_GLOBAL_2 + ". Please wait...");
            Config config = (Config) FileHandler.readFromJson(Constants.FILE_CONFIG, Config.class);
            config.randomStatuses = new Config().randomStatuses;
            FileHandler.writeToJson(Constants.FILE_CONFIG, config);
            Globals.getGlobalData().getGlobalPatches().add(Constants.PATCH_GLOBAL_2);
            logger.info("Reloading Globals...");
            Globals.initConfig(Globals.client, config, Globals.getGlobalData());
        }
    }

    public static void globalDataPatch() {
        OldGlobalData oldData = (OldGlobalData) FileHandler.readFromJson(Constants.FILE_GLOBAL_DATA, OldGlobalData.class);
        DailyMessages dailyMessages = (DailyMessages) DailyMessages.create(DailyMessages.FILE_PATH, new DailyMessages());
        dailyMessages.getMessages().addAll(oldData.getDailyMessages());
        dailyMessages.getQueue().addAll(oldData.getQueuedRequests());
        dailyMessages.flushFile();
    }

    private static String toNewSystem(String from) {
        from = from.replace("#args#", "<args>");
        from = from.replace("#args!#", "<args!>");
        from = from.replace("#random#", "<random>");
        from = from.replace("#ifRole#", "<ifRole>");
        from = from.replace("#ifName#", "<ifName>");
        from = from.replace("#ifArgs#", "<ifArgs>");
        from = from.replace("#ifArgsEmpty#", "<ifArgsEmpty>");
        from = from.replace("#replace#", "<replace>");
        from = from.replace("#replace!#", "<replace!>");
        from = from.replace("#!r#", "</r>");
        from = from.replace("#author#", "<author>");
        from = from.replace("#username#", "<username>");
        from = from.replace("#spacer#", "<spacer>");
        from = from.replace("#!break#", "<!break>");
        from = from.replace("#toCaps#", "<toCaps>");
        from = from.replace("#embedImage#", "<embedImage>");
        from = from.replace("#randNum#", "<randNum>");
        from = from.replace("#delCall#", "<delCall>");
        return from;
    }
}

