package com.github.vaerys.commands;

import com.github.vaerys.commands.admin.Module;
import com.github.vaerys.commands.admin.*;
import com.github.vaerys.commands.adminccs.*;
import com.github.vaerys.commands.cc.*;
import com.github.vaerys.commands.characters.*;
import com.github.vaerys.commands.competition.*;
import com.github.vaerys.commands.creator.Shutdown;
import com.github.vaerys.commands.creator.*;
import com.github.vaerys.commands.creator.directmessages.*;
import com.github.vaerys.commands.dmcommands.InfoDM;
import com.github.vaerys.commands.dmcommands.InviteDm;
import com.github.vaerys.commands.general.*;
import com.github.vaerys.commands.groups.ClearGroupUp;
import com.github.vaerys.commands.groups.GroupUp;
import com.github.vaerys.commands.help.*;
import com.github.vaerys.commands.joinmessages.*;
import com.github.vaerys.commands.mention.SetPrefix;
import com.github.vaerys.commands.mention.SetPrefixAdminCC;
import com.github.vaerys.commands.mention.SetPrefixCC;
import com.github.vaerys.commands.modtools.*;
import com.github.vaerys.commands.pixels.*;
import com.github.vaerys.commands.roleSelect.CosmeticRoles;
import com.github.vaerys.commands.roleSelect.ListModifs;
import com.github.vaerys.commands.roleSelect.ListRoles;
import com.github.vaerys.commands.roleSelect.ModifierRoles;
import com.github.vaerys.commands.servers.*;
import com.github.vaerys.commands.slash.*;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class CommandList {

    final static Logger logger = LoggerFactory.getLogger(CommandList.class);

    private static final List<Command> commands = new ArrayList<Command>() {{
        //Admin commands
        add(new AdminEcho());
        add(new AddProfile());
        add(new ChannelHere());
        add(new ChannelStats());
        add(new DenyXpPrefix());
        add(new EditInfoFiles());
        add(new ModNote());
        add(new Module());
        add(new Mute());
        add(new PropMutePerms());
        add(new ResetRuleCode());
        add(new PruneEmptyProfiles());
        add(new PurgeBannedData());
        add(new SetAdminRole());
        add(new SetWelcomeMessage());
        add(new SetMutedRole());
        add(new SetPinLimit());
        add(new SetRateLimit());
        add(new SetRuleCode());
        add(new SetRuleCodeReward());
        add(new SetTrustedRoles());
//        add(new SetupWizard());
        add(new Toggle());
        add(new UpdateInfo());
        add(new UpdateRolePerms());
        add(new UserSettings());

        //General commands
        add(new ClearReminder());
        add(new EditLinks());
        add(new GetAvatar());
        add(new GetReminders());
        add(new Hello());
        add(new LastDailyMessage());
        add(new NewDailyMessage());
        add(new Patreon());
        add(new Ping());
        add(new RemindMe());
        add(new RulesCode());
        add(new SetGender());
        add(new SetQuote());
        add(new Test());
        add(new UserInfo());
        add(new WhatsMyColour());
        add(new Invite());

        //Help commands
        add(new BotHelp());
        add(new BotInfo());
        add(new GetGuildInfo());
        add(new Commands());
        add(new HelpChannel());
        add(new HelpModules());
        add(new HelpSettings());
        add(new HelpTags());
        add(new Help());
        add(new ListTags());
        add(new Report());
        add(new SilentReport());

        //RoleSelect commands
        add(new CosmeticRoles());
        add(new ListModifs());
        add(new ListRoles());
        add(new ModifierRoles());

        //Server commands
        add(new AddServer());
        add(new DelServer());
        add(new EditServerDesc());
        add(new EditServerIP());
        add(new EditServerName());
        add(new ListServers());
        add(new Server());

        //Character Commands
        add(new CharAvatar());
        add(new CharInfo());
        add(new DelChar());
        add(new EditChar());
        add(new ListChars());
        add(new SelectChar());
        add(new SetBioRolePrefix());
        add(new UpdateChar());
//        add(new EditDungeonChar());

        //CC commands
        add(new DelCC());
        add(new EditCC());
        add(new GetCCData());
        add(new InfoCC());
        add(new ListCCs());
        add(new NewCC());
        add(new RandomCC());
        add(new SearchCCs());

        //Competition commands
        add(new EnterComp());
        add(new EnterVote());
        add(new FinalTally());
        add(new GetCompEntries());
        add(new PurgeComp());
        add(new RemoveEntry());
        add(new WeightedFinalTally());

        //Groups commands
        add(new ClearGroupUp());
        add(new GroupUp());

        //Pixels
        add(new CheckPixelRoles());
        add(new DefaultLevelMode());
        add(new EditXp());
        add(new LevelUpMessage());
        add(new ManagePixelRoles());
        add(new PixelHelp());
        add(new Pixels());
        add(new ProfileSettings());
        add(new Rank());
        add(new SetLevel());
        add(new SetLevelUpReaction());
        add(new SetPixelModifier());
        add(new TopTen());
        add(new TopUserForRole());
        add(new TransferLevels());

        //Dm commands
        add(new InfoDM());
        add(new InviteDm());

        //slashCommands
        add(new DealWithIt());
        add(new Disapproval());
        add(new Fite());
        add(new Gib());
        add(new Lenny());
        add(new PaintMe());
        add(new Shrug());
        add(new TableFlip());
        add(new UnFlip());

        //mention Commands
        add(new SetPrefix());
        add(new SetPrefixCC());
        add(new SetPrefixAdminCC());

        //joinMessage Commands
        add(new DeleteJoinMessage());
        add(new EditJoinMessage());
        add(new JoinMessageInfo());
        add(new ListJoinMessages());
        add(new NewJoinMessage());

        //admin CCs
        add(new NewAdminCC());
        add(new ListAdminCCs());
        add(new EditAdminCC());
        add(new DeleteAdminCC());
        add(new ResetTries());
        add(new AdminCCInfo());
    }};

    private static final List<Command> creatorCommands = new ArrayList<Command>() {{
        //Creator Commands
        add(new Shutdown());
        add(new Sudo());
        add(new UpdateAvatar());
        add(new GetGlobalStats());
        add(new ResetPlayingStatus());
        add(new ToggleTypingStatus());
        add(new SetPlayingStatus());
        add(new DailyMsg());
        add(new EventSetup());
        add(new WhoIsThis());
        add(new UnBlacklistUser());
        add(new BlacklistUser());

        //DM Creator Commands
        add(new BlockUser());
        add(new GetGuildList());
        add(new GetGuildInfoDm());
        add(new Respond());
        add(new QuickRespond());
        add(new Echo());
        add(new WhoWasThat());
        add(new PatreonToken());
        add(new UnBlockUser());
        add(new BotStats());
        add(new Present());
    }};


    public static List<Command> getAllCommands(boolean validate) {
        if (validate) validateCommands(commands);
        return commands;
    }

    public static List<Command> getAllCommands() {
        return getAllCommands(false);
    }

    public static List<Command> getSetupCommands() {
        return getAllCommands(false);
    }

    public static List<Command> getAllCreatorCommands(boolean validate) {
        if (validate) validateCommands(creatorCommands);
        return creatorCommands;
    }

    public static List<Command> getCreatorCommands() {
        return getAllCreatorCommands(false);
    }

    private static void validateCommands(List<Command> commands) {
        for (Command c : commands) {
            logger.trace("Validating Command: " + c.getClass().getName());
            String errorReport = c.validate();
            Globals.addToErrorStack(errorReport);
        }
    }

    public static List<Command> getAll() {
        List<Command> allCommands = new ArrayList<>(commands);
        allCommands.addAll(creatorCommands);
        return allCommands;
    }

    public static List<Command> getCommands(boolean isDm) {
        List<Command> getCommands = new ArrayList<>();
        for (Command c : commands) {
            if (isDm) {
                if (c.channel == ChannelSetting.FROM_DM || c.hasDmVersion) getCommands.add(c);
            } else {
                if (c.channel != ChannelSetting.FROM_DM) getCommands.add(c);
            }
        }
        return getCommands;
    }

    public static <T extends Command> T getCommand(Class obj) {
        if (!Command.class.isAssignableFrom(obj)) {
            throw new IllegalArgumentException("Cannot Get Command from Class (" + obj.getName() + ")");
        }
        for (Command c : getAll()) {
            if (c.getClass().getName().equals(obj.getName())) {
                return (T) c;
            }
        }
        if (getAll().size() != 0) {
            throw new IllegalArgumentException("Could not find Command (" + obj.getName() + ")");
        }
        return null;
    }

    public static List<Command> getCommandsByType(SAILType type) {
        return getAll().stream().filter(c -> c.type == type).collect(Collectors.toList());
    }
}
