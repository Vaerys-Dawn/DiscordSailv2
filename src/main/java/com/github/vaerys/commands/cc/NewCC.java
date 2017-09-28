package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.TagHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class NewCC implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_MAKE_CC)) {
            return "> You have been denied the creation of custom commands.";
        }
        boolean isShitpost = false;
        boolean isLocked = false;
        SplitFirstObject splitfirst = new SplitFirstObject(args);
        String newContent;
        List<IChannel> shitpostChannels = command.guild.config.getChannelsByType(Command.CHANNEL_SHITPOST, command.guild);
        if (shitpostChannels != null) {
            for (IChannel channel : shitpostChannels) {
                if (command.channel.longID == channel.getLongID()) {
                    isShitpost = true;
                }
            }
        }
        if (object.getSettings().contains(UserSetting.AUTO_SHITPOST)) {
            isShitpost = true;
        }
        String nameCC = splitfirst.getFirstWord();
        if (splitfirst.getRest() == null || splitfirst.getRest().isEmpty()) {
            return "> Custom command contents cannot be blank.";
        }
        if (nameCC.contains("\n")) {
            return "> Command name cannot contain Newlines.";
        }
        String content = splitfirst.getRest();
        newContent = TagHandler.testForShit(content);
        if (!newContent.equals(content)) {
            isShitpost = true;
        }
        content = newContent;
        newContent = TagHandler.testForLock(content, command.user.get(), command.guild.get());
        if (!newContent.equals(content)) {
            isLocked = true;
        }
        content = newContent;
        return command.guild.customCommands.addCommand(isLocked, nameCC, content, isShitpost, command);
    }

    @Override
    public String[] names() {
        return new String[]{"NewCC"};
    }

    @Override
    public String description() {
        return "Creates a Custom Command.";
    }

    @Override
    public String usage() {
        return "[Command Name] [Contents]";
    }

    @Override
    public String type() {
        return TYPE_CC;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
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
