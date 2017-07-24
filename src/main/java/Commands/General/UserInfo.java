package Commands.General;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Main.Utility;
import Objects.UserTypeObject;
import Objects.XEmbedBuilder;
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
        if (command.message.getMentions().size() > 0) {
            user = command.message.getMentions().get(0);
        }
        for (UserTypeObject u : command.guildUsers.getUsers()) {
            if (u.getID().equals(user.getStringID())) {

                XEmbedBuilder builder = new XEmbedBuilder();
                List<IRole> roles = user.getRolesForGuild(command.guild);
                ArrayList<String> roleNames = new ArrayList<>();
                ArrayList<String> links = new ArrayList<>();

                //If user is a bot it will display the image below as the user avatar icon.
                if (user.isBot()) {
                    builder.withAuthorIcon("http://i.imgur.com/aRJpAP4.png");
                }
                //sets title to user Display Name;
                builder.withAuthorName(user.getDisplayName(command.guild));


                //sets thumbnail to user Avatar.
                builder.withThumbnail(user.getAvatarURL());

                //gets the age of the account.
                long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - user.getCreationDate().atZone(ZoneOffset.UTC).toEpochSecond();

                //sets sidebar colour
                builder.withColor(Utility.getUsersColour(user, command.guild));

                //collect role names;
                roleNames.addAll(roles.stream().filter(role -> !role.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));

                if (u.getLinks() != null && u.getLinks().size() > 0) {
                    links.addAll(u.getLinks().stream().map(link -> "[" + link.getName() + "](" + link.getLink() + ")").collect(Collectors.toList()));
                }

                //builds desc

                command.setAuthor(user);

                String desc = "";
                desc += "**Account Created: **" + Utility.formatTimeDifference(difference);
                desc += "\n**Gender: **" + u.getGender();

                boolean showLevel = true;
                boolean showCC = command.guildConfig.moduleCC;

                if (!command.guildConfig.modulePixels || u.getXP() == 0) {
                    showLevel = false;
                }

                if (showCC && !showLevel) {
                    desc += "\n**Custom Commands: **" + command.customCommands.getUserCommandCount(command);
                } else if (showLevel && !showCC) {
                    desc += "\n**Level: **" + XpHandler.xpToLevel(u.getXP());
                } else if (showLevel && showCC){
                    desc += "\n**Custom Commands: **" + command.customCommands.getUserCommandCount(command) +
                            indent + indent + indent + "**Level: **" + XpHandler.xpToLevel(u.getXP());
                }
                
                desc += "\n**Roles: **" + Utility.listFormatter(roleNames, true);
                desc += "\n\n*" + u.getQuote() + "*";
                desc += "\n" + Utility.listFormatter(links, true);

                builder.withDesc(desc);
                builder.withFooterText("User ID: " + u.getID());

                //sends Message
                Utility.sendEmbedMessage("", builder, command.channel);
                return null;
            }
        }
        if (user.isBot()) {
            command.guildUsers.addUser(user.getStringID());
            return execute(args, command);
        }
        return "> Unfortunately that user doesn't seem to have a user info right now.";
    }

    @Override
    public String[] names() {
        return new String[]{"UserInfo", "Me"};
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
