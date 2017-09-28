package com.github.vaerys.handlers;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class PatchHandler {

    final static Logger logger = LoggerFactory.getLogger(PatchHandler.class);

    public static void guildPatches(IGuild guild) {

        // overhauling strings to longs and fixing spelling/name issues.
        overhaulCharacters(guild);
        overhaulGuildConfig(guild);
        overhaulGuildUsers(guild);
        overhaulCustomCommands(guild);
        overhaulServers(guild);
        overhaulComp(guild);
        //unicode fixes
        fixUnicode(guild);
        fixUnicodeGuildUsers(guild);
        guildConfigRemoveUnicodeEmoji(guild);
    }

    private static void guildConfigRemoveUnicodeEmoji(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), GuildConfig.FILE_PATH));
        if (checkPatch(1.1, guild, "FixUnicode_Guild_Config", json)) return;
        String s = json.get("levelUpReaction").getAsString();
        try {
            Long l = Long.parseUnsignedLong(s);
        } catch (NumberFormatException e) {
            json.remove("levelUpReaction");
            json.addProperty("levelUpReaction", "null");
        }
        newPatchID(1.1, json);
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), GuildConfig.FILE_PATH), json);
    }

    private static void fixUnicodeGuildUsers(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH));
        JsonObject backupjson = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH + "1", true));
        if (checkPatch(1.1, guild, "FixUnicode_Profiles", json)) return;
        JsonArray profiles = json.getAsJsonArray("profiles");
        JsonArray oldprofiles = backupjson.getAsJsonArray("profiles");
        if (profiles == null || oldprofiles == null) {
            return;
        }
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
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH), json);
    }

    private static void fixUnicodeDaily() {
        JsonObject json = FileHandler.fileToJsonObject(Constants.DIRECTORY_STORAGE + DailyMessages.FILE_PATH);
        JsonObject backupjson = FileHandler.fileToJsonObject(Constants.DIRECTORY_BACKUPS + DailyMessages.FILE_PATH + 1);
        if (checkPatch(1.1, null, "FixUnicode_dailyMessages", json)) return;
        JsonArray commands = json.getAsJsonArray("dailyMessages");
        JsonArray oldCommands = backupjson.getAsJsonArray("dailyMessages");
        if (commands != null) {
            commands.forEach(command -> {
                oldCommands.forEach(oldCommand -> {
                    if (oldCommand.getAsJsonObject().get("uID").getAsString().equalsIgnoreCase(command.getAsJsonObject().get("uID").getAsString())) {
                        command.getAsJsonObject().remove("content");
                        command.getAsJsonObject().add("content", oldCommand.getAsJsonObject().get("content"));
                    }
                });
            });
        }
        newPatchID(1.1, json);
        FileHandler.writeToJson(Constants.DIRECTORY_STORAGE + DailyMessages.FILE_PATH, json);
    }

    private static void newPatchID(double s, JsonObject json) {
        json.remove("fileVersion");
        json.addProperty("fileVersion", s);
    }

    private static void fixUnicode(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH));
        JsonObject backupjson = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH + "1", true));
        if (checkPatch(1.1, guild, "FixUnicode", json)) return;
        JsonArray commands = json.getAsJsonArray("commands");
        JsonArray oldCommands = backupjson.getAsJsonArray("commands");
        commands.forEach(command -> {
            oldCommands.forEach(oldCommand -> {
                if (oldCommand.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(command.getAsJsonObject().get("name").getAsString())) {
                    command.getAsJsonObject().remove("contents");
                    command.getAsJsonObject().add("contents", oldCommand.getAsJsonObject().get("contents"));
                }
            });
        });
        newPatchID(1.1, json);
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH), json);
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
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), Competition.FILE_PATH));
        if (checkPatch(1.0, guild, "Overhaul_Comp", json)) return;
        JsonArray array = json.getAsJsonArray("entries");
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
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), Competition.FILE_PATH), json);
    }

    private static void overhaulServers(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), Servers.FILE_PATH));
        if (checkPatch(1.0, guild, "Overhaul_Servers", json)) return;
        JsonArray array = json.getAsJsonArray("servers");
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
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), Servers.FILE_PATH), json);
    }

    private static void overhaulCustomCommands(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH));
        if (checkPatch(1.0, guild, "Overhaul_CustomCommands", json)) return;
        JsonArray array = json.getAsJsonArray("commands");
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
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), CustomCommands.FILE_PATH), json);
    }

    private static void overhaulGuildUsers(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH));
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
        muted.forEach(user -> {
            String oldItem = user.getAsJsonObject().get("userID").getAsString();
            long newItem = Long.parseUnsignedLong(oldItem);
            user.getAsJsonObject().remove("userID");
            user.getAsJsonObject().addProperty("userID", newItem);
        });
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), GuildUsers.FILE_PATH), json);
    }

    private static void overhaulCharacters(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), Characters.FILE_PATH));
        if (checkPatch(1.0, guild, "Overhaul_Characters", json)) return;
        JsonElement e = json.get("characters");
        JsonArray array = e.getAsJsonArray();
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
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), Characters.FILE_PATH), json);
    }

    private static void overhaulGuildConfig(IGuild guild) {
        JsonObject json = FileHandler.fileToJsonObject(Utility.getFilePath(guild.getLongID(), GuildConfig.FILE_PATH));
        if (checkPatch(1.0, guild, "Overhaul_GuildConfig", json)) return;
        JsonArray array = json.getAsJsonArray("channelSettings");
        array.forEach(jsonElement -> {
            JsonElement channelIDs = jsonElement.getAsJsonObject().get("channelIDs");
            JsonArray newIDs = new JsonArray();
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
        FileHandler.writeToJson(Utility.getFilePath(guild.getLongID(), GuildConfig.FILE_PATH), json);
    }

    public static void preInitPatches() {
        fixUnicodeDaily();
        if (Files.exists(Paths.get(Constants.FILE_GLOBAL_DATA))) {
            JsonObject json = FileHandler.fileToJsonObject(Constants.FILE_GLOBAL_DATA);
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
            FileHandler.writeToJson(Constants.FILE_GLOBAL_DATA, json);
        }
        if (Files.exists(Paths.get(Constants.FILE_CONFIG))) {
            JsonObject json = FileHandler.fileToJsonObject(Constants.FILE_CONFIG);
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


//        OldGlobalData oldData = (OldGlobalData) FileHandler.readFromJson(Constants.FILE_GLOBAL_DATA, OldGlobalData.class);
//        DailyMessages dailyMessages = (DailyMessages) DailyMessages.create(DailyMessages.FILE_PATH, new DailyMessages());
//        dailyMessages.getMessages().addAll(oldData.getDailyMessages());
//        dailyMessages.getQueue().addAll(oldData.getQueuedRequests());
//        dailyMessages.flushFile();
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

