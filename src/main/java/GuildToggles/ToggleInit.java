package GuildToggles;

import GuildToggles.Modules.*;
import GuildToggles.Toggles.*;
import Interfaces.GuildToggle;

import java.util.ArrayList;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ToggleInit {
    public static ArrayList<GuildToggle> get(){
        ArrayList<GuildToggle> guildToggles = new ArrayList<>();

        //toggles
        guildToggles.add(new AdminLogging());
        guildToggles.add(new AutoArtPinning());
        guildToggles.add(new ChannelLogging());
        guildToggles.add(new CompEntries());
        guildToggles.add(new DailyMessage());
        guildToggles.add(new DeleteLogging());
        guildToggles.add(new DenyInvites());
        guildToggles.add(new DontLogBot());
        guildToggles.add(new EditLogging());
        guildToggles.add(new ExtendEditLog());
        guildToggles.add(new GeneralLogging());
        guildToggles.add(new JoinLeaveLgging());
        guildToggles.add(new MentionSpam());
        guildToggles.add(new MuteRepeatOffender());
        guildToggles.add(new RateLimiting());
        guildToggles.add(new RoleIsToggle());
        guildToggles.add(new ShitpostFiltering());
        guildToggles.add(new SlashCommands());
        guildToggles.add(new UserRoleLogging());
        guildToggles.add(new UseTimeStamps());
        guildToggles.add(new Voting());
        guildToggles.add(new XpDecay());
        guildToggles.add(new XpGain());
        guildToggles.add(new JoinServerMessages());

        //modules
        guildToggles.add(new ModuleGroups());
        guildToggles.add(new ModuleChars());
        guildToggles.add(new ModuleComp());
        guildToggles.add(new ModuleServers());
        guildToggles.add(new ModuleRoles());
        guildToggles.add(new ModuleCC());
        guildToggles.add(new ModuleMe());
        guildToggles.add(new ModuleModMuting());
        guildToggles.add(new ModuleArtPinning());
        guildToggles.add(new ModulePixels());

        return guildToggles;
    }

    // TODO: 23/04/2017 add global toggles i.e. allow multiple reminders.
    public static ArrayList<GuildToggle> globalToggles(){
        return null;
    }
}
