package Handlers;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Globals;
import Main.TagSystem;
import Main.Utility;
import Objects.CCommandObject;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.regex.Pattern;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class CCHandler {

    private String command;
    private String args;
    private IMessage message;
    private IGuild guild;
    private IUser author;
    private IChannel channel;
    private String guildID;
    private CommandObject commandObject;
    CustomCommands customCommands;
    GuildConfig guildConfig;

    private final static Logger logger = LoggerFactory.getLogger(CCHandler.class);

    public CCHandler(String command, String args, CommandObject commandObject) {
        this.message = commandObject.message;
        this.command = command;
        this.args = args;
        this.commandObject = commandObject;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        customCommands = Globals.getGuildContent(guildID).getCustomCommands();
        guildConfig = Globals.getGuildContent(guildID).getGuildConfig();
        handleCommand();
    }

    private void handleCommand() {
        String response;
        String prefixEmbedImage = "#embedImage#{";
        String tagDeleteMessage = "#delCall#";
        for (CCommandObject cc : customCommands.getCommandList()) {
            if (command.equalsIgnoreCase(guildConfig.getPrefixCC() + cc.getName())) {

                //command logging
                logger.debug(Utility.loggingFormatter("CUSTOM_COMMAND",command,args,commandObject));

                if (Utility.canBypass(author, guild)) ;
                else if (cc.isShitPost() && guildConfig.shitPostFiltering) {
                    if (guildConfig.getChannelTypeID(Command.CHANNEL_SHITPOST) != null) {
                        if (!channel.getID().equals(guildConfig.getChannelTypeID(Command.CHANNEL_SHITPOST))) {
                            IChannel shitpost = Globals.getClient().getChannelByID(guildConfig.getChannelTypeID(Command.CHANNEL_SHITPOST));
                            Utility.sendMessage("> Command must be performed in " + shitpost.mention(), channel);
                            return;
                        }
                    }
                }
                response = cc.getContents(true);
                int argsCount = StringUtils.countMatches(response, "#args#");
                if (argsCount != 0) {
                    if (args.length() * argsCount > Globals.argsMax) {
                        Utility.sendMessage("> Args to large for this command. Max args size : " + Globals.argsMax, channel);
                        return;
                    }
                }
                response = TagSystem.tagSystem(response, message, args);
                response = response.replace("#DELCALL#", "#delCall#");
                response = response.replace("#EMBEDIMAGE#", "#embedImage#");
                if (customCommands.checkblackList(response) != null) {
                    Utility.sendMessage(customCommands.checkblackList(response), channel);
                    return;
                }
                if (response.contains(tagDeleteMessage)) {
                    response = response.replace(tagDeleteMessage, "");
                    Utility.deleteMessage(message);
                }
                if (response.contains("#embedImage#{")) {
                    String imageURL = TagSystem.tagEmbedImage(response, prefixEmbedImage);
                    if (imageURL != null || !imageURL.isEmpty()) {
                        response = response.replaceFirst(Pattern.quote(prefixEmbedImage + imageURL + "}"), "");
                        response = TagSystem.tagToCaps(response);
                        Utility.sendFileURL(response, imageURL, channel,true);
                        return;
                    }
                }
                Utility.sendMessage(response, channel);
            }
        }
    }
}
