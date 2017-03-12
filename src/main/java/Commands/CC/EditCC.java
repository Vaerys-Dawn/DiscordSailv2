package Commands.CC;

import Interfaces.Command;
import Commands.CommandObject;
import Main.Utility;
import Objects.CCommandObject;
import Objects.SplitFirstObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EditCC implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject getName = new SplitFirstObject(args);
        if (getName.getRest() == null) {
            return Utility.getCommandInfo(this, command);
        }
        SplitFirstObject getMode = new SplitFirstObject(getName.getRest());
        String mode = getMode.getFirstWord();
        String content = getMode.getRest();
        for (CCommandObject c : command.customCommands.getCommandList()) {
            if (c.getName().equalsIgnoreCase(getName.getFirstWord())) {
                if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, command.author, command.guild) ||
                        command.authorID.equals(c.getUserID()) && !c.isLocked() ||
                        Utility.canBypass(command.author, command.guild)) {
                    if (StringUtils.countMatches(mode + " " + content, "#embedImage#{") > 1) {
                        return "> Custom Commands Cannot have multiple #embedImage# tags";
                    }
                    switch (mode.toLowerCase()) {
                        case "replace":
                            return editModeReplace(c, content);
                        case "toembed":
                            return editModeToEmbed(c);
                        case "append":
                            return editModeAppend(c, content);
                        case "delet":
                            return editModeDeleteTag(c);
                        case "delcall":
                            return editModeDeleteTag(c);
                        case "shitpost":
                            return editModeShitPost(c, command, command.author, command.guild);
                        case "lock":
                            return editModeLock(c,command,command.author,command.guild);
                        default:
                            if (content == null || content.isEmpty()) {
                                return editModeReplace(c, mode);
                            } else {
                                return editModeReplace(c, mode + " " + content);
                            }
                    }
                } else {
                    return "> You do not have permission to edit this command.";
                }
            }
        }
        return "> Command Not found.";
    }

    private String editModeLock(CCommandObject c, CommandObject command, IUser author, IGuild guild) {
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, author, guild)) {
            return "> Lock for " + c.getName() + " is now " + c.toggleShitPost() + ".";
        } else {
            return command.notAllowed;
        }
    }

    private String editModeShitPost(CCommandObject c, CommandObject command, IUser author, IGuild guild) {
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, author, guild)) {
            return "> Shitpost for " + c.getName() + " is now " + c.toggleShitPost() + ".";
        } else {
            return command.notAllowed;
        }
    }

    private String editModeDeleteTag(CCommandObject c) {
        String delCall = "#delCall#";
        if (c.getContents(false).contains(delCall)) {
            return "> Command will already delete the calling message.";
        } else {
            c.setContents(c.getContents(false) + delCall);
            return "> Tag added";
        }
    }


    private String editModeReplace(CCommandObject command, String content) {
        if (content == null || content.isEmpty()) {
            return "> Missing content to replace with.";
        }
        command.setContents(content);
        return "> Command Edited.";
    }

    private String editModeToEmbed(CCommandObject commmand) {
        String contents = commmand.getContents(false);
        if (contents.contains(" ") || contents.contains("\n")) {
            return "> Failed to add embed tag.";
        }
        if (contents.contains("#embedImage#{")) {
            return "> Command already has an EmbedImage Tag, cannot add more than one.";
        }
        commmand.setContents("#embedImage#{" + contents + "}");
        return "> Embed tag added.";
    }

    private String editModeAppend(CCommandObject command, String content) {
        if (content == null || content.isEmpty()) {
            return "> Missing content to append.";
        }
        if ((command.getContents(false) + content).length() > 2000) {
            return "> Cannot append content, would make command to large.";
        }
        command.setContents(command.getContents(false) + content);
        return "> Content appended to end of command.";
    }

    @Override
    public String[] names() {
        return new String[]{"EditCC"};
    }

    @Override
    public String description() {
        return "Allows you to edit a custom command.\n" +
                "Modes: Replace, Append, toEmbed,DelCall\n" +
                "**Admin Modes: **Lock, Shitpost\n\n" +
                "Mode is optional, defaults to replace.\n" +
                "[Custom Command Guide](https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/Custom-Command-Guide)";
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
