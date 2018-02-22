package com.github.vaerys.enums;

public enum GuildSetupStage {
    UNSET,          // Setup has not been run on this guild
    BEGIN_SETUP,    // The first step is to send a DM to the user.
    COMPLETE        // Setup has been completed.
}
