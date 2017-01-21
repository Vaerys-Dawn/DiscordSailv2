package Handlers;

import Main.Constants;
import Main.Globals;
import Main.TagSystem;
import Main.Utility;
import Objects.CCommandObject;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import org.apache.commons.lang3.StringUtils;
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

    CustomCommands customCommands;
    GuildConfig guildConfig;

    public CCHandler(String command, String args, IMessage message) {
        this.command = command;
        this.args = args;
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        customCommands = Globals.getGuildContent(guildID).getCustomCommands();
        guildConfig = Globals.getGuildContent(guildID).getGuildConfig();
        handleCommand();
    }

    private void handleCommand() {
        if (customCommands.checkblackList(args) != null) {
            Utility.sendMessage(customCommands.checkblackList(args), channel);
            return;
        }
        String response;
        String prefixEmbedImage = "#embedImage#{";
        for (CCommandObject cc : customCommands.getCommandList()) {
            if (command.equalsIgnoreCase(guildConfig.getPrefixCC() + cc.getName())) {
                if (Utility.canBypass(author, guild)) ;
                else if (cc.isShitPost() && guildConfig.doShitPostFiltering()) {
                    if (guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST) != null) {
                        if (!channel.getID().equals(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST))) {
                            IChannel shitpost = Globals.getClient().getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST));
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
                if (response.contains("#embedImage#{")){
                    String imageURL = TagSystem.tagEmbedImage(response,prefixEmbedImage);
                    if (imageURL != null ||!imageURL.isEmpty()){
                        response = response.replaceFirst(Pattern.quote(prefixEmbedImage + imageURL + "}"), "");
                        Utility.sendFile(response,imageURL,channel);
                        return;
                    }
                }
                Utility.sendMessage(response, channel);
            }
        }
    }
}
