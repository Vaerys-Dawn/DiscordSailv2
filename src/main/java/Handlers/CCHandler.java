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
        customCommands = (CustomCommands) Utility.initFile(guildID, Constants.FILE_CUSTOM, CustomCommands.class);
        guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        handleCommand();
        Utility.flushFile(guildID, Constants.FILE_CUSTOM, customCommands, customCommands.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
    }

    private void handleCommand() {
        if (customCommands.checkblackList(args) !=null){
            Utility.sendMessage(customCommands.checkblackList(args),channel);
            return;
        }
        String response;
        for (CCommandObject cc : customCommands.getCommandList()) {
            if (command.equalsIgnoreCase(guildConfig.getPrefixCC() + cc.getName())) {
                if (cc.isShitPost() && guildConfig.doShitPostFiltering()) {
                    if (guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST) != null) {
                        if (!channel.getID().equals(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST))) {
                            IChannel shitpost = Globals.getClient().getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST));
                            Utility.sendMessage("> Command must be performed in " + shitpost.mention(),channel);
                            return;
                        }
                    }
                }
                response = cc.getContents(true);
                int argsCount = StringUtils.countMatches(response,"#args#");
                if (argsCount != 0){
                    if (args.length()*argsCount > Globals.argsMax) {
                        Utility.sendMessage("> Args to large for this command. Max args size : " + Globals.argsMax, channel);
                        return;
                    }
                }
                response = TagSystem.tagSystem(response, message, args);
                Utility.sendMessage(response, channel);
            }
        }
    }
}
