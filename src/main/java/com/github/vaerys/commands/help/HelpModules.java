package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;
import sx.blah.discord.handle.obj.Permissions;

public class HelpModules extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = getInfo(true, args, command);
        if (builder == null) {
            return "> Could not find any modules named **" + args + "**.";
        } else {
            RequestHandler.sendEmbedMessage("", builder, command.channel.get());
            return null;
        }
    }

    public XEmbedBuilder getInfo(boolean isModule, String args, CommandObject command) {
        for (GuildToggle t : command.guild.toggles) {
            if (t.isModule() == isModule && t.name().toString().equalsIgnoreCase(args)) {
                return t.info(command);
            }
        }
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"HelpModule", "HelpModules", "ModuleHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about a module.";
    }

    @Override
    protected String usage() {
        return "[Module Name]";
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
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
