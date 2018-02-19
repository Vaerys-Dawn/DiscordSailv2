package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EditCC extends Command {

    private static String modes = "**Modes: **\n" +
            "> Replace\n" +
            "> ToEmbed\n" +
            "> DelCall\n" +
            "> AddSearch\n";
    private static String adminModes = "**Admin Modes:**\n" +
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


    protected static final String[] NAMES = new String[]{"EditCC"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit a custom command.\n" + modes +
                "**[Custom Command Guide](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/Custom-Command-Guide)**";
    }

    protected static final String USAGE = "[Command Name] (Mode) [New Contents/Image]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CC;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.MANAGE_CC;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    protected static final SubCommandObject SUB_1 = new SubCommandObject(
        NAMES,
        "[Command Name] (Mode)",
        "allows editing of other user's commands or editing toggles.\n" + adminModes,
        SAILType.ADMIN,
        new Permissions[]{Permissions.MANAGE_MESSAGES}
    );
    @Override
    public void init() {
        subCommands.add(SUB_1);
    }
}
