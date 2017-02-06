package Commands.Admin;

import Commands.Command;
import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import Objects.RoleTypeObject;
import com.sun.jna.Union;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Vaerys on 06/02/2017.
 */
public class UpdateRolePerms implements Command
{
    @Override
    public String execute(String args, CommandObject command) {
        List<IRole> parentRole = Utility.getRolesByName(command.guild,args);
        EnumSet parentPerms = command.guild.getEveryoneRole().getPermissions();
        ArrayList<String> permList = new ArrayList<>();
        String workingID = Utility.sendMessage("`Working...`",command.channel).get();
        if (parentRole.size() != 0){
            parentPerms = parentRole.get(0).getPermissions();
        }
        for (IRole r: command.guild.getRoles()){
            for (RoleTypeObject to: command.guildConfig.getCosmeticRoles()){
                if (r.getID().equals(to.getRoleID())){
                    r.changePermissions(parentPerms);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (Object p : parentPerms.toArray()){
            permList.add(p.toString());
        }
        Utility.deleteMessage(command.client.getMessageByID(workingID));
        return "> Cosmetic Roles Perms set to : " + Utility.listFormatter(permList,true);
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateRolePerms"};
    }

    @Override
    public String description() {
        return "Sets all Cosmetic roles to only give certain perms.";
    }

    @Override
    public String usage() {
        return "[Parent Role Name]";
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
        return new Permissions[]{Permissions.ADMINISTRATOR};
    }

    @Override
    public boolean requiresArgs() {
        return false;
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
