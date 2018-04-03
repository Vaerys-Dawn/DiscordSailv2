package com.github.vaerys.commands.admin;

import com.github.vaerys.masterobjects.CommandObject;
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
            new String[]{"Boop", "Snugg"},
            "[Test Data]",
            "nothing",
            SAILType.MOD_TOOLS
    );
    String nothing = "> You didn't see anything.";

    @Override
    public String execute(String args, CommandObject command) {

//        StringHandler output = new StringHandler();
//        output.append("Next step is to pick which settings you want to use.\n")
//                .append("There's a lot of settings in modules, and those will be set there.\n")
//                .append("Here's a list of the settings that aren't tied to any specific module.\n\n");
//
//        List<GuildToggle> globalToggles = ToggleInit.getAllToggles(false);
//        List<GuildToggle> modules = ToggleInit.getAllToggles(true);
//        List<String> enabled = new LinkedList<>();
//        List<String> disabled = new LinkedList<>();
//
//
//        globalToggles.sort(Comparator.comparing(GuildToggle::name));
//
//        List<SAILType> types = new LinkedList<>();
//
//        modules.forEach(t -> types.addAll(t.settings.stream().map(s -> s.name()).collect(Collectors.toList())));
//
//        if (!new DebugMode().getDefault()) {
//            globalToggles.removeIf(t -> t.name() == SAILType.DEBUG_MODE);
//        }
//
//        ListIterator iterator = globalToggles.listIterator();
//        while (iterator.hasNext()) {
//            GuildToggle toggle = (GuildToggle) iterator.next();
//            if (types.contains(toggle.name())) {
//                iterator.remove();
//            } else {
//                if (toggle.enabled(command.guild.config)) enabled.add(toggle.name().toString());
//                else disabled.add(toggle.name().toString());
//            }
//        }
//
//        String format = "\t> **%s** - %s\n";
//        for (GuildToggle t : globalToggles) {
//            output.appendFormatted(format, t.name().toString(), t.shortDesc(command));
//        }
//        output.append("\n");
//
//        output.append("You can switch settings on and off with **" +
//                new Toggle().getCommand(command) + "** and getAllCommands more info on each setting with **" +
//                new HelpSettings().getCommand(command) + "**.");
//
//        XEmbedBuilder embed = new XEmbedBuilder(command);
//        embed.withTitle("Global Settings");
//        embed.appendField("Enabled", "```" + Utility.listFormatter(enabled, true) + "```", false);
//        embed.appendField("Disabled", "```" + Utility.listFormatter(disabled, true) + "```", false);
//        RequestHandler.sendEmbedMessage(output.toString(),embed, command.channel);
//        return null;


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
