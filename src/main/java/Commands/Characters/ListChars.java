package Commands.Characters;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.CharacterObject;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListChars implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        String id = command.authorSID;
        String title = "> Here are all of your characters.";
        if (command.message.getMentions().size() > 0){
            IUser mentioned = command.message.getMentions().get(0);
            id = mentioned.getStringID();
            title = "> Here are all of **@" + mentioned.getName() + "#" + mentioned.getDiscriminator() + "'s** Characters.";
        }
        ArrayList<String> list = new ArrayList<>();
        for (CharacterObject c: command.characters.getCharacters()){
            if (c.getUserID().equals(id)){
                list.add(c.getName());
            }
        }
        Utility.listFormatterEmbed(title,builder,list,true);
        builder.appendField(spacer,Utility.getCommandInfo(new SelectChar(),command),false);
        builder.withColor(Utility.getUsersColour(command.client.getOurUser(), command.guild));
        Utility.sendEmbedMessage("",builder,command.channel);
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
        return "(@User)";
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
