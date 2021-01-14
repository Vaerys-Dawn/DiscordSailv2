package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.objects.userlevel.CharacterObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.userlevel.ServerObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ListIterator;

public class PurgeBannedData extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        /***
         * 0: Profiles
         * 1: Custom Commands
         * 2: Characters
         * 3: Servers
         */
        long[] purgedData = new long[]{0, 0, 0, 0};

        long userId = -1;
        String userStringID = null;
        try {
            userId = Long.parseLong(args);
            userStringID = args;
            if (userStringID.equalsIgnoreCase(Globals.creatorID + "")) {
                return "\\> You cannot purge the bot owner's data, if you have found an error in their data please DM me with the details.";
            }
            if (userId == command.guild.getOwnerID()) {
                return "\\> You cannot purge the servers owner's data, if you have found an error in their data please DM me with the details.";
            }
            Member toTest = command.guild.getUserByID(userId);
            if (toTest != null) {
                if (toTest.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    return "\\> You cannot purge a globalUser with the administrator permission, if you have found an error in their data please DM me with the details.";
                }
            }
        } catch (NumberFormatException e) {
            //do nothing
        }
        if (!command.botUser.getPermissions().contains(Permission.BAN_MEMBERS)) {
            return "\\> I cant purge the data of banned globalUser unless I get the **Ban Members** permission.\n" +
                    "Feel free to remove the permission after you purge the data as I will no longer need it.";
        }
        if (userId != -1 && userStringID != null) {
            purgeData(userId, command, purgedData);
        } else {
            for (Guild.Ban user : command.guild.get().retrieveBanList().complete()) {
                purgeData(user.getUser().getIdLong(), command, purgedData);
            }
        }
        String response = "\\> Purged Profiles: **" + purgedData[0] + "**." +
                "\\> Purged CCs: **" + purgedData[1] + "**." +
                "\n\\> Purged Characters: **" + purgedData[2] + "**." +
                "\n\\> Purged Server Listings: **" + purgedData[3] + "**." +
                "\n\nYou are now free to remove my **Ban Members** Permission as I no longer need it.";

        return response;
    }

    public void purgeData(long userID, CommandObject command, long[] purgedData) {
        ListIterator iterator = command.guild.users.profiles.listIterator();
        while (iterator.hasNext()) {
            ProfileObject object = (ProfileObject) iterator.next();
            if (userID == object.getUserID()) {
                iterator.remove();
                purgedData[0]++;
            }
        }
        iterator = command.guild.customCommands.getCommandList().listIterator();
        while (iterator.hasNext()) {
            CCommandObject ccObject = (CCommandObject) iterator.next();
            if (userID == ccObject.getUserID()) {
                iterator.remove();
                purgedData[1]++;
            }
        }
        iterator = command.guild.characters.getCharacters(command.guild.get()).listIterator();
        while (iterator.hasNext()) {
            CharacterObject charObject = (CharacterObject) iterator.next();
            if (charObject.getUserID() == userID) {
                iterator.remove();
                purgedData[2]++;
            }
        }
        iterator = command.guild.servers.getServers().listIterator();
        while (iterator.hasNext()) {
            ServerObject serverObject = (ServerObject) iterator.next();
            if (serverObject.getCreatorID() == userID) {
                iterator.remove();
                purgedData[3]++;
            }
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"PurgeBannedData"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes all data related to banned users from the server's data. or just the data from one globalUser.\n\n" +
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
    protected Permission[] perms() {
        return new Permission[]{Permission.ADMINISTRATOR, Permission.BAN_MEMBERS};
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
