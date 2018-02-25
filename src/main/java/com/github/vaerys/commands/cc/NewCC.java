package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class NewCC extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_MAKE_CC)) {
            return "> You have been denied the creation of custom commands.";
        }
        if (command.guild.getChannelsByType(ChannelSetting.CC_DENIED).contains(command.channel.get()))
            return "> This Channel has CCs Denied, You cannot create ccs here.";
        boolean isShitpost = false;
        boolean isLocked = false;
        SplitFirstObject splitFirst = new SplitFirstObject(args);
        List<IChannel> shitpostChannels = command.guild.getChannelsByType(ChannelSetting.SHITPOST);
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
        String nameCC = splitFirst.getFirstWord();
        String argsCC = splitFirst.getRest();
        if ((argsCC == null || argsCC.isEmpty()) && command.message.get().getAttachments().size() == 0) {
            return "> Custom command contents cannot be blank.";
        }
        if (command.message.get().getAttachments().size() != 0) {
            String testLink = command.message.get().getAttachments().get(0).getUrl();
            if (Utility.isImageLink(testLink)) {
                if (argsCC == null || argsCC.isEmpty()) {
                    argsCC = "<embedImage>{" + testLink + "}";
                } else {
                    argsCC += "<embedImage>{" + testLink + "}";
                }
            } else {
                return "> Custom command attachment must be a valid Image.";
            }
        }
        if (nameCC.contains("\n")) {
            return "> Command name cannot contain Newlines.";
        }
        if (argsCC.contains("<shitpost>")) {
            argsCC.replace("<shitpost>", "");
            isShitpost = true;
        }
        if (argsCC.contains("<lock>") && GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) {
            argsCC.replace("<lock>", "");
            isLocked = true;
        }
        return command.guild.customCommands.addCommand(isLocked, nameCC, argsCC, isShitpost, command);
    }

    @Override
    protected String[] names() {
        return new String[]{"NewCC", "CCNew"};
    }

    @Override
    public String description(CommandObject command) {
        return "Creates a Custom Command.";
    }

    @Override
    protected String usage() {
        return "[Command Name] [Contents/Image]";
    }

    @Override
    protected SAILType type() {
        return  SAILType.CC;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.MANAGE_CC;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
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

    }
}
