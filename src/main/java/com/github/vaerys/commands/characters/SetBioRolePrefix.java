package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 30/06/2017.
 */
public class SetBioRolePrefix extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) {
            command.guild.characters.setRolePrefix("");
            return "\\> Role Prefix Removed.";
        }
        if (args.length() < 20) {
            command.guild.characters.setRolePrefix(args);
            return "\\> Role Prefix Updated.";
        } else {
            return "\\> Role Prefix is too long.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetCharRolePrefix"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set what shows before the roles of a character.";
    }

    @Override
    protected String usage() {
        return "(Prefix)";
    }

    @Override
    protected SAILType type() {
        return SAILType.CHARACTER;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
