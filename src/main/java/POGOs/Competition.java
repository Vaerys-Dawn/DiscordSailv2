package POGOs;

import Main.Constants;
import Main.Globals;
import Objects.PollObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 29/08/2016.
 */
public class Competition {
    boolean properlyInit = false;
    ArrayList<PollObject> entries = new ArrayList<>();
    ArrayList<String> voting = new ArrayList<>();

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public void newEntry(PollObject entry) {
        entries.add(entry);
    }

    public ArrayList<PollObject> getEntries() {
        return entries;
    }

    public ArrayList<String> getVotes() {
        return voting;
    }

    public String addVote(String voterID, String vote) {
        StringBuilder builder = new StringBuilder();
        boolean userFound = false;
        int position = -1;
        ArrayList<String> userVotes;
        ArrayList<String> newVotes = new ArrayList<>(Arrays.asList(vote.split(" ")));
        for (int i = 0; i < voting.size(); i++) {
            String[] testVotes = voting.get(i).split(",");
            if (voterID.equals(testVotes[0])) {
                position = i;
                userFound = true;
            }
        }
        if (userFound) {
            userVotes = new ArrayList<>(Arrays.asList(voting.get(position).split(",")));
        } else {
            userVotes = new ArrayList<>();
            userVotes.add(voterID);
        }
        if (userVotes.size() == Globals.voteLimit + 1) {
            return "> You have already used up all of your votes.";
        }
        int x = 0;
        builder.append("> Your votes for entries: \n");
        for (int i = 0; newVotes.size() > i; i++) {
            if (x < Globals.voteLimit && userVotes.size() < Globals.voteLimit + 1) {
                try {
                    int entry = Integer.parseInt(newVotes.get(i));
                    if (entry > Globals.compEntries) {
                        //builder.append("    **Vote Not Counted. Reason: Number too high**\n");
                    } else if(entry == 0) {
                        //builder.append("    **Vote Not Counted. Reason: Number = 0\n");
                    }
                    else{
                        userVotes.add("" + entry);
                        builder.append(Constants.PREFIX_INDENT+"Entry: **" + entry + "**\n");
                        x++;
                    }
                } catch (NumberFormatException e) {
                    //builder.append("    **Vote Not Counted. Reason: Not a number**\n");
                }
            }
        }
        if (newVotes.size() > Globals.voteLimit) {
            //builder.append("    **Rest of Votes Not Counted: Reason Max votes = " + Globals.voteLimit + "**\n");
        }
        builder.append(Constants.PREFIX_INDENT+"Have been saved. you now have: **" + (Globals.voteLimit - userVotes.size() + 1) + "** Vote token(s) left.");

        StringBuilder finalVotes = new StringBuilder();
        for (String s : userVotes) {
            finalVotes.append(s + ",");
        }
        finalVotes.delete(finalVotes.length() - 1, finalVotes.length());
        if (userFound) {
            voting.set(position, finalVotes.toString());
        } else {
            voting.add(finalVotes.toString());
        }
        return builder.toString();
    }

}
