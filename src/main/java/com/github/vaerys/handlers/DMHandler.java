package com.github.vaerys.handlers;

import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class DMHandler {

    final static Logger logger = LoggerFactory.getLogger(DMHandler.class);

    private static TreeMap<Instant, Long> lastMinUsers = new TreeMap<>();

    public DMHandler(DmCommandObject command) {
        if (command.message.get().getAuthor().isBot()) return;
        if (command.globalUser.longID == Globals.creatorID) return;

        Globals.lastDmUserID = command.globalUser.longID;

        sendLog(command);

        if (blockedUserHandler(command)) return;

        addUser(command);

        if (spamCatcher(command)) return;

        if (smartAssBlocker(command)) return;

        if (commandChecker(command)) return;

        if (inviteCatcher(command)) return;

        sendMessage(command);

        command.globalUser.queueDm("\\> Thank you for your message.");
    }

    private void sendMessage(DmCommandObject command) {
        String content = command.message.getContent();
        XEmbedBuilder builder = new XEmbedBuilder();
        Color color = command.globalUser.getRandomColour();
        builder.setColor(color);
        builder.setAuthor(command.globalUser.username + " | " + command.globalUser.longID, command.globalUser.avatarURL);
        int attachmentStart = 0;
        List<Message.Attachment> attachmentList = command.message.getAttachments();
        if (attachmentList.size() != 0 && Utility.isImageLink(attachmentList.get(0).getUrl())) {
            attachmentStart++;
            builder.setImage(attachmentList.get(0).getUrl());
        }
        if (attachmentList.size() > attachmentStart) {
            for (int i = attachmentStart; i < attachmentList.size(); i++) {
                if (content.length() != 0) {
                    content += "\n";
                }
                content += attachmentList.get(i).getUrl();
            }
        }
        builder.setDescription(content);
        command.client.creator.queueDm(builder.build());
    }

    private void addUser(DmCommandObject command) {
        lastMinUsers.put(command.message.getTimestamp(), command.globalUser.longID);
        Instant now = Instant.now().minusSeconds(20);
        lastMinUsers.entrySet().removeIf(e -> e.getKey().isBefore(now));
    }

    private long getCount(long userID) {
        return lastMinUsers.entrySet().stream().filter(l -> l.getValue() == userID).count();
    }

    private boolean blockedUserHandler(DmCommandObject command) {
        if (command.globalUser.isBlockedFromDms()) {
            command.globalUser.queueDm("\\> You have been blocked from sending DMs to S.A.I.L by the Bot Creator.");
            return true;
        }
        return false;
    }

    private boolean spamCatcher(DmCommandObject command) {
        long count = getCount(command.globalUser.longID);
        if (count == 7) {
            sendMessage(command);
            command.globalUser.queueDm("\\> Hey. Could you slow down with the dms? My creator doesn't need you spamming her.");
            return true;
        }
        if (count == 8) {
            sendMessage(command);
            command.globalUser.queueDm("\\> **Hey!**");
            return true;
        }
        if (count == 9) {
            sendMessage(command);
            command.globalUser.queueDm("\\> **Hey! Stop that!**");
            return true;
        }
        if (count == 10) {
            sendMessage(command);
            command.globalUser.queueDm("\\> **I'm going to block you if you don't stop...**");
            return true;
        }
        if (count > 10) {
            sendMessage(command);
            command.globalUser.queueDm("\\> **I warned you...**");
            Globals.getGlobalData().getBlockedFromDMS().add(command.globalUser.longID);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
            }
            command.globalUser.queueDm("\\> You were blocked.");
            command.client.creator.queueDm("\\> I blocked " + command.globalUser.username + " for spamming you.");
            return true;
        }
        return false;
    }

    private void sendLog(DmCommandObject command) {
        final String[] logging = {"[" + command.message.get().getAuthor().getIdLong() + "] " + command.message.get().getAuthor().getName() + "#" + command.message.get().getAuthor().getDiscriminator() + ": " + command.message.get().toString()};
        if (command.message.getAttachments().size() != 0) {
            command.message.getAttachments().forEach(attachment -> logging[0] += "\n" + attachment.getUrl());
        }
        logger.info(logging[0]);
    }

    private boolean commandChecker(DmCommandObject command) {
        List<GuildObject> guilds = Globals.getGuilds();
        List<String> prefixes = new LinkedList<>() {{
            add("!");
            add("!!");
            add("/");
            add("$");
            add("%");
        }};
        for (GuildObject g : guilds) {
            prefixes.add(g.config.getPrefixCommand());
            prefixes.add(g.config.getPrefixCC());
            prefixes.add(g.config.getPrefixAdminCC());
        }
        prefixes = prefixes.stream().distinct().collect(Collectors.toList());
        for (String p : prefixes) {
            if (command.message.getContent().startsWith(p)) {
                command.globalUser.queueDm("\\> Hey there, looks like you're trying to use a command only available on a guild. unfortunately I can't run guild only commands in my Direct messages.");
                return true;
            }
        }
        return false;
    }

    private boolean inviteCatcher(DmCommandObject command) {
        if (Pattern.compile("(?i)(discord\\.gg/|discordapp\\.com/Invite/)").matcher(command.message.getContent()).find()) {
            command.globalUser.queueDm("\\> Hey it looks like you are trying to send me a Invite link to a server. If you want me to " +
                    "join your server please send a request to the Support server found in my info command.\n" +
                    "Note: My Creator may be busy or asleep when you send the request so please be polite, " +
                    "I am still an invite only bot that still requires developer help to set up to keep that in mind.");
            return true;
        }
        return false;
    }

    private boolean smartAssBlocker(DmCommandObject command) {
        String step1 = "Thank you for your message.";
        String step2 = "That's what I just said, you don't have to repeat it.";
        String step3 = "Okay, do you like repeating the things bots say?";
        String step4 = "Alright this was funny to begin with but now its gotten too far. next time you do that, im going to block you.";
        String step5 = "I warned you...";
        if (command.message.getContent().toLowerCase().contains(step1.toLowerCase())) {
            command.globalUser.queueDm("\\> " + step2);
            return true;
        }
        if (command.message.getContent().toLowerCase().contains(step2.toLowerCase())) {
            command.globalUser.queueDm("\\> " + step3);
            return true;
        }
        if (command.message.getContent().toLowerCase().contains(step3.toLowerCase())) {
            command.globalUser.queueDm("\\> " + step4);
            return true;
        }
        if (command.message.getContent().toLowerCase().contains(step4.toLowerCase())) {
            command.globalUser.queueDm("\\> " + step5);
            Globals.getGlobalData().getBlockedFromDMS().add(command.globalUser.longID);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
            }
            command.globalUser.queueDm("\\> You were blocked.");
            command.client.creator.queueDm("\\> " + command.globalUser.username + " was blocked for being a smart ass.");
            return true;
        }
        return false;
    }
}
