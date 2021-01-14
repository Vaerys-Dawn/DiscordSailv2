package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.events.TimedEvent;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.time.DayOfWeek;
import java.util.stream.Collectors;

public class EventSetup extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject eventName = new SplitFirstObject(args);
        TimedEvent event = null;
        if (eventName.getFirstWord().equalsIgnoreCase("list")) {
            XEmbedBuilder builder = new XEmbedBuilder(command);
            builder.setTitle("All Events:");
            String eventList = Utility.listFormatter(Globals.getEvents().stream().map(event1 -> event1.getEventName()).collect(Collectors.toList()), true);
            builder.setDescription("```\n" + eventList + spacer + "```");
            builder.queue(command);
            return null;
        }
        if (eventName.getRest() == null) {
            return "\\> Missing Mode.\n" + getModes();
        }
        for (TimedEvent t : Globals.getEvents()) {
            if (t.getEventName().equalsIgnoreCase(eventName.getFirstWord())) {
                event = t;
            }
        }
        if (event == null && eventName.getRest().equalsIgnoreCase("Create")) {
            Globals.getEvents().add(new TimedEvent(eventName.getFirstWord()));
            return "\\> Event Created.";
        } else {
            SplitFirstObject mode = new SplitFirstObject(eventName.getRest());
            if (event == null) {
                return "\\> Event does not exist yet.";
            }
            String output = doModes(event, mode, command);
            event.sanitizeDates();
            Globals.updateEvent();
            return output;
        }
    }

    private String doModes(TimedEvent event, SplitFirstObject mode, CommandObject command) {
        switch (mode.getFirstWord().toLowerCase()) {
            case "create":
                return "> Event Already Exists";
            case "delete":
                return deleteEvent(event);
            case "setstart":
                return setDate(event, mode.getRest(), true);
            case "setend":
                return setDate(event, mode.getRest(), false);
            case "setdate":
                return setDate(event, mode.getRest());
            case "avatars":
                return setSettingMode(event, mode.getRest(), true);
            case "messages":
                return setSettingMode(event, mode.getRest(), false);
            case "addmessage":
                return addMessage(event, mode.getRest(), command);
            case "delmessage":
                return delMessage(event, mode.getRest());
            case "addavatar":
                return addAvatar(event, mode.getRest(), command);
            case "delavatar":
                return delAvatar(event, mode.getRest());
            case "sethello":
                return setHello(event, mode.getRest());
            case "info":
                return event.getInfo(command);
            case "movetotop":
                return moveToTop(event);
            case "movetobottom":
                return moveToBottom(event);
            case "moveup":
                return moveUp(event);
            case "movedown":
                return moveDown(event);
            default:
                return getModes();
        }
    }

    private String moveDown(TimedEvent event) {
        int pos = Globals.getEvents().indexOf(event);
        if (pos == 0) {
            return "> event already at bottom.";
        }
        Globals.getEvents().remove(event);
        Globals.getEvents().add(pos - 1, event);
        return "> Moved Event down one position. (" + (pos - 1) + ")";
    }

    private String moveUp(TimedEvent event) {
        int pos = Globals.getEvents().indexOf(event);
        if (pos + 1 == Globals.getEvents().size()) {
            return "> event already at top.";
        }
        Globals.getEvents().remove(event);
        Globals.getEvents().add(pos + 1, event);
        return "> Moved Event up one position. (" + (pos + 1) + ")";
    }

    private String moveToBottom(TimedEvent event) {
        Globals.getEvents().remove(event);
        Globals.getEvents().add(0, event);
        return "> Moved event to the bottom of the list. (0)";
    }

    private String moveToTop(TimedEvent event) {
        Globals.getEvents().remove(event);
        Globals.getEvents().add(event);
        return "> Moved event to the top of the list. (" + (Globals.getEvents().size() - 1) + ")";
    }

    private String setHello(TimedEvent event, String rest) {
        if (rest == null) return "> Missing arguments.\n" + getModes();
        if (rest.equalsIgnoreCase("remove")) {
            event.setHelloMessage(null);
        }
        if (rest.contains("<globalUser>")) {
            if (rest.startsWith("> ")) {
                event.setHelloMessage(rest);
            } else {
                event.setHelloMessage("> " + rest);
            }
            return "> Hello message set.";
        } else {
            return "> Missing <globalUser> tag";
        }
    }

    private String delAvatar(TimedEvent event, String rest) {
        if (rest == null) {
            return event.removeAvatar(null);
        } else {
            DayOfWeek day;
            try {
                day = DayOfWeek.valueOf(rest.toUpperCase());
            } catch (IllegalArgumentException e) {
                if (rest.equalsIgnoreCase("All")) {
                    return event.clearAvatars();
                }
                return "> Invalid Day";
            }
            return event.removeAvatar(day);
        }
    }

    private String addAvatar(TimedEvent event, String rest, CommandObject command) {
        if (rest == null) {
            return imageAvatar(event, command, null);
        }
        SplitFirstObject daySplit = new SplitFirstObject(rest);
        DayOfWeek day;
        try {
            day = DayOfWeek.valueOf(daySplit.getFirstWord().toUpperCase());
        } catch (IllegalArgumentException e) {
            return "> Invalid Day.";
        }
        if (daySplit.getRest() == null) {
            return imageAvatar(event, command, day);
        } else {
            String url = daySplit.getRest();
            if (Utility.isImageLink(url) && !url.endsWith(".gif")) {
                return event.setAvatar(url, day);
            } else return "> Invalid Image.";
        }
    }

    private String imageAvatar(TimedEvent event, CommandObject command, DayOfWeek day) {
        if (command.message.getAttachments().size() != 0) {
            String url = command.message.getAttachments().get(0).getUrl();
            if (Utility.isImageLink(url) && !url.endsWith(".gif")) {
                return event.setAvatar(url, day);
            } else return "> Invalid Image.";
        } else return "> Missing arguments.\n" + getModes();
    }

    private String delMessage(TimedEvent event, String rest) {
        if (rest == null) return "> Missing arguments.\n" + getModes();
        for (DailyMessage m : event.getMessages()) {
            if (m.getSpecialID().equalsIgnoreCase(rest)) {
                event.getMessages().remove(m);
                return "> Message Deleted.";
            }
        }
        return "> Could not find message.";
    }

    private String addMessage(TimedEvent event, String rest, CommandObject command) {
        if (rest == null) return "> Missing arguments.\n" + getModes();
        SplitFirstObject messageID = new SplitFirstObject(rest);
        if (messageID.getRest() == null) return "> Missing arguments.\n" + getModes();
        for (DailyMessage m : event.getMessages()) {
            if (m.getSpecialID().equalsIgnoreCase(messageID.getFirstWord())) {
                return "> Message Id already in use.";
            }
        }
        DayOfWeek day = null;
        SplitFirstObject message = new SplitFirstObject(messageID.getRest());
        try {
            day = DayOfWeek.valueOf(message.getFirstWord().toUpperCase());
        } catch (IllegalArgumentException e) {
            // do nothing
        }
        if (day == null) {
            event.getMessages().add(new DailyMessage(message.getAll(), day, command.user.longID, messageID.getFirstWord()));
        } else {
            event.getMessages().add(new DailyMessage(message.getRest(), day, command.user.longID, messageID.getFirstWord()));
        }
        event.setSpecialMessages(true);
        return "> New message added.";
    }

    private String setSettingMode(TimedEvent event, String rest, boolean isAvatar) {
        if (rest == null) return "> Missing arguments.\n" + getModes();
        if (rest.equalsIgnoreCase("true") || rest.equalsIgnoreCase("false")) {
            boolean mode = Boolean.valueOf(rest);
            if (isAvatar) {
                event.setDoAvatars(mode);
                return "> doRotateAvatars is now " + mode;
            } else {
                event.setSpecialMessages(mode);
                return "> doSpecialMessages is now " + mode;
            }
        } else {
            return "> Not a valid state, True/False only.";
        }
    }

    private String setDate(TimedEvent event, String rest, boolean... isStart) {
        if (rest == null) return "> Missing arguments.\n" + getModes();
        try {
            String[] split = rest.split(" ");
            int month = Integer.parseInt(split[1]);
            int day = Integer.parseInt(split[0]);
            if (day < 1 || day > 31) {
                return "> Invalid Day Value.";
            }
            if (month < 1 || month > 12) {
                return "> Invalid Month Value.";
            }
            if (isStart.length != 0) {
                if (isStart[0]) {
                    event.setStartDay(day);
                    event.setStartMonth(month);
                    return "> Start Date Updated.";
                } else {
                    event.setEndDay(day);
                    event.setEndMonth(month);
                    return "> End Date Updated.";
                }
            } else {
                event.setStartDay(day);
                event.setStartMonth(month);
                event.setEndDay(day);
                event.setEndMonth(month);
                return "> Date Updated.";
            }
        } catch (NumberFormatException e) {
            return "> Invalid Date.";
        } catch (IndexOutOfBoundsException e) {
            return "> Missing Arguments.\n" + getModes();
        }
    }

    private String deleteEvent(TimedEvent event) {
        if (Globals.getEvents().remove(event)) return "> Event Deleted.";
        else return "> You should not get this message.";
    }

    public String getModes() {
        return "**Modes:**\n" +
                "> Create\n" +
                "> Delete\n" +
                "> SetStart [StartDay] [StartMonth]\n" +
                "> SetEnd [EndDay] [EndMonth]\n" +
                "> Set Date [Day] [Month]\n" +
                "> Avatars [True/False]\n" +
                "> Messages [True/False]\n" +
                "> AddMessage [MessageID] (DayOfWeek) [Message]\n" +
                "> DelMessage [MessageID]\n" +
                "> AddAvatar (DayOfWeek) [Link/Image]\n" +
                "> DelAvatar (DayOfWeek/All)\n" +
                "> SetHello [Message/Remove]\n" +
                "> MoveToTop\n" +
                "> MoveToBottom\n" +
                "> MoveUp\n" +
                "> MoveDown\n" +
                "> Info";
    }

    @Override
    protected String[] names() {
        return new String[]{"EventSetup"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for Setting up Events.\n" + getModes();
    }

    @Override
    protected String usage() {
        return "[EventName/List] (Mode) (Args)";
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
