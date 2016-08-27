package Handlers;

import Main.Constants;
import Main.Utility;
import POGOs.CustomCommands;
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
    FileHandler handler = new FileHandler();

    public CCHandler(String command, String args, IMessage message) {
        this.command = command;
        this.args = args;
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        initFiles();
        handleCommand();
    }

    private void initFiles() {
        customCommands = (CustomCommands) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_CUSTOM),CustomCommands.class);
    }

    private void handleCommand() {
        Utility.sendMessage("test",channel);
    }
}
