package com.github.vaerys.commands.admin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

/**
 * Created by Vaerys on 06/02/2017.
 */
public class UpdateRolePerms extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        List<IRole> parentRole = GuildHandler.getRolesByName(command.guild.get(), args);
        EnumSet parentPerms = command.guild.get().getEveryoneRole().getPermissions();
        ArrayList<String> permList = new ArrayList<>();
        IMessage workingMsg = RequestHandler.sendMessage("`Working...`", command.channel.get()).get();
        if (parentRole.size() != 0) {
            if (command.guild.config.isRoleCosmetic(parentRole.get(0).getLongID())) {
                parentPerms = parentRole.get(0).getPermissions();
            }
        }
        for (IRole r : command.guild.get().getRoles()) {
            if (command.guild.config.isRoleCosmetic(r.getLongID())) {
                if (!r.getPermissions().containsAll(parentPerms)) {
                    EnumSet finalParentPerms = parentPerms;
                    RequestBuffer.request(() -> r.changePermissions(finalParentPerms));
                }
            }
        }
        for (Object p : parentPerms.toArray()) {
            permList.add(p.toString());
        }
        RequestHandler.deleteMessage(workingMsg);
        return "> Cosmetic Roles Perms set to : " + Utility.listFormatter(permList, true);
    }

    protected static final String[] NAMES = new String[]{"UpdateRolePerms"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Sets permissions of all Cosmetic roles to mach those of a specific role.\nDefaults to Everyone Role.";
    }

    protected static final String USAGE = "(Parent Role Name)";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.ADMINISTRATOR};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
