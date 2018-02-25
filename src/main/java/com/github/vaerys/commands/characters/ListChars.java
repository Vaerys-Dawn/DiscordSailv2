package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListChars extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        UserObject user = command.user;
        String title = "> Here are all of your characters.";
        if (args != null && !args.isEmpty()) {
            user = Utility.getUser(command, args, true);
            if (user == null) {
                return "> Could not find user.";
            }
            if (user.longID != command.user.longID) {
                title = "> Here are all of **@" + user.displayName + "'s** Characters.";
            }
        }
        if (user.isPrivateProfile(command.guild) && user.longID != command.user.longID) {
            return "> User has set their profile to private.";
        }
        ArrayList<String> list = new ArrayList<>();
        for (CharacterObject c : command.guild.characters.getCharacters(command.guild.get())) {
            if (c.getUserID() == user.longID) {
                list.add(c.getName());
            }
        }
        Utility.listFormatterEmbed(title, builder, list, true);
        builder.appendField(spacer, Utility.getCommandInfo(new CharInfo(), command), false);
        builder.withFooterText(command.user.characters.size() + "/" + command.guild.characters.maxCharsForUser(user, command.guild) + " Slots used.");
        if (user.getProfile(command.guild).getSettings().contains(UserSetting.PRIVATE_PROFILE)) {
            RequestHandler.sendEmbedMessage("", builder, command.user.get().getOrCreatePMChannel());
            return "> Char list sent to your Direct messages.";
        }
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"ListChars", "Chars", "CharList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows you all of your characters.";
    }

    @Override
    protected String usage() {
        return "(@User)";
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
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
