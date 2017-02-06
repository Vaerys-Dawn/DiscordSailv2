package Commands.Characters;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Utility;
import Objects.CharacterObject;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListChars implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        EmbedBuilder builder = new EmbedBuilder();
        String title = "> Here are all of your characters.";
        ArrayList<String> list = command.characters.getCharacters().stream().filter(c -> c.getUserID().equals(command.authorID)).map(CharacterObject::getName).collect(Collectors.toCollection(ArrayList::new));
        Utility.listFormatterEmbed(title,builder,list,true);
        builder.appendField(spacer,Utility.getCommandInfo(new SelectChar(),command),false);
        builder.withColor(Utility.getUsersColour(command.client.getOurUser(), command.guild));
        Utility.sendEmbededMessage("",builder.build(),command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListChars","Chars","CharList"};
    }

    @Override
    public String description() {
        return "Shows you all of your characters.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CHARACTER;
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
