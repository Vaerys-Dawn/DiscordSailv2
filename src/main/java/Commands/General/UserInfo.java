package Commands.General;

import Interfaces.Command;
import Commands.CommandObject;
import Main.Utility;
import Objects.XEmbedBuilder;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class UserInfo implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        IUser user = command.author;
        if (command.message.getMentions().size() > 0){
            user = command.message.getMentions().get(0);
        }
        for (UserTypeObject u : command.guildUsers.getUsers()) {
            if (u.getID().equals(user.getID())) {

                XEmbedBuilder builder = new XEmbedBuilder();
                List<IRole> roles = user.getRolesForGuild(command.guild);
                ArrayList<String> roleNames = new ArrayList<>();

                //sets title to user Display Name;
                builder.withTitle(user.getDisplayName(command.guild));

                //sets thumbnail to user Avatar.
                builder.withThumbnail(user.getAvatarURL());

                //gets the age of the account.
                long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - user.getCreationDate().atZone(ZoneOffset.UTC).toEpochSecond();


                //sets sidebar colour
                builder.withColor(Utility.getUsersColour(user,command.guild));

                //collect role names;
                roleNames.addAll(roles.stream().filter(role -> !role.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));

                //builds desc
                builder.withDesc("Account Created: " + Utility.formatTimeDifference(difference) +
                        "\nGender: " + u.getGender() +
                        "\nRoles : " + Utility.listFormatter(roleNames, true) +
                        "\n\n*" + u.getQuote() + "*");

                // TODO: 27/02/2017 when xp system is implemented put xp and rank on the user card.

                //adds ID
                builder.withFooterText("User ID: " + u.getID());

                //sends Message
                Utility.sendEmbedMessage("", builder, command.channel);
                return null;
            }
        }
        return "> Unfortunately that user doesn't seem to have a user info right now.";
    }

    @Override
    public String[] names() {
        return new String[]{"UserInfo","Me"};
    }

    @Override
    public String description() {
        return "Lets you see some information about yourself or another user.";
    }

    @Override
    public String usage() {
        return "(@user)";
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
