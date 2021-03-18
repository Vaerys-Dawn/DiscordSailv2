package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.CharacterObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListChars extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        UserObject user = command.user;
        String title = "\\> Here are all of your characters.";
        //get globalUser
        if (args != null && !args.isEmpty()) {
            user = Utility.getUser(command, args, true);
            if (user == null) {
                return "\\> Could not find globalUser.";
            }
            if (user.longID != command.user.longID) {
                title = "\\> Here are all of @" + user.displayName + "'s Characters.";
            }
        }
        //check private
        if (user.isPrivateProfile() && user.longID != command.user.longID) {
            return "\\> User has set their profile to private.";
        }
        //generate list
        List<String> list = user.getCharacters(command.guild.longID).stream().map(CharacterObject::getName).collect(Collectors.toList());
        //give message if empty
        if (list.isEmpty()) {
            return user.longID == command.user.longID ?
                    "\\> You do not have any characters yet. Create one with **" + new UpdateChar().getUsage(command) + "**." :
                    "\\> User does not have any characters yet.";
        }
        //build embed data
        builder.setTitle(title);
        builder.setDescription("```\n" + Utility.listFormatter(list, true) + "```\n" + new CharInfo().missingArgs(command));
        builder.setFooter(user.getCharacters(command.guild.longID).size() + "/" + command.guild.characters.maxCharsForUser(user, command.guild) + " Slots used.");
        //send private char list
        if (user.getProfile().getSettings().contains(UserSetting.PRIVATE_PROFILE)) {
            command.user.queueDm(builder.build());
            return "\\> Char list sent to your Direct messages.";
        }
        //send regular
        builder.queue(command);
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
    protected Permission[] perms() {
        return new Permission[0];
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
        // does nothing
    }
}
