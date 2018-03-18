package com.github.vaerys.commands.groups;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class ClearGroupUp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.guild.channelData.getGroupUpObjects().clear();
        return "> GroupUp list cleared.";
    }

    @Override
    protected String[] names() {
        return new String[]{"ClearGroupUp", "PurgeGroupUp", "EmptyGroupUp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Purges the GroupUp list.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.GROUPS;
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
