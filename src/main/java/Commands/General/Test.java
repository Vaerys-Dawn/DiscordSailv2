package Commands.General;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.ChannelSettingObject;
import Objects.ChannelTypeObject;
import sx.blah.discord.api.internal.json.objects.WebhookObject;
import sx.blah.discord.handle.impl.obj.Webhook;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        String channels = "Channel Settings: \n";
        for (ChannelSettingObject c: command.guildConfig.getChannelSettings()){
            channels += c.getType() + ": "  + Utility.listFormatter(c.mentionChannelIDs(),true) + "\n";
        }
        return channels;
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
        return new Permissions[0];
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
