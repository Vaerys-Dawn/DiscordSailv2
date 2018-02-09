package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Vaerys on 06/02/2017.
 */
public class UpdateRolePerms extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        List<IRole> parentRole = Utility.getRolesByName(command.guild.get(), args);
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

    @Override
    public String[] names() {
        return new String[]{"UpdateRolePerms"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets permissions of all Cosmetic roles to mach those of a specific role.\nDefaults to Everyone Role.";
    }

    @Override
    public String usage() {
        return "(Parent Role Name)";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.ADMINISTRATOR};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
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
