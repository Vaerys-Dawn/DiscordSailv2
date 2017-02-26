package Commands.Characters;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import Objects.CharacterObject;
import Objects.RoleTypeObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class CharInfo implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (CharacterObject object: command.characters.getCharacters()){
            if (object.getName().equalsIgnoreCase(args)){
                EmbedBuilder builder = new EmbedBuilder();
                builder.withTitle(object.getNickname());

                ArrayList<IRole> roles = new ArrayList<>();
                ArrayList<String> roleNames = new ArrayList<>();
                for (RoleTypeObject role : object.getRoles()){
                    roles.add(command.client.getRoleByID(role.getRoleID()));
                    roleNames.add(command.client.getRoleByID(role.getRoleID()).getName());
                }
                if (roles.size() > 0) {
                    builder.withColor(Utility.getUsersColour(roles, command.guild));
                }else {
                    builder.withColor(Utility.getUsersColour(command.client.getOurUser(),command.guild));
                }

                String description = "";
                description += "Age: " + object.getAge();
                description += "\nGender: " + object.getGender();
                description += "\n" + Utility.listFormatter(roleNames,true);
                description += "\nBio: " + object.getShortBio();
                if (object.getLongBioURL() != null && !object.getLongBioURL().isEmpty()) {
                    description += "\n\n[Long Description Link...](" + object.getLongBioURL() + ")";
                }
                builder.withDesc(description);
                if (object.getAvatarURL() != null && !object.getAvatarURL().isEmpty()) {
                    builder.withThumbnail(object.getAvatarURL());
                }
                Utility.sendEmbededMessage("",builder.build(),command.channel);
                return null;
            }
        }
        return "> Character with that name not found.";
    }

    @Override
    public String[] names() {
        return new String[]{"CharInfo","InfoChar"};
    }

    @Override
    public String description() {
        return "Gives Information about a certain character.";
    }

    @Override
    public String usage() {
        return "[Character name]";
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
        return true;
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
