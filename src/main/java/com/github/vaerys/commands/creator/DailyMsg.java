package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
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

public class DailyMsg implements Command {

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

    @Override
    public String[] names() {
        return new String[]{"DailyMsg"};
    }

    @Override
    public String description(CommandObject command) {
        return "allows for editing of the daily message list.\n**Tags:** " + Utility.listFormatter(TagList.getNames(TagList.DAILY), true) +"\n" + modes;
    }

    @Override
    public String usage() {
        return "[ID] (Mode) (args)";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
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