package Commands.RoleSelect;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Utility;
import Objects.RoleTypeObject;
import POGOs.GuildConfig;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListModifs implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        GuildConfig guildConfig = command.guildConfig;
        String title = "> Here are the **Modifier** roles you can choose from:\n";
        ArrayList<String> list = new ArrayList<>();
        EmbedBuilder builder = new EmbedBuilder();
        list.addAll(guildConfig.getModifierRoles().stream().map(RoleTypeObject::getRoleName).collect(Collectors.toList()));
        Utility.listFormatterEmbed(title,builder,list,true);
        builder.appendField(spacer, Utility.getCommandInfo(new ModifierRoles(), command),false);
        builder.withColor(Utility.getUsersColour(command.client.getOurUser(), command.guild));
        Utility.sendEmbededMessage("", builder.build(), command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListModifiers", "Modifiers", "Modifs"};
    }

    @Override
    public String description() {
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
