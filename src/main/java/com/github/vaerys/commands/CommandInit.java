package com.github.vaerys.commands;

import com.github.vaerys.commands.admin.*;
import com.github.vaerys.commands.cc.*;
import com.github.vaerys.commands.characters.*;
import com.github.vaerys.commands.competition.*;
import com.github.vaerys.commands.creator.*;
import com.github.vaerys.commands.creator.Shutdown;
import com.github.vaerys.commands.creator.directmessages.*;
import com.github.vaerys.commands.dmCommands.ClearReminderDM;
import com.github.vaerys.commands.dmCommands.HelpDM;
import com.github.vaerys.commands.dmCommands.InfoDM;
import com.github.vaerys.commands.dmCommands.ReminderDM;
import com.github.vaerys.commands.general.*;
import com.github.vaerys.commands.groups.ClearGroupUp;
import com.github.vaerys.commands.groups.GroupUp;
import com.github.vaerys.commands.help.*;
import com.github.vaerys.commands.pixels.*;
import com.github.vaerys.commands.roleSelect.CosmeticRoles;
import com.github.vaerys.commands.roleSelect.ListModifs;
import com.github.vaerys.commands.roleSelect.ListRoles;
import com.github.vaerys.commands.roleSelect.ModifierRoles;
import com.github.vaerys.commands.servers.*;
import com.github.vaerys.commands.slash.*;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.SlashCommand;

import java.util.ArrayList;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class CommandInit {

    public static ArrayList<Command> get() {
        ArrayList<Command> commands = new ArrayList<>();

        //Admin commands
        commands.add(new ChannelHere());
        commands.add(new ChannelStats());
        commands.add(new SetRateLimit());
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
        commands.add(new PurgeBannedData());
        //General commands
        commands.add(new GetAvatar());
        commands.add(new Hello());
        commands.add(new RemindMe());
        commands.add(new SetGender());
        commands.add(new SetQuote());
        commands.add(new Test());
        commands.add(new UserInfo());
        commands.add(new ClearReminder());
        commands.add(new EditLinks());
        commands.add(new Ping());
        commands.add(new LastDailyMessage());
        commands.add(new NewDailyMessage());
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
        commands.add(new RandomCC());
        //Competition commands
        commands.add(new FinalTally());
        commands.add(new GetCompEntries());
        commands.add(new EnterComp());
        commands.add(new EnterVote());
        commands.add(new PurgeComp());
        commands.add(new WeightedFinalTally());
        //Groups commands
        commands.add(new GroupUp());
        commands.add(new ClearGroupUp());
        //Pixels
        commands.add(new Pixels());
        commands.add(new PixelHelp());
        commands.add(new DefaultLevelMode());
        commands.add(new ProfileSettings());
        commands.add(new LevelUpMessage());
        commands.add(new ManagePixelRoles());
        commands.add(new TransferLevels());
        commands.add(new EditXp());
        commands.add(new SetLevel());
        commands.add(new SetPixelModifier());
        commands.add(new CheckPixelRoles());
        commands.add(new TopUserForRole());
        commands.add(new Rank());
        commands.add(new TopTen());
        commands.add(new SetLevelUpReaction());
        //Dm commands
        commands.add(new HelpDM());
        commands.add(new InfoDM());
        commands.add(new ReminderDM());
        commands.add(new ClearReminderDM());
        //slashCommands
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

    public static ArrayList<SlashCommand> getSlashCommands() {
        ArrayList<SlashCommand> commands = new ArrayList<>();


        return commands;
    }

    public static ArrayList<Command> getCreatorCommands() {
        //Creator Commands
        ArrayList<Command> commands = new ArrayList<>();
        commands.add(new Shutdown());
        commands.add(new Sudo());
        commands.add(new UpdateAvatar());
        commands.add(new GetGlobalStats());
        commands.add(new ResetPlayingStatus());
        commands.add(new ToggleTypingStatus());
        commands.add(new SetPlayingStatus());
        commands.add(new DailyMsg());
        //DM Creator Commands
        commands.add(new BlockUser());
        commands.add(new GetGuildList());
        commands.add(new GetGuildInfoDm());
        commands.add(new Respond());
        commands.add(new ShutdownDM());
        commands.add(new QuickRespond());
        commands.add(new TestDM());
        return commands;
    }
}
