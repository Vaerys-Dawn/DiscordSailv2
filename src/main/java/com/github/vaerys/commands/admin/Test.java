package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test extends Command {

    protected static final SubCommandObject object = new SubCommandObject(
            new String[]{"Test"},
            "[Lol this command has no usages XD]",
            "nothing",
            SAILType.ADMIN
    );
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

//        EnumSet<Permissions> botPerms = command.client.bot.getPermissions(command.guild);
//        return botPerms.contains(Permissions.MANAGE_CHANNELS) ? "> I HAVE MANAGE_CHANNELS" : "> I DO NOT HAVE MANAGE_CHANNELS";

//        return object.isSubCommand(command) + " " + object.getArgs(command) + " " + object.getCommandUsage(command);
        throw new DiscordException("TestException");
    }

    @Override
    protected String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description(CommandObject command) {
        return "Tests Things.";
    }

    @Override
    protected String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
        subCommands.add(object.appendRegex(" (\\+|-)"));
    }
}
