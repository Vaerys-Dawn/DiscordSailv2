package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Vaerys on 06/02/2017.
 */
public class UpdateRolePerms extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<Role> parentRole = GuildHandler.getRolesByName(command.guild.get(), args);
        EnumSet<Permission> parentPerms = command.guild.get().getPublicRole().getPermissions();
        ArrayList<String> permList = new ArrayList<>();
        Message workingMsg = command.guildChannel.sendMessage("`Working...`");
        if (!parentRole.isEmpty() && command.guild.config.isRoleCosmetic(parentRole.get(0).getIdLong())) {
            parentPerms = parentRole.get(0).getPermissions();
        }
        for (Role r : command.guild.get().getRoles()) {
            boolean hasPerms = !r.getPermissions().containsAll(parentPerms) && !parentPerms.containsAll(r.getPermissions());
            boolean isCosmetic = command.guild.config.isRoleCosmetic(r.getIdLong());
            if (isCosmetic && hasPerms) {
                r.getManager().setPermissions(parentPerms).queue();
            }
        }
        for (Object p : parentPerms.toArray()) {
            permList.add(p.toString());
        }
        workingMsg.delete().complete();
        return "\\> Cosmetic Roles Perms set to : " + Utility.listFormatter(permList, true);
    }

    @Override
    protected String[] names() {
        return new String[]{"UpdateRolePerms"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets permissions of all Cosmetic roles to mach those of a specific role.\nDefaults to Everyone Role.";
    }

    @Override
    protected String usage() {
        return "(Parent Role Name)";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_ROLES, Permission.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {
        // does nothing
    }
}
