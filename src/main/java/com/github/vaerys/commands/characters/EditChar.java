package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class EditChar implements Command {

    String modes = "**Modes:**\n" +
            "> Age - `Max Chars: 20`\n" +
            "> Gender - `Max Chars: 20`\n" +
            "> Avatar - `Needs Valid Image URL`\n" +
            "> Bio - `Max Chars: 300`\n" +
            "> LongDesc - `Needs Valid URL`";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject charName = new SplitFirstObject(args);
        if (charName.getRest() == null || charName.getRest().isEmpty()) {
            return "> Mode Not Specified";
        }
        SplitFirstObject mode = new SplitFirstObject(charName.getRest());
        for (CharacterObject c : command.guild.characters.getCharacters(command.guild.get())) {
            if (c.getName().equalsIgnoreCase(charName.getFirstWord())) {
                if (c.getUserID().equals(command.user.stringID) || Utility.canBypass(command.user.get(), command.guild.get())) {
                    if (mode.getRest() == null || mode.getRest().isEmpty()) {
                        return "> Missing Arguments for Editing.";
                    }
                    switch (mode.getFirstWord().toLowerCase()) {
                        case "age":
                            return CharEditModes.age(mode.getRest(), c);
                        case "gender":
                            return CharEditModes.gender(mode.getRest(), c);
                        case "avatar":
                            return CharEditModes.avatar(mode.getRest(), c);
                        case "bio":
                            return CharEditModes.desc(mode.getRest(), c);
                        case "longdesc":
                            return CharEditModes.longDesc(mode.getRest(), c);
                        default:
                            return "> Mode not Valid.";
                    }
                } else {
                    return command.user.notAllowed;
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
        return "Allows the User to edit their Character.\n" + modes;
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
