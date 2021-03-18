package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;


/**
 * Created by Vaerys on 01/02/2017.
 */
public class DelCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        CCommandObject cc = command.guild.customCommands.getCommand(args);
        if (cc == null) return Constants.ERROR_CC_NOT_FOUND;
        Member author = command.user.getMember();
        boolean canBypass = GuildHandler.testForPerms(author, command.guild.get(), Permission.MESSAGE_MANAGE);
        if (author.getIdLong() == command.guild.getOwnerID()
                || author.getIdLong() == Globals.creatorID
                || author.getIdLong() == command.botUser.longID) {
            canBypass = true;
        }
        if (author.getIdLong() == cc.getUserID() || canBypass) {
            if (cc.isLocked() && command.botUser.longID != author.getIdLong()) {
                return "\\> This command is locked and must be unlocked to be deleted.";
            } else {
                command.guild.customCommands.removeCommand(cc);
                return "\\> Command Deleted.";
            }
        } else {
            return "\\> You do not have permission to delete that command.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"DelCC", "DeleteCC", "RemoveCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Deletes The custom command.";
    }

    @Override
    protected String usage() {
        return "[Command Name]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CC;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.MANAGE_CC;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {
        // does nothing
    }
}
