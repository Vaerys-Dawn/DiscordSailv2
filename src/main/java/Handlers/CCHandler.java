package Handlers;

import Commands.CommandObject;
import Interfaces.ChannelSetting;
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
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.EnumSet;
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
    private CommandObject commandObject;
    CustomCommands customCommands;
    GuildConfig guildConfig;

    private final static Logger logger = LoggerFactory.getLogger(CCHandler.class);

    public CCHandler(String command, String args, CommandObject commandObject) {
        this.message = commandObject.message;
        this.command = command;
        this.args = args;
        this.commandObject = commandObject;
        this.guild = commandObject.guild;
        this.author = commandObject.author;
        this.channel = commandObject.channel;
        guildConfig = commandObject.guildConfig;
        customCommands = commandObject.customCommands;
        handleCommand();
    }

    private void handleCommand() {
        String response;
        String prefixEmbedImage = "<embedImage>{";
        String tagDeleteMessage = "<delCall>";
        for (CCommandObject cc : customCommands.getCommandList()) {
            if (command.equalsIgnoreCase(guildConfig.getPrefixCC() + cc.getName())) {

                //command logging
                logger.debug(Utility.loggingFormatter("CUSTOM_COMMAND", command, args, commandObject));

                if (Utility.canBypass(author, guild)) ;
                else if (cc.isShitPost() && guildConfig.shitPostFiltering) {
                    ArrayList<String> channelMentions = new ArrayList<>();
                    boolean isShitpost = false;
                    if (guildConfig.getChannelIDsByType(Command.CHANNEL_SHITPOST) != null) {
                        for (ChannelSetting c : commandObject.channelSettings) {
                            if (c.type().equals(Command.CHANNEL_SHITPOST)) {
                                for (String id : c.getIDs(commandObject.guildConfig)) {
                                    if (commandObject.channelSID.equals(id)) {
                                        isShitpost = true;
                                    }
                                    for (IChannel channel : commandObject.guild.getChannels()) {
                                        if (id.equals(channel.getStringID())) {
                                            EnumSet<Permissions> userPerms = channel.getModifiedPermissions(commandObject.author);
                                            if (userPerms.contains(Permissions.SEND_MESSAGES) && userPerms.contains(Permissions.READ_MESSAGES)) {
                                                channelMentions.add(channel.mention());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!isShitpost) {
                            if (channelMentions.size() == 0) {
                                Utility.sendMessage("> You do not have access to any channels that you are able to run this command in.", channel);
                                return;
                            } else if (channelMentions.size() > 1) {
                                Utility.sendMessage("> Command must be performed in any of the following channels: \n" + Utility.listFormatter(channelMentions, true), channel);
                                return;
                            } else if (channelMentions.size() == 1) {
                                Utility.sendMessage("> Command must be performed in: " + channelMentions.get(0), channel);
                                return;
                            }
                        }
                    }

                }
//                if (!channel.getTypingStatus()) {
//                    channel.toggleTypingStatus();
//                }
                response = cc.getContents(true);
                int argsCount = StringUtils.countMatches(response, "<args>");
                if (argsCount != 0) {
                    if (args.length() * argsCount > Globals.argsMax) {
                        Utility.sendMessage("> Args to large for this command. Max args size : " + Globals.argsMax, channel);
                        return;
                    }
                }
                response = TagSystem.tagSystem(response, message, args);
                response = response.replace("<DELCALL>", "<delCall>");
                response = response.replace("<EMBEDIMAGE>", "<embedImage>");
                if (customCommands.checkblackList(response) != null) {
                    Utility.sendMessage(customCommands.checkblackList(response), channel);
                    return;
                }
                if (response.contains(tagDeleteMessage)) {
                    response = response.replace(tagDeleteMessage, "");
                    Utility.deleteMessage(message);
                }
                if (response.contains("<embedImage>{")) {
                    String imageURL = TagSystem.tagEmbedImage(response, prefixEmbedImage);
                    if (imageURL != null || !imageURL.isEmpty()) {
                        if (commandObject.channel.getModifiedPermissions(author).contains(Permissions.EMBED_LINKS)) {
                            response = response.replaceFirst(Pattern.quote(prefixEmbedImage + imageURL + "}"), "");
                            response = TagSystem.tagToCaps(response);
                            Utility.sendFileURL(response, imageURL, channel, true);
                        } else {
                            response = response.replaceFirst(Pattern.quote(prefixEmbedImage + imageURL + "}"), "<" + imageURL + ">");
                            Utility.sendMessage(response, channel);
                        }
                        return;
                    }
                }
                Utility.sendMessage(response, channel);
            }
        }
    }
}
