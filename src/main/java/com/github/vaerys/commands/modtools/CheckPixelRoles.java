package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

/**
 * Created by Vaerys on 12/07/2017.
 */
public class CheckPixelRoles extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        Message working = command.guildChannel.sendMessage("`Working...`");
        for (Member user : command.guild.getUsers()) {
            GuildHandler.checkUsersRoles(user.getIdLong(), command.guild, true);
        }
        GuildHandler.checkTopTen(command.guild);
        working.delete().complete();
        return "\\> Done.";
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
