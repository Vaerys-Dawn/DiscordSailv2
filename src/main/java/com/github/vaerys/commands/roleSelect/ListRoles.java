package com.github.vaerys.commands.roleSelect;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListRoles implements Command {

    public static XEmbedBuilder getList(CommandObject command) {
        String title = "> Here are the **Cosmetic** roles you can choose from:\n";
        ArrayList<String> list = new ArrayList<>();
        XEmbedBuilder builder = new XEmbedBuilder(command);
        for (long l : command.guild.config.getCosmeticRoleIDs()) {
            IRole role = command.guild.getRoleByID(l);
            list.add(role.getName());
        }
        Utility.listFormatterEmbed(title, builder, list, true);
        builder.appendField(spacer, Utility.getCommandInfo(new CosmeticRoles(), command), false);
        return builder;
    }

    @Override
    public String execute(String args, CommandObject command) {
        RequestHandler.sendEmbedMessage("", getList(command), command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListRoles", "Roles", "RoleList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows the list of cosmetic roles you can choose from.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_ROLE_SELECT;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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
