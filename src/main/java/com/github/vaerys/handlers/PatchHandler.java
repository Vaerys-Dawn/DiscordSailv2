package com.github.vaerys.handlers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.FilePaths;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.objects.botlevel.PatchObject;
import com.github.vaerys.pogos.*;
import com.github.vaerys.templates.FileFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class PatchHandler {

    final static Logger logger = LoggerFactory.getLogger(PatchHandler.class);

    public static void guildPatches(IGuild guild) {

        // 1.0 overhauling strings to longs and fixing spelling/name issues.
        overhaulCharacters(guild);
        overhaulGuildConfig(guild);
        overhaulGuildUsers(guild);
        overhaulCustomCommands(guild);
        overhaulServers(guild);
        overhaulComp(guild);
        // 1.1 unicode fixes
        fixUnicode(guild);
        fixUnicodeGuildUsers(guild);
        guildConfigRemoveUnicodeEmoji(guild);
        //1.2 fixes
        removeMentionRestriction(guild);
        switchModNotesToStrikes(guild);

        //1.2 for GuildConfog 1.1 for Channel Data
        moveChannelsToChannelData(guild);
        //1.2
        changeChannelSettingsToEnum(guild);

        //1.3 fixes
        sanitizer(guild);
        updateChannelTypesForCodeConventions(guild);
        refactorTrustedToInviteAllowed(guild);

        //1.4 fixes
        removeNullPrefix(guild);
        renameToggles(guild);
    }


    public static void preInitPatches() {
        // 1.0 patches
        overhaulGlobalData();
        overhaulConfig();
        fixUnicodeDaily();
        //1.1 patches
        fixDefaultDailyMessages();

        //1.2 patches
        fixMultipleDailies();
    }

    private static PatchObject getJsonConfig(IGuild guild, String file, double patchID, String patch) {
        String path = Utility.getFilePath(guild.getLongID(), file);
        // check if file exists:
        if (!FileHandler.exists(path)) return null;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(patchID, guild, patch, json)) return null;
        return new PatchObject(json, path, patchID);
    }

    private static void finalizePatch(PatchObject patch) {
        newPatchID(patch.getPatchID(), patch.getObject());
        FileHandler.writeToJson(patch.getPath(), patch.getObject());
    }

    private static void renameToggles(IGuild guild) {
        PatchObject patch = getJsonConfig(guild, GuildConfig.FILE_PATH, 1.4, "RenameToggles");
        if (patch == null) return;
        boolean welcome = patch.getObject().get("joinsServerMessages").getAsBoolean();
        boolean initial = patch.getObject().get("welcomeMessage").getAsBoolean();

        patch.getObject().remove("joinsServerMessages");
        patch.getObject().remove("welcomeMessage");

        patch.getObject().addProperty("welcomeMessages", welcome);
        patch.getObject().addProperty("initialMessage", initial);
        finalizePatch(patch);
    }

    private static void refactorTrustedToInviteAllowed(IGuild guild) {
        PatchObject patch = getJsonConfig(guild, GuildConfig.FILE_PATH,
                1.3, "Refactor_Trusted_To_Invite_Allowed");
        if (patch == null) return;

        JsonArray array = patch.getObject().get("trustedRoleIDs").getAsJsonArray();
        patch.getObject().remove("trustedRoleIDs");
        if (array.size() != 0) {
            patch.getObject().addProperty("inviteAllowedID", array.get(0).getAsLong());
        }

        finalizePatch(patch);
    }

    private static void changeChannelSettingsToEnum(IGuild guild) {
        PatchObject json = getJsonConfig(guild, ChannelData.FILE_PATH,
                1.2, "Change_ChannelSettings_To_Enum");
        if (json == null) return;

        JsonArray channelSettings = json.getObject().getAsJsonArray("channelSettings");
        for (int i = 0; i < channelSettings.size(); i++) {
            JsonObject object = channelSettings.get(i).getAsJsonObject();
            String type = object.get("type").getAsString();
            object.remove("type");
            ChannelSetting setting = ChannelSetting.get(type);
            if (setting == null) {
                channelSettings.remove(i);
                i--;
            } else {
                object.addProperty("type", setting.name());
            }
        }
        finalizePatch(json);
    }

    private static void updateChannelTypesForCodeConventions(IGuild guild) {
        PatchObject json = getJsonConfig(guild, ChannelData.FILE_PATH,
                1.3, "");
        if (json == null) return;

        JsonArray channelSettings = json.getObject().getAsJsonArray("channelSettings");
        for (int i = 0; i < channelSettings.size(); i++) {
            JsonObject object = channelSettings.get(i).getAsJsonObject();
            String type = object.get("type").getAsString();
            switch (type) {
                case "CHANNEL_CC":
                    object.addProperty("type", ChannelSetting.CC_INFO.name());
                    break;
                case "CHANNEL_CHARACTERS":
                    object.addProperty("type", ChannelSetting.CHARACTER.name());
                    break;
                case "CREATE_CC":
                    object.addProperty("type", ChannelSetting.MANAGE_CC.name());
                    break;
                case "CHANNEL_INFO":
                    object.addProperty("type", ChannelSetting.INFO.name());
                    break;
            }
        }
        finalizePatch(json);
    }

    private static void moveChannelsToChannelData(IGuild guild) {
        PatchObject jsonConfig = getJsonConfig(guild, GuildConfig.FILE_PATH,
                1.2, "Move_Channels_To_Channel_Data_2");
        PatchObject jsonChannel = getJsonConfig(guild, ChannelData.FILE_PATH,
                1.1, "Move_Channels_To_Channel_Data_1");
        if (jsonConfig == null || jsonChannel == null) return;

        JsonArray array = jsonConfig.getObject().getAsJsonArray("channelSettings");

        jsonChannel.getObject().add("channelSettings", array);

        finalizePatch(jsonConfig);
        finalizePatch(jsonChannel);
    }


    private static void switchModNotesToStrikes(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH);
        // check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.2, guild, "Update_Notes_To_Strikes", json)) return;

        // patch
        GuildUsers guildUsers = FileFactory.create(guild.getLongID(), FilePaths.GUILD_USERS, GuildUsers.class);
        guildUsers.profiles.forEach(object -> {
            if (object.modNotes == null) return;

            object.modNotes.forEach(note -> {
                if (note.getNote().contains("⚠")) {
                    note.setStrike(true);
                    note.setNote(note.getNote().replaceAll("(^⚠ | ⚠|⚠)", ""));
                }

            });
        });
        guildUsers.flushFile();

        json = FileHandler.fileToJsonObject(path);
        newPatchID(1.2, json);
        FileHandler.writeToJson(path, json);
    }

    private static void removeNullPrefix(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.5, guild, "Remove_Null_Prefixes_CustomCommands", json)) return;

        CustomCommands commands = FileFactory.create(guild.getLongID(), FilePaths.CUSTOM_COMMANDS, CustomCommands.class);
        commands.getCommandList().forEach(object -> {
            String contents = object.getContents(false);
            if (contents.startsWith("null")) {
                object.setContents(contents.replaceFirst("null", ""));
            }
        });
        commands.flushFile();

        json = FileHandler.fileToJsonObject(path);
        newPatchID(1.5, json);
        FileHandler.writeToJson(path, json);
    }

    private static void fixMultipleDailies() {
        String path = Constants.DIRECTORY_STORAGE + DailyMessages.FILE_PATH;
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.2, null, "Fix_Multiple_Daily_Messages", json)) return;
        DailyMessages messages = FileFactory.create(FilePaths.DAILY_MESSAGES, DailyMessages.class);
        ArrayList<DailyMessage> dailyMessages = new ArrayList<>();

        for (DailyMessage message : messages.getMessages()) {
            boolean foundMessage = false;
            for (DailyMessage m : dailyMessages) {
                if (m.getUID() == message.getUID()) {
                    foundMessage = true;
                }
            }
            if (!foundMessage) {
                dailyMessages.add(message);
            }
        }
        messages.setMessages(dailyMessages);
        messages.flushFile();
        json = FileHandler.fileToJsonObject(path);
        newPatchID(1.2, json);
        FileHandler.writeToJson(path, json);
    }

    private static void fixDefaultDailyMessages() {
        String path = Constants.DIRECTORY_STORAGE + Constants.FILE_CONFIG;
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.1, null, "Fix_Default_Daily_Messages_Config", json)) return;
        JsonArray oldMessages = json.getAsJsonArray("dailyMessages");
        JsonArray array = new JsonArray();
        long creatorID = json.get("creatorID").getAsLong();
        oldMessages.forEach(jsonElement -> {
            if (jsonElement != null) {
                JsonArray contents = jsonElement.getAsJsonObject().get("contents").getAsJsonArray();
                String dayOfWeek = jsonElement.getAsJsonObject().get("dayOfWeek").getAsString();
                StringBuilder newContent = new StringBuilder();
                for (JsonElement j : contents) {
                    newContent.append(j.getAsString() + "\n");
                }
                if (newContent.toString().endsWith("\n")) ;
                newContent.replace(newContent.length() - 1, newContent.length(), "");
                JsonObject newObject = new JsonObject();
                newObject.addProperty("content", newContent.toString());
                newObject.addProperty("day", dayOfWeek);
                newObject.addProperty("userID", creatorID);
                newObject.addProperty("specialID", Constants.DAILY_SPECIALID);
                newObject.addProperty("uID", -1);
                array.add(newObject);
            }
        });
        json.remove("dailyMessages");
        json.add("dailyMessages", array);
        newPatchID(1.1, json);
        FileHandler.writeToJson(path, json);
    }

    private static void sanitizer(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.3, guild, "Append_DeSanitizer_CustomCommands", json)) return;
        JsonArray object = json.getAsJsonArray("commands");
        for (int i = 0; i < object.size(); i++) {
            JsonObject blacklisted = object.get(i).getAsJsonObject();
            String name = blacklisted.get("name").getAsString();
            if (name.equalsIgnoreCase("echo")) {
                JsonArray newArray = new JsonArray();
                newArray.add("<args><dontSanitize>");
                blacklisted.remove("contents");
                blacklisted.add("contents", newArray);
            }
        }
        newPatchID(1.3, json);
        FileHandler.writeToJson(path, json);
    }

    private static void removeMentionRestriction(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.2, guild, "Remove_Mention_Restrictions_CustomCommands", json)) return;
        JsonArray object = json.getAsJsonArray("blackList");
        for (int i = 0; i < object.size(); i++) {
            JsonObject blacklisted = object.get(i).getAsJsonObject();
            String phrase = blacklisted.get("phrase").getAsString();
            if (phrase.equalsIgnoreCase("<@")) {
                object.remove(i);
            }
        }
        newPatchID(1.2, json);
        FileHandler.writeToJson(path, json);
    }

    private static void guildConfigRemoveUnicodeEmoji(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), GuildConfig.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.1, guild, "FixUnicode_Guild_Config", json)) return;
        JsonElement object = json.get("levelUpReaction");
        if (object == null) return;
        String s = object.getAsString();
        if (s == null) return;
        try {
            Long.parseUnsignedLong(s);
        } catch (NumberFormatException e) {
            json.remove("levelUpReaction");
            json.addProperty("levelUpReaction", "null");
        } catch (ClassCastException e) {
            // do nothing
        }
        newPatchID(1.1, json);
        FileHandler.writeToJson(path, json);
    }

    private static void fixUnicodeGuildUsers(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH);
        String backupPath = Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH + "1", true);
        //check files
        if (!FileHandler.exists(path) || !FileHandler.exists(backupPath)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        JsonObject backupjson = FileHandler.fileToJsonObject(backupPath);
        if (checkPatch(1.1, guild, "FixUnicode_Profiles", json)) return;
        JsonArray profiles = json.getAsJsonArray("profiles");
        JsonArray oldprofiles = backupjson.getAsJsonArray("profiles");
        if (profiles == null || oldprofiles == null) return;
        profiles.forEach(profile -> {
            oldprofiles.forEach(oldprofile -> {
                if (Utility.stringLong(oldprofile.getAsJsonObject().get("userID").getAsString()) == profile.getAsJsonObject().get("userID").getAsLong()) {
                    profile.getAsJsonObject().remove("gender");
                    profile.getAsJsonObject().add("gender", oldprofile.getAsJsonObject().get("gender"));
                    profile.getAsJsonObject().remove("quote");
                    profile.getAsJsonObject().add("quote", oldprofile.getAsJsonObject().get("quote"));
                }
            });
        });
        newPatchID(1.1, json);
        FileHandler.writeToJson(path, json);
    }

    private static void fixUnicodeDaily() {
        String path = Constants.DIRECTORY_STORAGE + DailyMessages.FILE_PATH;
        String backupPath = Constants.DIRECTORY_BACKUPS + DailyMessages.FILE_PATH + 1;
        //check files
        if (!FileHandler.exists(path) || !FileHandler.exists(backupPath)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        JsonObject backupjson = FileHandler.fileToJsonObject(backupPath);
        if (checkPatch(1.1, null, "FixUnicode_dailyMessages", json)) return;
        JsonArray commands = json.getAsJsonArray("dailyMessages");
        JsonArray oldCommands = backupjson.getAsJsonArray("dailyMessages");
        if (commands == null || oldCommands == null) return;
        commands.forEach(command -> {
            oldCommands.forEach(oldCommand -> {
                if (oldCommand.getAsJsonObject().get("uID").getAsString().equalsIgnoreCase(command.getAsJsonObject().get("uID").getAsString())) {
                    command.getAsJsonObject().remove("content");
                    command.getAsJsonObject().add("content", oldCommand.getAsJsonObject().get("content"));
                }
            });
        });
        newPatchID(1.1, json);
        FileHandler.writeToJson(path, json);
    }

    private static void newPatchID(double s, JsonObject json) {
        json.remove("fileVersion");
        json.addProperty("fileVersion", s);
    }

    private static void fixUnicode(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH);
        String backupPath = Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH + "1", true);
        //check files
        if (!FileHandler.exists(path) || !FileHandler.exists(backupPath)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        JsonObject backupjson = FileHandler.fileToJsonObject(backupPath);
        if (checkPatch(1.1, guild, "FixUnicode", json)) return;
        JsonArray commands = json.getAsJsonArray("commands");
        JsonArray oldCommands = backupjson.getAsJsonArray("commands");
        if (commands == null || oldCommands == null) return;
        commands.forEach(command -> {
            oldCommands.forEach(oldCommand -> {
                if (oldCommand.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(command.getAsJsonObject().get("name").getAsString())) {
                    command.getAsJsonObject().remove("contents");
                    command.getAsJsonObject().add("contents", oldCommand.getAsJsonObject().get("contents"));
                }
            });
        });
        newPatchID(1.1, json);
        FileHandler.writeToJson(path, json);
    }


    private static boolean checkPatch(double version, IGuild guild, String patch, JsonObject json) {
        if (json.get("fileVersion") != null) {
            if (json.get("fileVersion").getAsDouble() >= version) {
                if (guild != null) {
                    logger.trace(guild.getLongID() + ": Skipping Patch - " + patch + ".");
                } else {
                    logger.trace("Skipping Patch - " + patch + ".");
                }
                return true;
            }
        }
        if (guild != null) {
            logger.info(guild.getLongID() + ": Performing Patch " + patch + ".");
        } else {
            logger.info("Performing Patch " + patch + ".");
        }
        return false;
    }

    private static void overhaulComp(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), Competition.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) ;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, guild, "Overhaul_Comp", json)) return;
        JsonArray array = json.getAsJsonArray("entries");
        if (array == null) return;
        array.forEach(entry -> {
            try {
                String oldItem = entry.getAsJsonObject().get("userID").getAsString();
                long newItem = Long.parseUnsignedLong(oldItem);
                entry.getAsJsonObject().remove("userID");
                entry.getAsJsonObject().addProperty("userID", newItem);
            } catch (NumberFormatException e) {
                entry.getAsJsonObject().remove("userID");
                entry.getAsJsonObject().addProperty("userID", -1);
            } catch (ClassCastException e) {
                // do nothing
            }
        });
        FileHandler.writeToJson(path, json);
    }

    private static void overhaulServers(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), Servers.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, guild, "Overhaul_Servers", json)) return;
        JsonArray array = json.getAsJsonArray("servers");
        if (array == null) return;
        array.forEach(server -> {
            try {
                String oldItem = server.getAsJsonObject().get("creatorID").getAsString();
                long newItem = Long.parseUnsignedLong(oldItem);
                server.getAsJsonObject().remove("creatorID");
                server.getAsJsonObject().addProperty("creatorID", newItem);
            } catch (NumberFormatException e) {
                server.getAsJsonObject().remove("creatorID");
                server.getAsJsonObject().addProperty("creatorID", -1);
            } catch (ClassCastException e) {
                // do nothing
            }
        });
        FileHandler.writeToJson(path, json);
    }

    private static void overhaulCustomCommands(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, guild, "Overhaul_CustomCommands", json)) return;
        JsonArray array = json.getAsJsonArray("commands");
        if (array == null) return;
        array.forEach(command -> {
            try {
                String oldItem = command.getAsJsonObject().get("userID").getAsString();
                long newItem = Long.parseUnsignedLong(oldItem);
                command.getAsJsonObject().remove("userID");
                command.getAsJsonObject().addProperty("userID", newItem);
            } catch (NumberFormatException e) {
                command.getAsJsonObject().remove("userID");
                command.getAsJsonObject().addProperty("userID", -1);
            } catch (ClassCastException e) {
                // do nothing
            }
        });
        FileHandler.writeToJson(path, json);
    }

    private static void overhaulGuildUsers(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, guild, "Overhaul_GuildUsers", json)) return;
        JsonArray users = json.getAsJsonArray("users");
        if (users == null) return;
        users.forEach(profile -> {
            try {
                String oldItem = profile.getAsJsonObject().get("ID").getAsString();
                long newItem = Long.parseUnsignedLong(oldItem);
                profile.getAsJsonObject().remove("ID");
                profile.getAsJsonObject().addProperty("userID", newItem);
            } catch (NumberFormatException e) {
                profile.getAsJsonObject().remove("ID");
                profile.getAsJsonObject().addProperty("userID", -1);
            } catch (ClassCastException e) {
                // do nothing
            }
        });
        json.remove("users");
        json.add("profiles", users);
        JsonArray muted = json.getAsJsonArray("mutedUsers");
        if (muted == null) return;
        muted.forEach(user -> {
            String oldItem = user.getAsJsonObject().get("userID").getAsString();
            long newItem = Long.parseUnsignedLong(oldItem);
            user.getAsJsonObject().remove("userID");
            user.getAsJsonObject().addProperty("userID", newItem);
        });
        FileHandler.writeToJson(path, json);
    }

    private static void overhaulCharacters(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), Characters.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, guild, "Overhaul_Characters", json)) return;
        JsonElement e = json.get("characters");
        JsonArray array = e.getAsJsonArray();
        if (array == null) return;
        array.forEach(jsonElement -> {
            try {
                String oldType = jsonElement.getAsJsonObject().get("userID").getAsString();
                long newType = Long.parseUnsignedLong(oldType);
                jsonElement.getAsJsonObject().remove("userID");
                jsonElement.getAsJsonObject().addProperty("userID", newType);
            } catch (ClassCastException ex) {
                //Do nothing
            } catch (NumberFormatException e1) {
                logger.error("NumberFormatException - This shouldn't happen.");
                jsonElement.getAsJsonObject().remove("userID");
                jsonElement.getAsJsonObject().addProperty("userID", -1);
            }
        });
        FileHandler.writeToJson(path, json);
    }

    private static void overhaulGuildConfig(IGuild guild) {
        String path = Utility.getFilePath(guild.getLongID(), GuildConfig.FILE_PATH);
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, guild, "Overhaul_GuildConfig", json)) return;
        JsonArray array = json.getAsJsonArray("channelSettings");
        if (array == null) return;
        array.forEach(jsonElement -> {
            JsonElement channelIDs = jsonElement.getAsJsonObject().get("channelIDs");
            JsonArray newIDs = new JsonArray();
            if (array == null) return;
            channelIDs.getAsJsonArray().forEach(id -> {
                try {
                    String oldType = id.getAsString();
                    long newType = Long.parseUnsignedLong(oldType);
                    newIDs.add(newType);
                } catch (NumberFormatException e) {
                    //do nothing
                } catch (ClassCastException e) {
                    // do nothing
                }
            });
            jsonElement.getAsJsonObject().remove("channelIDs");
            jsonElement.getAsJsonObject().add("channelIDs", newIDs);
        });
        JsonArray array1 = json.getAsJsonArray("XPDeniedPrefixes");
        json.remove("XPDeniedPrefixes");
        json.add("xpDeniedPrefixes", array1);
        FileHandler.writeToJson(path, json);
    }


    private static void overhaulConfig() {
        String path = Constants.DIRECTORY_STORAGE + Constants.FILE_CONFIG;
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, null, "Overhaul_Config", json)) return;
        try {
            String oldItem = json.get("creatorID").getAsString();
            long newItem = Long.parseUnsignedLong(oldItem);
            json.remove("creatorID");
            json.addProperty("creatorID", newItem);
        } catch (ClassCastException e) {
            // do nothing
        } catch (NumberFormatException e) {
            json.remove("creatorID");
            json.addProperty("creatorID", -1);
        }
        FileHandler.writeToJson(Constants.FILE_CONFIG, json);

    }

    private static void overhaulGlobalData() {
        String path = Constants.DIRECTORY_STORAGE + Constants.FILE_GLOBAL_DATA;
        //check file
        if (!FileHandler.exists(path)) return;
        JsonObject json = FileHandler.fileToJsonObject(path);
        if (checkPatch(1.0, null, "Overhaul_GlobalData", json)) return;
        //blocked from Dms
        JsonArray blockedUsers = json.getAsJsonArray("blockedFromDMS");
        JsonArray newUsers = new JsonArray();
        blockedUsers.forEach(user -> {
            try {
                String oldItem = user.getAsString();
                long newItem = Long.parseUnsignedLong(oldItem);
                newUsers.add(newItem);
            } catch (NumberFormatException e) {
                //do nothing
            } catch (ClassCastException ex) {
                //do nothing
            }
        });
        json.remove("blockedFromDMS");
        json.add("blockedFromDMS", newUsers);
        //Reminders
        JsonArray reminders = json.getAsJsonArray("reminders");
        if (reminders == null) return;
        reminders.forEach(reminder -> {
            try {
                try {
                    String oldUserID = reminder.getAsJsonObject().get("userID").getAsString();
                    long newUserID = Long.parseUnsignedLong(oldUserID);
                    reminder.getAsJsonObject().remove("userID");
                    reminder.getAsJsonObject().addProperty("userID", newUserID);
                } catch (NumberFormatException e) {
                    reminder.getAsJsonObject().remove("userID");
                    reminder.getAsJsonObject().addProperty("userID", -1);
                }
                try {
                    String oldChannelID = reminder.getAsJsonObject().get("channelID").getAsString();
                    long newChannelID = Long.parseUnsignedLong(oldChannelID);
                    reminder.getAsJsonObject().remove("channelID");
                    reminder.getAsJsonObject().addProperty("channelID", newChannelID);
                } catch (NumberFormatException e) {
                    reminder.getAsJsonObject().remove("channelID");
                    reminder.getAsJsonObject().addProperty("channelID", -1);
                }
            } catch (ClassCastException ex) {
                // also do nothin
            }
        });
        FileHandler.writeToJson(path, json);
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

