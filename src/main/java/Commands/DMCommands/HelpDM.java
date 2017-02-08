package Commands.DMCommands;

import Commands.DMCommand;
import Commands.DMCommandObject;
import Main.Globals;
import Main.Utility;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class HelpDM implements DMCommand {
    @Override
    public String execute(String args, DMCommandObject command) {
        EmbedBuilder builder = new EmbedBuilder();
        ArrayList<String> list = (ArrayList) Globals.commandsDM.stream().map(c -> Globals.defaultPrefixCommand + c.names()[0]).collect(Collectors.toList());
        Collections.sort(list);
        String desc = "**> Direct Message Commands.**```"  + Utility.listFormatter(list,false) + "```\n";
        desc += Utility.getCommandInfo(new InfoDM());
        builder.withDescription(desc);
        Utility.sendEmbededMessage("",builder.build(),command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Help"};
    }

    @Override
    public String description() {
        return "Lists DM Commands.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }
}
