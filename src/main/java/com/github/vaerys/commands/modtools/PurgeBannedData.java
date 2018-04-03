package com.github.vaerys.commands.modtools;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.ServerObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ListIterator;

public class PurgeBannedData extends Command {

    private long purgedProfiles = 0;
    private long purgedCCs = 0;
    private long purgedCharacters = 0;
    private long purgedServers = 0;
    private long purgedDailyMessages = 0;

    @Override
    public String execute(String args, CommandObject command) {
        purgedProfiles = 0;
        purgedCCs = 0;
        purgedCharacters = 0;
        purgedServers = 0;
        purgedDailyMessages = 0;

        long userId = -1;
        String userStringID = null;
        try {
            userId = Long.parseLong(args);
            userStringID = args;
            if (userStringID.equalsIgnoreCase(Globals.creatorID + "")) {
                return "> You cannot purge the bot owner's data, if you have found an error in their data please DM me with the details.";
            }
            if (userId == command.guild.getOwnerID()) {
                return "> You cannot purge the servers owner's data, if you have found an error in their data please DM me with the details.";
            }
            IUser toTest = command.guild.getUserByID(userId);
            if (toTest != null) {
                if (toTest.getPermissionsForGuild(command.guild.get()).contains(Permissions.ADMINISTRATOR)) {
                    return "> You cannot purge a user with the administrator permission, if you have found an error in their data please DM me with the details.";
                }
            }
        } catch (NumberFormatException e) {
            //do nothing
        }
        if (!command.client.bot.get().getPermissionsForGuild(command.guild.get()).contains(Permissions.BAN)) {
            return "> I cant purge the data of banned user unless I getAllCommands the ban permission.\n" +
                    "Feel free to remove the permission after you purge the data as I don't need it.";
        }
        if (userId != -1 && userStringID != null) {
            purgeData(userId, command);
        } else {
            for (IUser user : command.guild.get().getBannedUsers()) {
                purgeData(user.getLongID(), command);
            }
        }
        return "> Purged Profiles: **" + purgedProfiles + "**." +
                "\n> Purged CCs: **" + purgedCCs + "**." +
                "\n> Purged Characters: **" + purgedCharacters + "**." +
                "\n> Purged Server Listings: **" + purgedServers + "**.";
//                "\n> Purged Daily Messages: **" + purgedDailyMessages + "**.";
    }

    public void purgeData(long userID, CommandObject command) {
        ListIterator iterator = command.guild.users.profiles.listIterator();
        while (iterator.hasNext()) {
            ProfileObject object = (ProfileObject) iterator.next();
            if (userID == object.getUserID()) {
                iterator.remove();
                purgedProfiles++;
            }
        }
        iterator = command.guild.customCommands.getCommandList().listIterator();
        while (iterator.hasNext()) {
            CCommandObject ccObject = (CCommandObject) iterator.next();
            if (userID == ccObject.getUserID()) {
                iterator.remove();
                purgedCCs++;
            }
        }
        iterator = command.guild.characters.getCharacters(command.guild.get()).listIterator();
        while (iterator.hasNext()) {
            CharacterObject charObject = (CharacterObject) iterator.next();
            if (charObject.getUserID() == userID) {
                iterator.remove();
                purgedCharacters++;
            }
        }
        iterator = command.guild.servers.getServers().listIterator();
        while (iterator.hasNext()) {
            ServerObject serverObject = (ServerObject) iterator.next();
            if (serverObject.getCreatorID() == userID) {
                iterator.remove();
                purgedServers++;
            }
        }
//        iterator = Globals.getDailyMessages().getMessages().listIterator();
//        while (iterator.hasNext()) {
//            DailyMessage dailyObject = (DailyMessage) iterator.next();
//            if (dailyObject.getUserID() == userID) {
//                iterator.remove();
//                purgedDailyMessages++;
//            }
//        }
    }

    @Override
    protected String[] names() {
        return new String[]{"PurgeBannedData"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes all data related to banned users from the server's data. or just the data from one user.\n\n" +
                "***!!! WARNING: IF YOU USE A USER_ID YOU WILL PURGE ALL DATA FOR THAT USER REGARDLESS OF STATUS SO BE CAREFUL !!!***\n";
    }

    @Override
    protected String usage() {
        return "(UserID)";
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.ADMINISTRATOR, Permissions.BAN};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
