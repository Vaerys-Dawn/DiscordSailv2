package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListChars implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
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
        builder.withColor(Utility.getUsersColour(command.client.bot, command.guild.get()));
        if (user.getProfile(command.guild).getSettings().contains(UserSetting.PRIVATE_PROFILE)) {
            Utility.sendEmbedMessage("", builder, command.user.get().getOrCreatePMChannel());
            return "> Char list sent to your Direct messages.";
        }
        Utility.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListChars", "Chars", "CharList"};
    }

    @Override
    public String description() {
        return "Shows you all of your characters.";
    }

    @Override
    public String usage() {
        return "(@User)";
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
        return false;
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
