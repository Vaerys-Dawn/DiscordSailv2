package Handlers;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class ArtHandler {

    public ArtHandler(CommandObject command) {
        if (command.author.isBot()) {
            return;
        }
        if (command.guildConfig.getChannelIDsByType(Command.CHANNEL_ART) == null) {
            return;
        }
        if (!command.guildConfig.getChannelIDsByType(Command.CHANNEL_ART).get(0).equals(command.channelSID)) {
            return;
        }
        if (command.message.getAttachments().size() != 0) {
            if (Utility.isImageLink(command.message.getAttachments().get(0).getUrl())) {
                command.message.getChannel().pin(command.message);
                checkList(command, true);
                return;
            }
        } else {
            for (String nl : command.message.getContent().split("/n")) {
                for (String s : nl.split(" ")) {
                    if (Utility.isImageLink(s) || isHostingWebsite(s)) {
                        try {
                            command.message.getChannel().pin(command.message);
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
        ArrayList<Long> pinnedMessages = command.channelData.getPinnedMessages();
        for (int i = 0; i < pinnedMessages.size(); i++) {
            if (command.channel.getMessageByID(pinnedMessages.get(i)) == null) {
                pinnedMessages.remove(i);
            } else if (!command.channel.getMessageByID(pinnedMessages.get(i)).isPinned()) {
                pinnedMessages.remove(i);
            }
        }
        while (pinnedMessages.size() >= 26) {
            for (IMessage msg : command.channel.getPinnedMessages()) {
                if (pinnedMessages.get(0) == msg.getLongID()) {
                    RequestBuffer.request(() -> {
                        command.channel.unpin(msg);
                        pinnedMessages.remove(0);
                    });
                }
            }
        }
        if (adding) {
            pinnedMessages.add(command.messageID);
        }
    }

    public boolean isHostingWebsite(String word) {
        boolean hostingWebsite = false;
        if (word.toLowerCase().toLowerCase().contains("https://gfycat.com/")) hostingWebsite = true;
        return hostingWebsite;
    }
}
