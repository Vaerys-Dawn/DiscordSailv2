package Commands.General;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.ChannelSettingObject;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject obe = new SplitFirstObject(args);
        if (obe.getFirstWord().equalsIgnoreCase("Mention")) {
            if (obe.getRest() != null) {
                IUser user = null;
                SplitFirstObject mentionee = new SplitFirstObject(obe.getRest());
                String toTest = Matcher.quoteReplacement(mentionee.getFirstWord()).replace("_", "[_| ]");
                for (IUser u : command.guild.getUsers()) {
                    try {
                        if ((u.getName() + "#" + u.getDiscriminator()).matches(toTest)) {
                            user = u;
                        }
                    }catch (PatternSyntaxException e){
                        //do nothing.
                    }
                }
                try {
                    long uID = Long.parseLong(mentionee.getFirstWord());
                    user = command.client.getUserByID(uID);
                } catch (NumberFormatException e) {
                    if (command.message.getMentions().size() > 0) {
                        user = command.message.getMentions().get(0);
                    }
                }
                if (user != null) {
                    return "> User was found.";
                } else {
                    return "> user could not be found.";
                }
            }
        }
        try {
            long msgId = Long.parseUnsignedLong(args);
            IMessage message = command.client.getMessageByID(msgId);
            if (message != null) {
                System.out.println("Embeds: " + message.getEmbeds().size());
                System.out.println("Attachments: " + message.getAttachments().size());
                System.out.println("Channels: " + message.getChannelMentions().size());
                System.out.println("Mentions: " + message.getMentions().size());
                System.out.println("RoleMentions: " + message.getRoleMentions().size());
                System.out.println("Reactions: " + message.getReactions().size());
                System.out.println("Charactes: " + message.getContent().length());
                System.out.println("Words: " + message.getContent().split(" ").length);
                System.out.println("Content: " + message.getContent());
                System.out.println("Formatted Content: " + message.getFormattedContent());
//            message.getChannel().pin(message);
//            command.client.getDispatcher().dispatch(new MessagePinEvent(message));
//            message.delete();
                return "> you sent Erin some data :P";
            } else {
                return "> Nothing interesting happens.";
            }
        } catch (NumberFormatException e) {
            String channels = "Channel Settings: \n";
            for (ChannelSettingObject c : command.guildConfig.getChannelSettings()) {
                channels += c.getType() + ": " + Utility.listFormatter(c.mentionChannelIDs(), true) + "\n";
            }
            return channels;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description() {
        return "Tests Things.";
    }

    @Override
    public String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
