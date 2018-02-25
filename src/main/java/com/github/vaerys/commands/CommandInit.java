package com.github.vaerys.commands;

import com.github.vaerys.commands.admin.*;
import com.github.vaerys.commands.cc.*;
import com.github.vaerys.commands.characters.*;
import com.github.vaerys.commands.competition.*;
import com.github.vaerys.commands.creator.*;
import com.github.vaerys.commands.creator.Shutdown;
import com.github.vaerys.commands.creator.directmessages.*;
import com.github.vaerys.commands.dmCommands.*;
import com.github.vaerys.commands.general.*;
import com.github.vaerys.commands.groups.ClearGroupUp;
import com.github.vaerys.commands.groups.GroupUp;
import com.github.vaerys.commands.help.*;
import com.github.vaerys.commands.mention.SetPrefix;
import com.github.vaerys.commands.mention.SetPrefixCC;
import com.github.vaerys.commands.pixels.*;
import com.github.vaerys.commands.roleSelect.CosmeticRoles;
import com.github.vaerys.commands.roleSelect.ListModifs;
import com.github.vaerys.commands.roleSelect.ListRoles;
import com.github.vaerys.commands.roleSelect.ModifierRoles;
import com.github.vaerys.commands.servers.*;
import com.github.vaerys.commands.setup.SetupBack;
import com.github.vaerys.commands.setup.SetupNext;
import com.github.vaerys.commands.setup.SetupQuit;
import com.github.vaerys.commands.setup.SetupRepeat;
import com.github.vaerys.commands.slash.*;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class CommandInit {

    final static Logger logger = LoggerFactory.getLogger(CommandInit.class);

    public static ArrayList<Command> get() {
        ArrayList<Command> commands = new ArrayList<>();

        //Admin commands
        commands.add(new AdminEcho());
        commands.add(new AddProfile());
        commands.add(new ChannelHere());
        commands.add(new ChannelStats());
        commands.add(new DenyXpPrefix());
        commands.add(new EditInfoFiles());
        commands.add(new ModNote());
        commands.add(new Module());
        commands.add(new Mute());
        commands.add(new PropMutePerms());
        commands.add(new PruneEmptyProfiles());
        commands.add(new PurgeBannedData());
        commands.add(new SetAdminRole());
        commands.add(new SetJoinMessage());
        commands.add(new SetMutedRole());
        commands.add(new SetPinLimit());
        commands.add(new SetRateLimit());
        commands.add(new SetRuleCode());
        commands.add(new SetRuleCodeReward());
        commands.add(new SetTrustedRoles());
        commands.add(new SetupWizard());
        commands.add(new Toggle());
        commands.add(new UpdateInfo());
        commands.add(new UpdateRolePerms());
        commands.add(new UserSettings());

        //General commands
        commands.add(new ClearReminder());
        commands.add(new EditLinks());
        commands.add(new GetAvatar());
        commands.add(new Hello());
        commands.add(new LastDailyMessage());
        commands.add(new NewDailyMessage());
        commands.add(new Patreon());
        commands.add(new Ping());
        commands.add(new RemindMe());
        commands.add(new RulesCode());
        commands.add(new SetGender());
        commands.add(new SetQuote());
        commands.add(new Test());
        commands.add(new UserInfo());
        commands.add(new WhatsMyColour());

        //Help commands
        commands.add(new BotHelp());
        commands.add(new BotInfo());
        commands.add(new GetGuildInfo());
        commands.add(new Help());
        commands.add(new HelpChannel());
        commands.add(new HelpModules());
        commands.add(new HelpSettings());
        commands.add(new HelpTags());
        commands.add(new Info());
        commands.add(new ListTags());
        commands.add(new Report());
        commands.add(new SilentReport());
        commands.add(new StartUpGuide());

        //RoleSelect commands
        commands.add(new CosmeticRoles());
        commands.add(new ListModifs());
        commands.add(new ListRoles());
        commands.add(new ModifierRoles());

        //Server commands
        commands.add(new AddServer());
        commands.add(new DelServer());
        commands.add(new EditServerDesc());
        commands.add(new EditServerIP());
        commands.add(new EditServerName());
        commands.add(new ListServers());
        commands.add(new Server());

        //Character Commands
        commands.add(new CharAvatar());
        commands.add(new CharInfo());
        commands.add(new DelChar());
        commands.add(new EditChar());
        commands.add(new ListChars());
        commands.add(new SelectChar());
        commands.add(new SetBioRolePrefix());
        commands.add(new UpdateChar());

        //CC commands
        commands.add(new DelCC());
        commands.add(new EditCC());
        commands.add(new GetCCData());
        commands.add(new InfoCC());
        commands.add(new ListCCs());
        commands.add(new NewCC());
        commands.add(new RandomCC());
        commands.add(new SearchCCs());

        //Competition commands
        commands.add(new EnterComp());
        commands.add(new EnterVote());
        commands.add(new FinalTally());
        commands.add(new GetCompEntries());
        commands.add(new PurgeComp());
        commands.add(new RemoveEntry());
        commands.add(new WeightedFinalTally());

        //Groups commands
        commands.add(new ClearGroupUp());
        commands.add(new GroupUp());

        //Pixels
        commands.add(new CheckPixelRoles());
        commands.add(new DefaultLevelMode());
        commands.add(new EditXp());
        commands.add(new LevelUpMessage());
        commands.add(new ManagePixelRoles());
        commands.add(new PixelHelp());
        commands.add(new Pixels());
        commands.add(new ProfileSettings());
        commands.add(new Rank());
        commands.add(new SetLevel());
        commands.add(new SetLevelUpReaction());
        commands.add(new SetPixelModifier());
        commands.add(new TopTen());
        commands.add(new TopUserForRole());
        commands.add(new TransferLevels());

        //Dm commands
        commands.add(new BotInfoDm());
        commands.add(new ClearReminderDM());
        commands.add(new HelpDM());
        commands.add(new InfoDM());
        commands.add(new ReminderDM());

        //slashCommands
        commands.add(new DealWithIt());
        commands.add(new Disapproval());
        commands.add(new Fite());
        commands.add(new Gib());
        commands.add(new Lenny());
        commands.add(new PaintMe());
        commands.add(new Shrug());
        commands.add(new TableFlip());
        commands.add(new UnFlip());

        //mention Commands
        commands.add(new SetPrefix());
        commands.add(new SetPrefixCC());

        validate(commands);

        return commands;
    }

    public static ArrayList<Command> getSetupCommands() {
        ArrayList<Command> commands = new ArrayList<>();

        // step 1
        commands.add(new Module());
        commands.add(new HelpModules());

        // Setup Traversal Commands
        commands.add(new SetupBack());
        commands.add(new SetupNext());
        commands.add(new SetupQuit());
        commands.add(new SetupRepeat());

        validate(commands);
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
        commands.add(new EventSetup());
        commands.add(new WhoIsThis());
        //DM Creator Commands
        commands.add(new BlockUser());
        commands.add(new GetGuildList());
        commands.add(new GetGuildInfoDm());
        commands.add(new Respond());
        commands.add(new ShutdownDM());
        commands.add(new QuickRespond());
        commands.add(new TestDM());
        commands.add(new Echo());
        commands.add(new WhoWasThat());
        commands.add(new PatreonToken());
        commands.add(new UnBlockUser());
        commands.add(new BotStats());

        validate(commands);

        return commands;
    }

    private static void validate(ArrayList<Command> commands) {
        for (Command c : commands) {
            logger.trace("Validating Command: " + c.getClass().getName());
            String errorReport = c.validate();
            Globals.addToErrorStack(errorReport);
        }
    }
}
