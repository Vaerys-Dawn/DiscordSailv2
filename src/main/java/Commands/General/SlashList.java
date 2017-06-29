package Commands.General;

import Commands.CommandObject;
import Interfaces.Command;
import Interfaces.SlashCommand;
import Main.Globals;
import Main.Utility;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class SlashList implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> list = new ArrayList<>();
        for (SlashCommand s: Globals.getSlashCommands()){
            list.add(s.call());
        }
        Collections.sort(list);
        XEmbedBuilder builder = new XEmbedBuilder();
        builder.withColor(Utility.getUsersColour(command.client.getOurUser(),command.guild));
        Utility.listFormatterEmbed("> Available Slash Commands.",builder,list,false);
        Utility.sendEmbedMessage("",builder,command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"SlashList","SlashCommands","Slash"};
    }

    @Override
    public String description() {
        return "List Available Slash Commands.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return null;
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
