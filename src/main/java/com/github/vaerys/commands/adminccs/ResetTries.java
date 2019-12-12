package com.github.vaerys.commands.adminccs;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class ResetTries extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        AdminCCObject cc = command.guild.adminCCs.getCommand(args);
        if (cc == null) return "\\> Could not find Admin Custom Command.";
        command.guild.adminCCs.removeTries(cc);
        cc.removeAllKeys();
        return "\\> Attempts for this command have been reset.";
    }

    @Override
    public String[] names() {
        return new String[]{"ResetTries"};
    }

    @Override
    public String description(CommandObject command) {
        return "Resets the attempts for an Admin Custom Command.";
    }

    @Override
    public String usage() {
        return "[Admin Custom Command Name]";
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN_CC;
    }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER,Permissions.MANAGE_ROLES,Permissions.MANAGE_MESSAGES};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}