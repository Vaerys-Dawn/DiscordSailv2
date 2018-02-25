package com.github.vaerys.objects;

import com.github.vaerys.commands.competition.EnterVote;
import com.github.vaerys.pogos.GuildConfig;

import java.util.ArrayList;

/**
 * Created by Vaerys on 28/03/2017.
 */
public class PollObject {
    boolean open = false;
    String pollName;
    String userID;
    String pollTopic;
    ArrayList<String> options = new ArrayList<>();
    ArrayList<VoteObject> votes = new ArrayList<>();
    boolean multiVotes = false;

    public PollObject(String pollName, String userID, String pollTopic) {
        this.pollName = pollName;
        this.userID = userID;
        this.pollTopic = pollTopic;
    }

    public String addOption(String option){
        if (open){
            return "> Cannot add option as poll is open.";
        }
        for (String s: options){
            if (option.equalsIgnoreCase(s)){
                return "> Option already added.";
            }
        }
        options.add(option);
        return "> Option added.";
    }

    public String toggleOpen(GuildConfig config){
        open = !open;
        if (!open){
            if (votes.size() > 0){
                // TODO: 28/03/2017 post results. then clear list.
            }
            return "> Poll voting closed.";
        }else {
            return "> Poll voting Open, you can vote in this poll by running\n`" + config.getPrefixCommand() +
                    new EnterVote().names[0] + " " + pollName + " [Vote]";
        }
    }

    public String toggleMultiVote(){
        multiVotes = !multiVotes;
        if (!multiVotes){
            return "> allowing of multiple votes has been disabled.";
        }else {
            return "> allowing of multiple votes has been enabled.";
        }
    }

    public String vote(String votes){
        if (multiVotes){

        }
        return "> null.";
    }
}
