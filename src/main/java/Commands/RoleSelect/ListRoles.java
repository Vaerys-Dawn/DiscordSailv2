package Commands.RoleSelect;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Objects.RoleTypeObject;
import POGOs.GuildConfig;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListRoles implements Command{

    @Override
    public String execute(String args, CommandObject command) {
        IGuild guild = command.guild;
        GuildConfig guildConfig = command.guildConfig;
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are the **Cosmetic** roles you can choose from:\n");
        int i = 0;
        int counter = 0;
        for (RoleTypeObject r : guildConfig.getCosmeticRoles()) {
            counter++;
            if (counter == guildConfig.getCosmeticRoles().size()) {
                if (i != 0) {
                    builder.append(guild.getRoleByID(r.getRoleID()).getName() + ".\n");
                } else {
                    builder.append(Constants.PREFIX_INDENT + guild.getRoleByID(r.getRoleID()).getName() + ".\n");
                }
            } else if (i == 0) {
                builder.append(Constants.PREFIX_INDENT + guild.getRoleByID(r.getRoleID()).getName() + ", ");
            } else if (i == 7) {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ",\n");
                i = -1;
            } else {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ", ");
            }
            i++;
        }
        return builder.toString();
    }

    @Override
    public String[] names() {
        return new String[]{"ListRoles","Roles","RoleList"};
    }

    @Override
    public String description() {
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
