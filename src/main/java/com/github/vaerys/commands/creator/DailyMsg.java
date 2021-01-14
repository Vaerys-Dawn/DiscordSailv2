package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.time.DayOfWeek;
import java.util.Formatter;
import java.util.ListIterator;

public class DailyMsg extends Command {

    String modes = "\\> Edit - `args = new Contents`\n" +
            "\\> Delete\n" +
            "\\> MoveDay\n" +
            "\\> Info - `Default`\n";

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
                return "\\> Could not find daily message with that UID.";
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
                        return "\\> Missing args";
                    }
                    messageObject.setContents(mode.getRest());
                    return "\\> Daily Message contents updated";
                case "delete":
                    ListIterator iterator = Globals.getDailyMessages().getMessages().listIterator();
                    while (iterator.hasNext()) {
                        DailyMessage object = (DailyMessage) iterator.next();
                        if (object.getUID() == uID) {
                            iterator.remove();
                        }
                    }
                    return "\\> Daily Message Deleted";
                case "moveday":
                    if (mode.getRest() == null) {
                        return "> Missing args";
                    }
                    String day = mode.getRest().toUpperCase();
                    try {
                        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
                        messageObject.setDay(dayOfWeek);
                        return "\\> Moved message to " + dayOfWeek + ".";
                    } catch (IllegalArgumentException e) {
                        return "\\> Not a valid day of the week.";
                    }
                case "info":
                    getInfo(messageObject,command).queue(command);
                    return null;
                default:
                    getInfo(messageObject,command).queue(command);
                    return null;
            }
        } catch (NumberFormatException e) {
            return "\\> Invalid UID.";
        }
    }

    public XEmbedBuilder getInfo(DailyMessage messageObject, CommandObject command) {
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        User user = command.client.get().getUserById(messageObject.getUserID());
        if (user != null) {
            embedBuilder.setAuthor(user.getName() + "#" + user.getDiscriminator());
        }
        if (messageObject.getDay() != null) {
            embedBuilder.setTitle(messageObject.getDay() + "");
        }
        String contents = messageObject.getContents(new CommandObject(command.guild, command.guildChannel.get()));
        if (contents.matches("^(> |\\*> |\\*\\*> |\\*\\*\\*> |_> |__> |`> |```> ).*$") || contents.startsWith("> ")) {
            embedBuilder.setDescription(contents);
        } else {
            embedBuilder.setDescription("\\> " + contents);
        }
        String formattedFooter;
        if (messageObject.getUID() != -1) {
            formattedFooter = new Formatter().format("UID: %04d", messageObject.getUID()).toString();
        } else {
            formattedFooter = messageObject.getSpecialID();
        }
        embedBuilder.setFooter(formattedFooter);
        return embedBuilder;
    }

    @Override
    protected String[] names() {
        return new String[]{"DailyMsg"};
    }

    @Override
    public String description(CommandObject command) {
        return "allows for editing of the daily message list.\n**Tags:** " + Utility.listFormatter(TagList.getNames(TagType.DAILY), true) + "\n" + modes;
    }

    @Override
    protected String usage() {
        return "[ID] (Mode) (args)";
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
