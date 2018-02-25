package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.time.DayOfWeek;
import java.util.Formatter;
import java.util.ListIterator;

public class DailyMsg extends Command {

    String modes = "> Edit - `args = new Contents`\n" +
            "> Delete\n" +
            "> MoveDay\n" +
            "> Info - `Default`\n";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject uIDString = new SplitFirstObject(args);
        try {
            long uID = Long.parseLong(uIDString.getFirstWord());
            DailyMessage messageObject = null;
            for (DailyMessage u : Globals.getDailyMessages().getMessages()) {
                if (u.getUID() == uID) {
                    messageObject = u;
                }
            }
            if (messageObject == null) {
                return "> Could not find daily message with that UID.";
            }
            SplitFirstObject mode = null;
            String totest;
            if (uIDString.getRest() == null) {
                totest = "empty";
            } else {
                mode = new SplitFirstObject(uIDString.getRest());
                totest = mode.getFirstWord();
            }
            switch (totest.toLowerCase()) {
                case "edit":
                    if (mode.getRest() == null) {
                        return "> Missing args";
                    }
                    messageObject.setContents(mode.getRest());
                    return "> Daily Message contents updated";
                case "delete":
                    ListIterator iterator = Globals.getDailyMessages().getMessages().listIterator();
                    while (iterator.hasNext()) {
                        DailyMessage object = (DailyMessage) iterator.next();
                        if (object.getUID() == uID) {
                            iterator.remove();
                        }
                    }
                    return "> Daily Message Deleted";
                case "moveday":
                    if (mode.getRest() == null) {
                        return "> Missing args";
                    }
                    String day = mode.getRest().toUpperCase();
                    try {
                        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
                        messageObject.setDay(dayOfWeek);
                        return "> Moved message to " + dayOfWeek + ".";
                    } catch (IllegalArgumentException e) {
                        return "> Not a valid day of the week.";
                    }
                case "info":
                    RequestHandler.sendEmbedMessage("", getInfo(messageObject, command), command.channel.get());
                    return null;
                default:
                    RequestHandler.sendEmbedMessage("", getInfo(messageObject, command), command.channel.get());
                    return null;
            }
        } catch (NumberFormatException e) {
            return "> Invalid UID.";
        }
    }

    public XEmbedBuilder getInfo(DailyMessage messageObject, CommandObject command) {
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        IUser user = command.client.get().getUserByID(messageObject.getUserID());
        if (user != null) {
            embedBuilder.withAuthorName(user.getName() + "#" + user.getDiscriminator());
        }
        if (messageObject.getDay() != null) {
            embedBuilder.withTitle(messageObject.getDay() + "");
        }
        String contents = messageObject.getContents(new CommandObject(command.guild, command.channel.get()));
        if (contents.matches("^(> |\\*> |\\*\\*> |\\*\\*\\*> |_> |__> |`> |```> ).*$") || contents.startsWith("> ")) {
            embedBuilder.withDesc(contents);
        } else {
            embedBuilder.withDesc("> " + contents);
        }
        String formattedFooter;
        if (messageObject.getUID() != -1) {
            formattedFooter = new Formatter().format("UID: %04d", messageObject.getUID()).toString();
        } else {
            formattedFooter = messageObject.getSpecialID();
        }
        embedBuilder.withFooterText(formattedFooter);
        return embedBuilder;
    }

    protected static final String[] NAMES = new String[]{"DailyMsg"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "allows for editing of the daily message list.\n**Tags:** " + Utility.listFormatter(TagList.getNames(TagType.DAILY), true) +"\n" + modes;
    }

    protected static final String USAGE = "[ID] (Mode) (args)";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
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
