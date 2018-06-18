package com.github.vaerys.commands.competition;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CompObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class RemoveEntry extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            int entry = Integer.parseInt(args) - 1;
            CompObject didRemove = command.guild.competition.getEntries().remove(entry);
            return "> Removed entry " + (entry + 1) + "\n<" + didRemove.getFileUrl() + ">";
        } catch (NumberFormatException e) {
            return "> Needs a valid number";
        } catch (IndexOutOfBoundsException e) {
            return "> Could not find entry";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"RemoveEntry"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes a entry";
    }

    @Override
    protected String usage() {
        return "[Entry Number]";
    }

    @Override
    protected SAILType type() {
        return SAILType.COMPETITION;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
