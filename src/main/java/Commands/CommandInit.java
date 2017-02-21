package Commands;

import Commands.Admin.*;
import Commands.CC.*;
import Commands.Characters.DelChar;
import Commands.Characters.ListChars;
import Commands.Characters.SelectChar;
import Commands.Characters.UpdateChar;
import Commands.Competition.EnterComp;
import Commands.Competition.EnterVote;
import Commands.Competition.FinalTally;
import Commands.Competition.GetCompEntries;
import Commands.Creator.Shutdown;
import Commands.Creator.Sudo;
import Commands.Creator.UpdateAvatar;
import Commands.DMCommands.*;
import Commands.General.GetAvatar;
import Commands.General.Hello;
import Commands.General.RemindMe;
import Commands.General.Test;
import Commands.Help.GetGuildInfo;
import Commands.Help.*;
import Commands.RoleSelect.CosmeticRoles;
import Commands.RoleSelect.ListModifs;
import Commands.RoleSelect.ListRoles;
import Commands.RoleSelect.ModifierRoles;
import Commands.Servers.*;

import java.util.ArrayList;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class CommandInit {

    public static ArrayList<Command> get() {
        ArrayList<Command> commands = new ArrayList<>();

        //Creator Commands
        commands.add(new Shutdown());
        commands.add(new Sudo());
        commands.add(new UpdateAvatar());

        //Admin commands
        commands.add(new ChannelHere());
        commands.add(new MaxMessages());
        commands.add(new FinalTally());
        commands.add(new GetCompEntries());
        commands.add(new SetAdminRole());
        commands.add(new SetMutedRole());
        commands.add(new SetTrustedRoles());
        commands.add(new Toggle());
        commands.add(new UpdateInfo());
        commands.add(new UpdateRolePerms());
        //General commands
        commands.add(new GetAvatar());
        commands.add(new Hello());
        commands.add(new RemindMe());
        commands.add(new Test());
        //Help commands
        commands.add(new GetGuildInfo());
        commands.add(new Help());
        commands.add(new HelpTags());
        commands.add(new Info());
        commands.add(new Report());
        commands.add(new SilentReport());
        //RoleSelect commands
        commands.add(new CosmeticRoles());
        commands.add(new ModifierRoles());
        commands.add(new ListRoles());
        commands.add(new ListModifs());
        //Server commands
        commands.add(new AddServer());
        commands.add(new DelServer());
        commands.add(new EditServerDesc());
        commands.add(new EditServerIP());
        commands.add(new EditServerName());
        commands.add(new ListServers());
        commands.add(new Server());
        //Character Commands
        commands.add(new DelChar());
        commands.add(new ListChars());
        commands.add(new SelectChar());
        commands.add(new UpdateChar());
        //CC commands
        commands.add(new DelCC());
        commands.add(new EditCC());
        commands.add(new GetCCData());
        commands.add(new InfoCC());
        commands.add(new ListCCs());
        commands.add(new NewCC());
        commands.add(new SearchCCs());
        commands.add(new TransferCC());
        //Competition commands
        commands.add(new EnterComp());
        commands.add(new EnterVote());

        return commands;
    }

    public static ArrayList<DMCommand> getDM() {
        ArrayList<DMCommand> commands = new ArrayList<>();

        //DM commands
        commands.add(new BlockUser());
        commands.add(new GetGuildList());
        commands.add(new Commands.DMCommands.GetGuildInfo());
        commands.add(new Respond());
        commands.add(new HelpDM());
        commands.add(new InfoDM());

        return commands;
    }
}
