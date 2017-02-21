package Commands.Admin;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class MaxMessages implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            int max = Integer.parseInt(args);
            if (max <= 0){
                return "> Rate Limit must be larger than 0";
            }else if (max > 10){
                return "> That would be stopped by Discord's Rate Limit.";
            }else{
                command.guildConfig.setRateLimit(max);
                return "> Guild Rate limit set to **" + max + "** messages per user every 10 seconds.";
            }
        }catch (NumberFormatException e){
            return "> You need to specify a number.";
        }
}

    @Override
    public String[] names() {
        return new String[]{"SetRateLimit"};
    }

    @Override
    public String description() {
        return "Sets the rate limit for your Guild. (Maximum Messages per 10 seconds per person.)";
    }

    @Override
    public String usage() {
        return "[Max messages per 10 sec]";
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
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return true;
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
