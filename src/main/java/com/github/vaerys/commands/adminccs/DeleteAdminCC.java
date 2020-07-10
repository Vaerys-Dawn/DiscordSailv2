package com.github.vaerys.commands.adminccs;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

public class DeleteAdminCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.adminCCs.commandExists(args)){
            command.guild.adminCCs.removeCommand(args);
            return "\\> Admin Custom Command Deleted.";
        }else {
            return "\\> Could not find any admin custom commands with that name.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"DeleteAdminCC","DelAdminCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for the deletion of Admin Custom Commands.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
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
    public Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER, Permission.MANAGE_ROLES, Permission.MESSAGE_MANAGE};
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