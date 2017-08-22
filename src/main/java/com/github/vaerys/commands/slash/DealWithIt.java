package com.github.vaerys.commands.slash;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class DealWithIt implements SlashCommand {

    @Override
    public String execute(String args, CommandObject command) {
        return "(•\\_•) ( •\\_•)>⌐■-■ (⌐■_■)";
    }

    @Override
    public String[] names() {
        return new String[]{"DealWithIt"};
    }
}
