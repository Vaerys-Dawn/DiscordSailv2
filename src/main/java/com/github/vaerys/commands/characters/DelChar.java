package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class DelChar extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        boolean bypass = false;
        if (GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) {
            bypass = true;
        }
        return command.guild.characters.delChar(args.split(" ")[0], command.user.get(), command.guild.get(), bypass);
    }

    @Override
    protected String[] names() {
        return new String[]{"DelChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Deletes a Character.";
    }

    @Override
    protected String usage() {
        return "[Character ID]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CHARACTER;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.CHARACTER;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
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
