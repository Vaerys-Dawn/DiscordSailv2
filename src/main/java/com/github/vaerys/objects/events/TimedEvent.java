package com.github.vaerys.objects.events;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class TimedEvent {
    String eventName = "event";
    boolean doAvatars = false;
    boolean specialMessages = false;
    int startDay = -1;
    int endDay = -1;
    int startMonth = -1;
    int endMonth = -1;
    String helloMessage = null;
    List<DailyMessage> messages = new ArrayList<>();
    List<EventAvatar> avatars = new ArrayList<>();

    public TimedEvent(String eventName) {
        this.eventName = eventName;
    }

    public String getHelloMessage() {
        return helloMessage;
    }

    public void setHelloMessage(String helloMessage) {
        this.helloMessage = helloMessage;
    }

    public void setDoAvatars(boolean doAvatars) {
        this.doAvatars = doAvatars;
    }

    public void setSpecialMessages(boolean specialMessages) {
        this.specialMessages = specialMessages;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean doRotateAvatars() {
        return doAvatars;
    }

    public boolean doSpecialMessages() {
        return specialMessages;
    }

    public List<DailyMessage> getMessages() {
        return messages;
    }

    public boolean isSingleDayEvent() {
        return (startDay == endDay && startMonth == endMonth);
    }

    public List<DailyMessage> getMessagesDay(DayOfWeek day) {
        List<DailyMessage> messageList = new ArrayList<>();
        for (DailyMessage d : messages) {
            if (d.getDay() != null && d.getDay().equals(day)) {
                messageList.add(d);
            }
        }
        if (messageList.size() == 0) {
            return getDefaultMessages();
        }
        return messageList;
    }

    public List<DailyMessage> getDefaultMessages() {
        List<DailyMessage> messageList = new ArrayList<>();
        for (DailyMessage d : messages) {
            if (d.getDay() == null) {
                messageList.add(d);
            }
        }
        return messageList;
    }

    public EventAvatar getAvatarDay(DayOfWeek day) {
        EventAvatar avatar = null;
        for (EventAvatar a : avatars) {
            if (a.day != null && a.day == day) {
                avatar = a;
            }
        }
        if (avatar == null) {
            return getDefaultAvatar();
        }
        return avatar;
    }

    public EventAvatar getDefaultAvatar() {
        for (EventAvatar a : avatars) {
            if (a.day == null) {
                return a;
            }
        }
        return null;
    }

    public boolean isEventActive() {
        if (!isValid()) return false;
        //getAllToggles instance
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        now.plusMinutes(30);
        ZonedDateTime start = now.withMonth(startMonth).withDayOfMonth(startDay);
        ZonedDateTime end = now.withMonth(endMonth).withDayOfMonth(endDay);
        //tests to see if the current date is the same as the start or end date
        if (start.getDayOfYear() == now.getDayOfYear() || end.getDayOfYear() == now.getDayOfYear()) return true;
        //returns true if the current date is between the start and end dates
        return start.isBefore(now) && end.isAfter(now);
    }

    public String setAvatar(String url, DayOfWeek day) {
        boolean found = false;
        for (EventAvatar a : avatars) {
            if (a.day == day) {
                a.setLink(url);
                found = true;
            }
        }
        if (!found) avatars.add(new EventAvatar(day, url));
        doAvatars = true;
        if (day == null) return "\\> Default Avatar Set.";
        return "\\> " + day + " Avatar Created.";
    }

    public String clearAvatars() {
        avatars = new ArrayList<>();
        doAvatars = false;
        return "\\> All avatars cleared.";
    }

    public String removeAvatar(DayOfWeek day) {
        for (EventAvatar a : avatars) {
            if (a.day == day) {
                avatars.remove(a);
                if (day == null) {
                    return "\\> Default Avatar removed.";
                }
                return "\\> " + day + " Avatar Removed.";
            }
        }
        return "\\> Could not remove avatar.";
    }

    public String getInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle("Event - " + eventName);
        StringBuilder desc = new StringBuilder();
        desc.append("**Position:** " + Globals.getEvents().indexOf(this));
        desc.append("\n**Valid Event:** " + isValid());
        desc.append("\n**Currently Active:** " + isEventActive());
        desc.append("\n**Does Avatars:** " + doAvatars);
        desc.append("\n**Does Special Messages:** " + specialMessages);
        String dayStart = startDay + Utility.getDateSuffix(startDay);
        String dayEnd = endDay + Utility.getDateSuffix(endDay);
        String monthStart = "null";
        String monthEnd = "null";
        if (startMonth != -1) monthStart = new DateFormatSymbols().getMonths()[startMonth - 1];
        if (endMonth != -1) monthEnd = new DateFormatSymbols().getMonths()[endMonth - 1];

        if (isSingleDayEvent()) {
            desc.append("\n**Date:** " + dayStart + " of " + monthStart + ".");
        } else {
            desc.append("\n**Date:** " + dayStart + " of " + monthStart + " to the ");
            desc.append(dayEnd + " of " + monthEnd + ".");
        }
        if (helloMessage != null) desc.append("\n**Hello Message:** " + helloMessage);
        String messageList = Utility.listFormatter(messages.stream().map(eventDailyMessage -> eventDailyMessage.getSpecialID()).collect(Collectors.toList()), true);
        String avatarList = Utility.listFormatter(avatars.stream().map(eventAvatar -> {
            if (eventAvatar.day == null) return "Default";
            else return eventAvatar.day.toString();
        }).collect(Collectors.toList()), true);
        if (messageList.length() != 0) desc.append("\n**Messages:**\n```\n" + messageList + Command.spacer + "```");
        if (avatarList.length() != 0) desc.append("\n**Avatars:**\n```\n" + avatarList + Command.spacer + "```");
        builder.setDescription(desc.toString());
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    public boolean isValid() {
        if (startDay == -1 || endDay == -1 || startMonth == -1 || endMonth == -1) return false;
        if (startDay > endDay && startMonth >= endMonth) return false;
        return true;
    }

    public void sanitizeDates() {
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        calStart.set(Calendar.MONTH, startMonth - 1);
        calEnd.set(Calendar.MONTH, endMonth - 1);
        //make sure that the startDay wont crash
        int max = calStart.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (startDay > max) {
            startDay = calStart.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        //make sure that the endDay wont crash
        max = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (endDay > max) {
            endDay = max;
        }
    }
}
