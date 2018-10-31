package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 12/07/2017.
 */
public class CheckPixelRoles extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        IMessage working = RequestHandler.sendMessage("`Working...`", command.channel.get()).get();
        for (IUser user : command.guild.getUsers()) {
            GuildHandler.checkUsersRoles(user.getLongID(), command.guild, true);
        }
        GuildHandler.checkTopTen(command.guild);
        RequestHandler.deleteMessage(working);
        return "> Done.";
    }

    @Override
    protected String[] names() {
        return new String[]{"CheckUserRoles"};
    }

    @Override
    public String description(CommandObject command) {
        return "checks all user's Roles and allocates the correct roles based on their Pixel stats.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
