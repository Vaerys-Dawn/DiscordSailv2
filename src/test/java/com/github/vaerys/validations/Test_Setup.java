package com.github.vaerys.validations;

import com.github.vaerys.guildtoggles.ToggleInit;
import com.github.vaerys.templates.GuildToggle;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Test_Setup extends TestCase {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Checks to see if each module defined in GuildToggle has a setup step module associated with it.
     * Only produces warnings for modules that do not have configured setups.
     */
    public void testModuleHasStage() {
        // get all modules, check if they have notnull "setupPage"
        List<GuildToggle> modules = ToggleInit.getAllToggles(false);
        for (GuildToggle m : modules) {
            if (m.isModule() && m.setupPage == null) {
                logger.warn(m.name().toString() + " does not have valid setup page");
            }
        }
    }
}
