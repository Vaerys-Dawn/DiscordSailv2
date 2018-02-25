package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
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
    long purgedProfiles = 0;
    long purgedCCs = 0;
    long purgedCharacters = 0;
    long purgedServers = 0;
    long purgedDailyMessages = 0;

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
            return "> I cant purge the data of banned user unless I getToggles the ban permission.\n" +
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
            if (userID == object.getUserID()){
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

    protected static final String[] NAMES = new String[]{"PurgeBannedData"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Removes all data related to banned users from the server's data. or just the data from one user.\n\n" +
                "***!!! WARNING: IF YOU USE A USER_ID YOU WILL PURGE ALL DATA FOR THAT USER REGARDLESS OF STATUS SO BE CAREFUL !!!***\n";
    }

    protected static final String USAGE = "(UserID)";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }


    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.ADMINISTRATOR, Permissions.BAN};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
