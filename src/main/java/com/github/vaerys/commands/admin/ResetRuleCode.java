package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class ResetRuleCode extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        IMessage working = RequestHandler.sendMessage("`Working...`", command.channel).get();
        for (ProfileObject p : command.guild.users.profiles) {
            if (p.getSettings().size() != 0) {
                p.getSettings().remove(UserSetting.READ_RULES);
            }
        }
        for (IUser u : command.guild.getUsers()) {
            GuildHandler.checkUsersRoles(u.getLongID(), command.guild);
        }
        RequestHandler.deleteMessage(working);
        return "Done. The Rule code tag has been removed off all profiles.";
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
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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