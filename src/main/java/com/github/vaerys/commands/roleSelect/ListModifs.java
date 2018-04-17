package com.github.vaerys.commands.roleSelect;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListModifs extends Command {

    public static XEmbedBuilder getList(CommandObject command) {
        String title = "> Here are the Modifier roles you can choose from:";
        List<String> list = command.guild.getModifierRoles().stream().map(iRole -> iRole.getName()).collect(Collectors.toList());
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(title);
        builder.withDesc("```\n" + Utility.listFormatter(list, true) + "```\n" + get(ModifierRoles.class).missingArgs(command));
        return builder;
    }

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.config.getModifierRoleIDs().size() == 0)
            return "> No Modifier roles are set up right now. Come back later.";
        RequestHandler.sendEmbedMessage("", getList(command), command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"ListModifiers", "Modifiers", "Modifs"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows the list of modifier roles you can choose from.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.ROLE_SELECT;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.BOT_COMMANDS;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
