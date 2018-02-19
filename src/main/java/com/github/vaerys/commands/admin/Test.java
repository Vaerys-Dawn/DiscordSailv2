package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;

import java.util.EnumSet;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test extends Command {

    String nothing = "> You didn't see anything.";

    @Override
    public String execute(String args, CommandObject command) {
//
//        IEmoji emoji = command.guild.getEmojiByName(args);
//        if (emoji == null) return "> Not a valid emoji name.";
//        return emoji.toString();

//        XEmbedBuilder builder = new XEmbedBuilder(command);
//        builder.withAuthorName("Note 1 - " +command.user.displayName);
//        builder.withAuthorIcon(command.user.getAvatarURL());
//        builder.withDescription("blah blah this is a note.\n\n" +
//                "`Last edited: " + Utility.formatTime(10, true) + " ago.`");
//        builder.withTimestamp(command.message.getTimestamp());
//        builder.withFooterText("Created by " + command.client.bot.displayName);
//        builder.withFooterIcon(command.client.bot.getAvatarURL());
//        builder.send(command.channel);
//        return nothing;
//        return (long) ((90 - 7) * (Globals.avgMessagesPerDay * command.guild.config.xpRate * command.guild.config.xpModifier) / 8) + "";
        EnumSet<Permissions> botPerms = command.client.bot.getPermissions(command.guild);
        return botPerms.contains(Permissions.MANAGE_CHANNELS) ? "> I HAVE MANAGE_CHANNELS" : "> I DO NOT HAVE MANAGE_CHANNELS";
//        throw new DiscordException("TestException");
    }


    @Override
    public String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description(CommandObject command) {
        return "Tests Things.";
    }

    @Override
    public String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
    public void init() {

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
