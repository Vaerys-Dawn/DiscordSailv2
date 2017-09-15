package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XRequestBuffer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class ArtHandler {

    public ArtHandler(CommandObject command) {
        if (command.user.get().isBot()) {
            return;
        }
        List<IChannel> channelIDS = command.guild.config.getChannelsByType(Command.CHANNEL_ART, command.guild);
        if (channelIDS.size() == 0) {
            return;
        }
        if (channelIDS.get(0).getLongID() != command.channel.longID) {
            return;
        }
        if (command.message.get().getAttachments().size() != 0) {
            if (Utility.isImageLink(command.message.get().getAttachments().get(0).getUrl())) {
                try {
                    command.message.get().getChannel().pin(command.message.get());
                    checkList(command, true);
                    return;
                } catch (DiscordException e) {
                    if (e.getErrorMessage().contains("already pinned")) {
                        return;
                    } else {
                        Utility.sendStack(e);
                    }
                }

            }
        } else {
            for (String nl : command.message.get().getContent().split("/n")) {
                for (String s : nl.split(" ")) {
                    if (Utility.isImageLink(s) || isHostingWebsite(s)) {
                        try {
                            command.message.get().getChannel().pin(command.message.get());
                            checkList(command, true);
                            return;
                        } catch (DiscordException e) {
                            //do nothing
                        }
                    }
                }
            }
            return;
        }
    }

    private void checkList(CommandObject command, boolean adding) {
        ArrayList<Long> pinnedMessages = command.guild.channelData.getPinnedMessages();

        //check for null'd pins
        for (int i = 0; i < pinnedMessages.size(); i++) {
            if (command.channel.get().getMessageByID(pinnedMessages.get(i)) == null) {
                pinnedMessages.remove(i);
            } else if (!command.channel.get().getMessageByID(pinnedMessages.get(i)).isPinned()) {
                pinnedMessages.remove(i);
            }
        }

        //remove unwanted pins
        int tries = 0;
        while (pinnedMessages.size() > 25 && tries < 25) {
            for (IMessage msg : command.channel.get().getPinnedMessages()) {
                if (pinnedMessages.get(0) == msg.getLongID()) {
                    unPin(msg, command);
                    if (!msg.isPinned()) {
                        pinnedMessages.remove(0);
                    }
                }
            }
            tries++;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //add new pin
        if (adding) {
            pinnedMessages.add(command.message.longID);
        }
    }

    private void unPin(IMessage message, CommandObject command) {
        if (message.isPinned()) {
            XRequestBuffer.request(() -> command.channel.get().unpin(message));
        }
    }

    public boolean isHostingWebsite(String word) {
        boolean hostingWebsite = false;
        if (word.toLowerCase().toLowerCase().contains("https://gfycat.com/")) hostingWebsite = true;
        return hostingWebsite;
    }
}
