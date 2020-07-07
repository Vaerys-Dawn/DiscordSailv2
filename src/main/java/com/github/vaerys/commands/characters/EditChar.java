package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CharacterObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class EditChar extends Command {

    String modes = "**Modes:**\n" +
            "> Age - `Max Chars: 20`\n" +
            "> Gender - `Max Chars: 20`\n" +
            "> Height - `Max Chars: 20`\n" +
            "> Weight - `Max Chars: 20`\n" +
            "> Name - `Min Chars 2, Max chars 32`\n" +
            "> Roles - `Uses your current Cosmetic roles.`\n" +
            "> Avatar - `Needs Valid Image URL or Image`\n" +
            "> Bio - `Max Chars: 450`\n" +
            "> LongDesc - `Needs Valid URL`";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject charName = new SplitFirstObject(args);
        if (charName.getRest() == null || charName.getRest().isEmpty()) {
            return "\\> Mode Not Specified";
        }
        SplitFirstObject mode = new SplitFirstObject(charName.getRest());
        for (CharacterObject c : command.guild.characters.getCharacters(command.guild.get())) {
            if (c.getName().equalsIgnoreCase(charName.getFirstWord())) {
                if (c.getUserID() == command.user.longID || GuildHandler.canBypass(command.user.get(), command.guild.get())) {
                    String rest = mode.getRest();
                    if (rest == null) {
                        rest = "";
                    }
                    command.setAuthor(command.guild.getUserByID(c.getUserID()));
                    switch (mode.getFirstWord().toLowerCase()) {
                        case "age":
                            return CharEditModes.age(rest, c, command);
                        case "gender":
                            return CharEditModes.gender(rest, c, command);
                        case "avatar":
                            return CharEditModes.avatar(rest, c, command);
                        case "bio":
                            return CharEditModes.desc(rest, c, command);
                        case "longdesc":
                            return CharEditModes.longDesc(rest, c);
                        case "weight":
                            return CharEditModes.weight(rest, c, command);
                        case "height":
                            return CharEditModes.height(rest, c, command);
                        case "name":
                            return CharEditModes.name(rest, c);
                        case "roles":
                            return CharEditModes.roles(command, c);
                        default:
                            return "\\> Mode not Valid.";
                    }
                } else {
                    return command.user.notAllowed;
                }
            }
        }
        return "\\> Char with that name not found.";
    }

    @Override
    protected String[] names() {
        return new String[]{"EditChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows the User to edit their Character.\n" + modes;
    }

    @Override
    protected String usage() {
        return "[Character ID] [Mode] [Args]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CHARACTER;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.CHARACTER;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
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
