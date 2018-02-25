package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class EditChar extends Command {

    String modes = "**Modes:**\n" +
            "> Age - `Max Chars: 20`\n" +
            "> Gender - `Max Chars: 20`\n" +
            "> Avatar - `Needs Valid Image URL or Image`\n" +
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

    protected static final String[] NAMES = new String[]{"EditChar"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows the User to edit their Character.\n" + modes;
    }

    protected static final String USAGE = "[Character ID] [Mode] [Args]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CHARACTER;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.CHARACTER;
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

    @Override
    public void init() {

    }
}
