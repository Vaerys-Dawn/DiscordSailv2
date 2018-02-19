package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CompObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
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

    protected static final String[] NAMES = new String[]{"RemoveEntry"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Removes a entry";
    }

    protected static final String USAGE = "[Entry Number]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.COMPETITION;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}