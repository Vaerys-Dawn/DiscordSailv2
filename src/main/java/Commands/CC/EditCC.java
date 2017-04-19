package Commands.CC;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.CCommandObject;
import Objects.SplitFirstObject;
import org.apache.commons.lang3.StringUtils;
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
                boolean canBypass = false;
                canBypass = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, command.author, command.guild);
                if (canBypass ||
                        command.authorID.equals(c.getUserID()) && !c.isLocked() ||
                        Utility.canBypass(command.author, command.guild)) {
                    if (StringUtils.countMatches(mode + " " + content, "#embedImage#{") > 1) {
                        return "> Custom Commands Cannot have multiple #embedImage# tags";
                    }
                    if(c.isLocked() && !canBypass){
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
                            return CCEditModes.shitPost(c, command, command.author, command.guild);
                        case "lock":
                            return CCEditModes.lock(c,command,command.author,command.guild);
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
    public String description() {
        return "Allows you to edit a custom command.\n" +
                "Modes: Replace, Append, toEmbed,DelCall\n" +
                "**Admin Modes: **lock, Shitpost\n\n" +
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
