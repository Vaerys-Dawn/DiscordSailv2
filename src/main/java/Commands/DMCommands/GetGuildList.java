package Commands.DMCommands;

import Interfaces.DMCommand;
import Commands.DMCommandObject;
import Main.Utility;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Created by Vaerys on 17/02/2017.
 */
public class GetGuildList implements DMCommand {
    @Override
    public String execute(String args, DMCommandObject command) {
        ArrayList<String> guilds = new ArrayList<>();
        for (IGuild g: command.client.getGuilds()){
            guilds.add(g.getName() +": " + g.getID());
        }
        XEmbedBuilder builder = new XEmbedBuilder();
        Utility.listFormatterEmbed("List Of Guilds", builder, guilds, false);
        Utility.sendDMEmbed("",builder,command.authorID);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"GetGuildList"};
    }

    @Override
    public String description() {
        return "Gives a list of all the Guilds the bot is connected to.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }
}