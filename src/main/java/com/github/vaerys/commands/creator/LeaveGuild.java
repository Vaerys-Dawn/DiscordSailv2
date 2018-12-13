package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

public class LeaveGuild extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        IGuild guild;

        // figure out if SAIL wants to leave the current guild, or a specific one.
        if(args.isEmpty()) {
            guild = command.guild.get();
        } else {
            long guildID;
            try {
                guildID = Long.parseUnsignedLong(args);
            } catch (NumberFormatException e) {
                return "> Could not parse guildID.";
            }
            guild = Client.getClient().getGuildByID(guildID);
            if (guild == null) {
                return "> No guild with that ID.";
            }
        }
        guild.leave();

        return "> Guild left. Guild's data can safely be deleted from \"" + Constants.DIRECTORY_STORAGE + guild.getLongID() + "\"." ;
    }

    @Override
    protected String[] names() {
        return new String[] {"LeaveGuild", "Leave"};
    }

    @Override
    public String description(CommandObject command) {
        return "Leaves the specified guild, or the current guild if none is specified.";
    }

    @Override
    protected String usage() {
        return "(guild long ID)";
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    protected void init() {

    }
}
