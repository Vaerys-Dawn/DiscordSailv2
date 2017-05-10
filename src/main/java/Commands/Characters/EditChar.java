package Commands.Characters;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.CharacterObject;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class EditChar implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject charName = new SplitFirstObject(args);
        if (charName.getRest() == null || charName.getRest().isEmpty()){
            return "> Mode Not Specified";
        }
        SplitFirstObject mode = new SplitFirstObject(charName.getRest());
        for (CharacterObject c: command.characters.getCharacters()){
            if (c.getName().equalsIgnoreCase(charName.getFirstWord())){
                if (c.getUserID().equals(command.authorSID) || Utility.canBypass(command.author,command.guild)){
                    if (mode.getRest() == null || mode.getRest().isEmpty()){
                        return "> Missing Arguments for Editing.";
                    }
                    switch (mode.getFirstWord().toLowerCase()){
                        case "age":
                            return CharEditModes.age(mode.getRest(),c);
                        case "gender":
                            return CharEditModes.gender(mode.getRest(),c);
                        case "avatar":
                            return CharEditModes.avatar(mode.getRest(),c);
                        case "desc":
                            return CharEditModes.desc(mode.getRest(),c);
                        case "longdesc":
                            return CharEditModes.longDesc(mode.getRest(),c);
                        default:
                            return "> Mode not Valid.";
                    }
                }else {
                    return command.notAllowed;
                }
            }
        }
        return "> Char with that name not found.";
    }

    @Override
    public String[] names() {
        return new String[]{"EditChar"};
    }

    @Override
    public String description() {
        return "Allows the User to edit their Character.\n" +
                "Modes: age, Avatar, Desc, Gender, LongDesc\n" +
                "\nAvatar and LongDesc need valid URLS.";
    }

    @Override
    public String usage() {
        return "[Char Name] [Mode] [Args]";
    }

    @Override
    public String type() {
        return TYPE_CHARACTER;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
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
