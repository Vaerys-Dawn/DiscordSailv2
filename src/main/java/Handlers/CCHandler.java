package Handlers;

import Main.Constants;
import Main.Utility;
import Objects.CCommandObject;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
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
        guildConfig = (GuildConfig) Utility.initFile(guildID,Constants.FILE_GUILD_CONFIG,GuildConfig.class);
        handleCommand();
        Utility.flushFile(guildID,Constants.FILE_CUSTOM,customCommands,customCommands.isProperlyInit());
        Utility.flushFile(guildID,Constants.FILE_GUILD_CONFIG,guildConfig,guildConfig.isProperlyInit());
    }

    private void initFiles() {

    }

    private void handleCommand() {
        String isBlacklisted = customCommands.checkblackList(args);
        String response;
        if (isBlacklisted != null) {
            Utility.sendMessage(isBlacklisted, channel);
            return;
        }
        for (CCommandObject cc : customCommands.getCommandList()) {
            if (cc.isShitPost()){
                if (!channel.getID().equals(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST))){
                    return;
                }
            }
            if (command.equalsIgnoreCase(Constants.PREFIX_CC + cc.getName())) {
                response = Utility.tagSystem(cc.getContents(), message, args);
                Utility.sendMessage(response, channel);
            }
        }
    }
}
