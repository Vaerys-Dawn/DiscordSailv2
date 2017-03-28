package POGOs;

import Main.Constants;
import Objects.CompObject;
import Objects.PollObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 29/08/2016.
 */
public class Competition {
    boolean properlyInit = false;
    ArrayList<CompObject> entries = new ArrayList<>();
    ArrayList<String> voting = new ArrayList<>();
    int voteLimit = 1;
    ArrayList<PollObject> polls = new ArrayList<>();

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public void newEntry(CompObject entry) {
        entries.add(entry);
    }

    public ArrayList<CompObject> getEntries() {
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
        if (userVotes.size() == voteLimit + 1) {
            return "> You have already used up all of your votes.";
        }
        int x = 0;
        builder.append("> Your votes for entries: \n");
        for (int i = 0; newVotes.size() > i; i++) {
            if (x < voteLimit && userVotes.size() < voteLimit + 1) {
                try {
                    int entry = Integer.parseInt(newVotes.get(i));
                    if (entry > entries.size()) {
                        //builder.append("    **Vote Not Counted. Reason: Number too high**\n");
                    } else if(entry <= 0) {
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
        if (newVotes.size() > voteLimit) {
            //builder.append("    **Rest of Votes Not Counted: Reason Max votes = " + Globals.voteLimit + "**\n");
        }
        builder.append(Constants.PREFIX_INDENT+"Have been saved. you now have: **" + (voteLimit - userVotes.size() + 1) + "** Vote token(s) left.");

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
