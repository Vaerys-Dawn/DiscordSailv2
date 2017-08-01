package Commands;

import Commands.Admin.*;
import Commands.CC.*;
import Commands.Characters.*;
import Commands.Competition.*;
import Commands.Creator.*;
import Commands.Creator.Shutdown;
import Commands.DMCommands.*;
import Commands.General.*;
import Commands.Groups.ClearGroupUp;
import Commands.Groups.GroupUp;
import Commands.Help.*;
import Commands.Help.GetGuildInfo;
import Commands.Pixels.*;
import Commands.RoleSelect.CosmeticRoles;
import Commands.RoleSelect.ListModifs;
import Commands.RoleSelect.ListRoles;
import Commands.RoleSelect.ModifierRoles;
import Commands.Servers.*;
import Commands.Slash.*;
import Interfaces.Command;
import Interfaces.DMCommand;
import Interfaces.SlashCommand;

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
        commands.add(new GetGlobalStats());
        commands.add(new ResetPlayingStatus());
        commands.add(new ToggleTypingStatus());
        commands.add(new SetPlayingStatus());
        commands.add(new DailyMsg());
        //Admin commands
        commands.add(new ChannelHere());
        commands.add(new ChannelStats());
        commands.add(new MaxMessages());
        commands.add(new Module());
        commands.add(new Mute());
        commands.add(new EditInfoFiles());
        commands.add(new SetAdminRole());
        commands.add(new SetMutedRole());
        commands.add(new SetTrustedRoles());
        commands.add(new Toggle());
        commands.add(new UpdateInfo());
        commands.add(new UpdateRolePerms());
        commands.add(new UserSettings());
        commands.add(new SetJoinMessage());
        commands.add(new PruneEmptyProfiles());
        commands.add(new DenyXpPrefix());
        //General commands
        commands.add(new GetAvatar());
        commands.add(new Hello());
        commands.add(new RemindMe());
        commands.add(new SetGender());
        commands.add(new SetQuote());
        commands.add(new Test());
        commands.add(new UserInfo());
        commands.add(new SlashList());
        commands.add(new ClearReminder());
        commands.add(new EditLinks());
        commands.add(new Ping());
        //Help commands
        commands.add(new GetGuildInfo());
        commands.add(new Help());
        commands.add(new HelpTags());
        commands.add(new Info());
        commands.add(new Report());
        commands.add(new SilentReport());
        commands.add(new StartUpGuide());
        commands.add(new BotInfo());
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
        commands.add(new CharInfo());
        commands.add(new DelChar());
        commands.add(new EditChar());
        commands.add(new ListChars());
        commands.add(new SelectChar());
        commands.add(new UpdateChar());
        commands.add(new SetBioRolePrefix());
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
        commands.add(new FinalTally());
        commands.add(new GetCompEntries());
        commands.add(new EnterComp());
        commands.add(new EnterVote());
        commands.add(new PurgeComp());
        //Groups commands
        commands.add(new GroupUp());
        commands.add(new ClearGroupUp());
        //Pixels
        commands.add(new Pixels());
        commands.add(new PixelHelp());
        commands.add(new DefaultLevelMode());
        commands.add(new PixelSettings());
        commands.add(new LevelUpMessage());
        commands.add(new ManagePixelRoles());
        commands.add(new TransferLevels());
        commands.add(new SetXp());
        commands.add(new SetLevel());
        commands.add(new SetPixelModifier());
        commands.add(new CheckPixelRoles());
        commands.add(new TopUserForRole());
        commands.add(new Rank());
        commands.add(new TopTen());
        commands.add(new FixLevelErrors());
        commands.add(new SetLevelUpReaction());

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
        commands.add(new ReminderDM());
        commands.add(new ClearReminderDM());
        commands.add(new ShutdownDM());
        commands.add(new QuickRespond());
        commands.add(new TestDM());
        return commands;
    }

    public static ArrayList<SlashCommand> getSlashCommands() {
        ArrayList<SlashCommand> commands = new ArrayList<>();

        commands.add(new Disapproval());
        commands.add(new Lenny());
        commands.add(new TableFlip());
        commands.add(new UnFlip());
        commands.add(new Shrug());
        commands.add(new Gib());
        commands.add(new Fite());
        commands.add(new DealWithIt());

        return commands;
    }
}
