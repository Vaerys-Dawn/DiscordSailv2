package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EditCC implements Command {

    String modes = "**Modes: **\n" +
            "> Replace\n" +
            "> ToEmbed\n" +
            "> DelCall\n";
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
        if (getName.getRest() == null) {
            return Utility.getCommandInfo(this, command);
        }
        SplitFirstObject getMode = new SplitFirstObject(getName.getRest());
        String mode = getMode.getFirstWord();
        String content = getMode.getRest();
        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (c.getName().equalsIgnoreCase(getName.getFirstWord())) {
                boolean canBypass = false;
                canBypass = Utility.testForPerms(command,Permissions.MANAGE_MESSAGES);
                if (canBypass ||
                        command.user.longID == c.getUserID() && !c.isLocked() ||
                        Utility.canBypass(command.user.get(), command.guild.get())) {
                    if (command.guild.customCommands.checkblackList(args) != null) {
                        return command.guild.customCommands.checkblackList(args);
                    }
                    if (StringUtils.countMatches(mode + " " + content, "#embedImage#{") > 1) {
                        return "> Custom Commands Cannot have multiple #embedImage# tags";
                    }
                    if (c.isLocked() && !canBypass) {
                        return "> This command is locked and cannot be edited.";
                    }
                    switch (mode.toLowerCase()) {
                        case "replace":
                            return CCEditModes.replace(c, content);
                        case "toembed":
                            return CCEditModes.toEmbed(c);
                        case "append":
                            return CCEditModes.append(c, content);
                        case "delet":
                            return CCEditModes.deleteTag(c);
                        case "delcall":
                            return CCEditModes.deleteTag(c);
                        case "shitpost":
                            return CCEditModes.shitPost(c, command, command.user.get(), command.guild.get());
                        case "lock":
                            return CCEditModes.lock(c, command, command.user.get(), command.guild.get());
                        default:
                            if (content == null || content.isEmpty()) {
                                return CCEditModes.replace(c, mode);
                            } else {
                                return CCEditModes.replace(c, mode + " " + content);
                            }
                    }
                } else {
                    return "> You do not have permission to edit this command.";
                }
            }
        }
        return "> Command Not found.";
    }


    @Override
    public String[] names() {
        return new String[]{"EditCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit a custom command.\n" + modes + adminModes +
                "**[Custom Command Guide](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/Custom-Command-Guide)**";
    }

    @Override
    public String usage() {
        return "[Command Name] (Mode) [New Contents]";
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
