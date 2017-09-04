package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.pogos.CustomCommands;
import com.github.vaerys.pogos.GuildConfig;
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
    GuildConfig guildconfig;

    private final static Logger logger = LoggerFactory.getLogger(CCHandler.class);

    public CCHandler(String args, CommandObject commandObject) {
        SplitFirstObject commandName = new SplitFirstObject(args);
        this.message = commandObject.message.get();
        this.command = commandName.getFirstWord();
        this.args = commandName.getRest();
        if (this.args == null) this.args = "";
        this.commandObject = commandObject;
        this.guild = commandObject.guild.get();
        this.author = commandObject.user.get();
        this.channel = commandObject.channel.get();
        guildconfig = commandObject.guild.config;
        customCommands = commandObject.guild.customCommands;
        handleCommand();
    }

    private void handleCommand() {
        ProfileObject object = commandObject.guild.users.getUserByID(author.getStringID());
        if (object != null && object.getSettings().contains(UserSetting.DENY_USE_CCS)) {
            Utility.sendMessage("> Nothing interesting happens. `(ERROR: 401)`", channel);
            return;
        }
        String response;
        String prefixEmbedImage = "<embedImage>{";
        String tagDeleteMessage = "<delCall>";
        for (CCommandObject cc : customCommands.getCommandList()) {
            if (command.equalsIgnoreCase(guildconfig.getPrefixCC() + cc.getName())) {

                //command logging
                logger.debug(Utility.loggingFormatter(commandObject, "CUSTOM_COMMAND"));

                if (Utility.canBypass(author, guild)) ;
                else if (cc.isShitPost() && guildconfig.shitPostFiltering) {
                    ArrayList<String> channelMentions = new ArrayList<>();
                    boolean isShitpost = false;
                    if (guildconfig.getChannelIDsByType(Command.CHANNEL_SHITPOST) != null) {
                        for (ChannelSetting c : commandObject.guild.channelSettings) {
                            if (c.type().equals(Command.CHANNEL_SHITPOST)) {
                                for (String id : c.getIDs(commandObject.guild.config)) {
                                    if (commandObject.channel.stringID.equals(id)) {
                                        isShitpost = true;
                                    }
                                    for (IChannel channel : commandObject.guild.get().getChannels()) {
                                        if (id.equals(channel.getStringID())) {
                                            EnumSet<Permissions> userPerms = channel.getModifiedPermissions(commandObject.user.get());
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
                response = cc.getContents(true);
                int argsCount = StringUtils.countMatches(response, "<args>");
                if (argsCount != 0) {
                    if (args.length() * argsCount > Globals.argsMax) {
                        Utility.sendMessage("> Args to large for this command. Max args size : " + Globals.argsMax, channel);
                        return;
                    }
                }
                response = TagHandler.tagSystem(response, commandObject, args);
                response = TagHandler.tagMentionRemover(response);
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
                    String imageURL = TagHandler.tagEmbedImage(response, prefixEmbedImage);
                    if (imageURL != null && !imageURL.isEmpty()) {
                        if (commandObject.channel.get().getModifiedPermissions(author).contains(Permissions.EMBED_LINKS)) {
                            response = response.replaceFirst(Pattern.quote(prefixEmbedImage + imageURL + "}"), "");
                            response = TagHandler.tagToCaps(response);
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
