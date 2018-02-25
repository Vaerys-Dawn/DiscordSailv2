package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;
import sx.blah.discord.handle.obj.Permissions;

public class HelpModules extends Command {

    protected static final String[] NAMES = new String[]{"HelpModule", "HelpModules", "ModuleHelp"};
    protected static final String USAGE = "[Module Name]";
    protected static final SAILType COMMAND_TYPE = SAILType.HELP;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = false;

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
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about a module.";
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
