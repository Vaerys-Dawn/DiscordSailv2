package Commands.Admin;

import Commands.CommandObject;
import Interfaces.ChannelSetting;
import Interfaces.Command;
import Main.Globals;
import Main.Utility;
import Objects.ChannelSettingObject;
import Objects.UserTypeObject;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class ChannelStats implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();

        ArrayList<String> channelTypes = new ArrayList<>();
        ArrayList<String> channelSettings = new ArrayList<>();

        for (ChannelSettingObject c : command.guildConfig.getChannelSettings()) {
            if (c.getChannelIDs().contains(command.channelSID)) {
                for (ChannelSetting setting : Globals.getChannelSettings()) {
                    if (c.getType().equalsIgnoreCase(setting.type())) {
                        if (setting.isSetting()) {
                            channelSettings.add(c.getType());
                        } else {
                            channelTypes.add(c.getType());
                        }
                    }
                }
            }
        }


        builder.withTitle("Channel Stats");
        builder.withColor(Utility.getUsersColour(command.botUser,command.guild));
        if (channelSettings.size() == 0 && channelTypes.size() == 0){
            return "> I found nothing of value.";
        }
        if (channelTypes.size() != 0) {
            builder.appendField("Types:", Utility.listFormatter(channelTypes, true), false);
        }
        if (channelSettings.size() != 0) {
            builder.appendField("Settings:", Utility.listFormatter(channelSettings, true), false);
        }
        Utility.sendEmbedMessage("",builder,command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ChannelStats"};
    }

    @Override
    public String description() {
        return "Gives information about the current channel";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_CHANNELS};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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
