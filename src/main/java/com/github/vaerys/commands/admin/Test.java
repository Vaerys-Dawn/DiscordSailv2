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

    protected static final String[] NAMES = new String[]{"Test", "Testing"};

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Tests Things.";
    }

    protected static final String USAGE = "[Lol this command has no usages XD]";

    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    protected static final SubCommandObject object = new SubCommandObject(
            new String[]{"Test"},
            USAGE,
            "nothing",
            SAILType.ADMIN
    );

    @Override
    public void init() {
        subCommands.add(object.appendRegex(" (\\+|-)"));
    }
}
