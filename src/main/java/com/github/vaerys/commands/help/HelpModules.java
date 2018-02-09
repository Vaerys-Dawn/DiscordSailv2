package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.XEmbedBuilder;
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
            if (t.isModule() == isModule && t.name().equalsIgnoreCase(args)) {
                return t.info(command);
            }
        }
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"HelpModule","HelpModules","ModuleHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about a module.";
    }

    @Override
    public String usage() {
        return "[Module Name]";
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}