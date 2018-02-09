package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EditCC extends Command {

    String modes = "**Modes: **\n" +
            "> Replace\n" +
            "> ToEmbed\n" +
            "> DelCall\n" +
            "> AddSearch\n";
    String adminModes = "**Admin Modes:**\n" +
            "> Shitpost\n" +
            "> Lock\n";

    @Override
    public String execute(String args, CommandObject command) {
        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_MAKE_CC)) {
            return "> You have been denied the modification of custom commands.";
        }
        SplitFirstObject getName = new SplitFirstObject(args);
        String rest = getName.getRest();
        if (rest == null) {
            rest = "";
        }
        if (command.message.get().getAttachments().size() != 0) {
            String testLink = command.message.get().getAttachments().get(0).getUrl();
            if (Utility.isImageLink(testLink)) {
                if (rest.length() > 0) {
                    rest += " ";
                }
                rest += "<embedImage>{" + testLink + "}";
            } else {
                return "> Custom command attachment must be a valid Image.";
            }
        }
        SplitFirstObject getMode = new SplitFirstObject(rest);
        String mode = getMode.getFirstWord();
        String content = getMode.getRest();
        CCommandObject customCommand = command.guild.customCommands.getCommand(getName.getFirstWord());
        if (customCommand == null) {
            return "> Command Not found.";
        }
        boolean canBypass = Utility.testForPerms(command, Permissions.MANAGE_MESSAGES);
        boolean isAuthor = command.user.longID == customCommand.getUserID();
        //test if can edit
        if ((customCommand.isLocked() && !canBypass) || (!canBypass && !isAuthor)) {
            return "> You do not have permission to edit this command.";
        }
        if (command.guild.customCommands.checkblackList(args) != null) {
            return command.guild.customCommands.checkblackList(args);
        }
        if (customCommand.isLocked() && !canBypass) {
            return "> This command is locked and cannot be edited.";
        }
        switch (mode.toLowerCase()) {
            case "replace":
                return CCEditModes.replace(customCommand, content, command);
            case "toembed":
                return CCEditModes.toEmbed(customCommand);
            case "append":
                return CCEditModes.append(customCommand, content, command);
            case "delet":
            case "delcall":
                return CCEditModes.deleteTag(customCommand);
            case "shitpost":
                return CCEditModes.shitPost(customCommand, command, command.user.get(), command.guild.get());
            case "lock":
                return CCEditModes.lock(customCommand, command, command.user.get(), command.guild.get());
            case "addsearch":
                return CCEditModes.addReplaceTag(customCommand, content);
            default:
                if (content == null || content.isEmpty()) {
                    return CCEditModes.replace(customCommand, mode, command);
                } else {
                    return CCEditModes.replace(customCommand, mode + " " + content, command);
                }
        }
    }


    @Override
    public String[] names() {
        return new String[]{"EditCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit a custom command.\n" + modes +
                "**[Custom Command Guide](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/Custom-Command-Guide)**";
    }

    @Override
    public String usage() {
        return "[Command Name] (Mode) [New Contents/Image]";
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
    public void init() {

    }

    @Override
    public String dualDescription() {
        return "allows editing of other user's commands or editing toggles.\n" + adminModes;
    }

    @Override
    public String dualUsage() {
        return "[Command Name] (Mode)";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }
}
