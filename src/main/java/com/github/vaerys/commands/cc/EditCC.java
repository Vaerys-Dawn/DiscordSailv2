package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.objects.utils.SubCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EditCC extends Command {

    private static String adminModes = "**Admin Modes:**\n" +
            "> Shitpost\n" +
            "> Lock\n";
    private static final SubCommandObject SUB_1 = new SubCommandObject(
            new String[]{"EditCC"},
            "[Command Name] (Mode)",
            "allows editing of other user's commands or editing toggles.\n" + adminModes,
            SAILType.MOD_TOOLS,
            new Permissions[]{Permissions.MANAGE_MESSAGES}
    );

    @Override
    public String execute(String args, CommandObject command) {
        ProfileObject object = command.guild.users.getUserByID(command.user.longID);
        if (object != null && object.getSettings().contains(UserSetting.DENY_MAKE_CC)) {
            return "\\> " + command.user.mention() + ", You have been denied the modification of custom commands.";
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
                return "\\> Custom command attachment must be a valid Image.";
            }
        }
        SplitFirstObject getMode = new SplitFirstObject(rest);
        String mode = getMode.getFirstWord();
        String content = getMode.getRest();
        CCommandObject customCommand = command.guild.customCommands.getCommand(getName.getFirstWord());
        if (customCommand == null) {
            return "\\> Command Not found.";
        }
        boolean canBypass = GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES);
        boolean isAuthor = command.user.longID == customCommand.getUserID();
        //test if can edit
        if ((customCommand.isLocked() && !canBypass) || (!canBypass && !isAuthor)) {
            return "\\> You do not have permission to edit this command.";
        }
        if (customCommand.isLocked() && !canBypass) {
            return "\\> This command is locked and cannot be edited.";
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
                return CCEditModes.addSearchTag(customCommand, content);
            case "removesearch":
                return CCEditModes.removeSearchTag(customCommand, content);
            default:
                if (content == null || content.isEmpty()) {
                    return CCEditModes.replace(customCommand, mode, command);
                } else {
                    return CCEditModes.replace(customCommand, mode + " " + content, command);
                }
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"EditCC"};
    }

    @Override
    public String description(CommandObject command) {
        String modes = "**Modes: **\n" +
                "> Replace\n" +
                "> ToEmbed\n" +
                "> DelCall\n" +
                "> AddSearch\n" +
                "> RemoveSearch\n";
        return "Allows you to edit a custom command.\n" + modes +
                "**[Custom Command Guide](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/Custom-Command-Guide)**";
    }

    @Override
    protected String usage() {
        return "[Command Name] (Mode) [New Contents/Image]";
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
        subCommands.add(SUB_1);
    }
}
