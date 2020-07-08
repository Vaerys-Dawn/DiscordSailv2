package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import sx.blah.discord.handle.obj.Message;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class ResetRuleCode extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        Message working = command.channel.get().sendMessage("`Working...`").complete();
        for (ProfileObject p : command.guild.users.profiles) {
            if (p.getSettings().size() != 0) {
                p.getSettings().remove(UserSetting.READ_RULES);
            }
        }
        for (Member u : command.guild.getUsers()) {
            GuildHandler.checkUsersRoles(u.getIdLong(), command.guild);
        }
        working.delete();
        return "\\> Done. The Rule code tag has been removed off all profiles.";
    }

    @Override
    public String[] names() {
        return new String[]{"ResetRuleCode"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes all of the Rule code tags off all profiles.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
}