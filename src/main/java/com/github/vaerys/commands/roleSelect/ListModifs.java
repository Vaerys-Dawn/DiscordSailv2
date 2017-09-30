package com.github.vaerys.commands.roleSelect;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListModifs implements Command {
    public static XEmbedBuilder getList(CommandObject command) {
        String title = "> Here are the **Modifier** roles you can choose from:\n";
        ArrayList<String> list = new ArrayList<>();
        XEmbedBuilder builder = new XEmbedBuilder();
        for (long l : command.guild.config.getModifierRoleIDs()) {
            IRole role = command.guild.get().getRoleByID(l);
            list.add(role.getName());
        }
        Utility.listFormatterEmbed(title, builder, list, true);
        builder.appendField(spacer, Utility.getCommandInfo(new ModifierRoles(), command), false);
        builder.withColor(command.client.color);
        return builder;
    }

    @Override
    public String execute(String args, CommandObject command) {
        Utility.sendEmbedMessage("",getList(command),command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListModifiers", "Modifiers", "Modifs"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows the list of modifier roles you can choose from.";
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
