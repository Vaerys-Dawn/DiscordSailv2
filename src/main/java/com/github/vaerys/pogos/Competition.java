package com.github.vaerys.pogos;

import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CompObject;
import com.github.vaerys.objects.PollObject;
import com.github.vaerys.templates.GlobalFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vaerys on 29/08/2016.
 */
public class Competition extends GlobalFile {
    public static final String FILE_PATH = "Competition.json";
    private double fileVersion = 1.0;
    ArrayList<CompObject> entries = new ArrayList<>();
    ArrayList<String> voting = new ArrayList<>();
    int voteLimit = 1;
    ArrayList<PollObject> polls = new ArrayList<>();

    public void newEntry(CompObject entry) {
        entries.add(entry);
    }

    public ArrayList<CompObject> getEntries() {
        return entries;
    }

    public ArrayList<String> getVotes() {
        return voting;
    }

    public String addVote(long voterID, String vote) {
        if (voterID == 137281607638843392L && vote.startsWith("-")) {
            return "> Ha ha very funny dark, not this time.";
        }
        boolean userFound = false;
        int position = -1;
        ArrayList<String> userVotes;
        for (int i = 0; i < voting.size(); i++) {
            String[] testVotes = voting.get(i).split(",");
            if (voterID == Utility.stringLong(testVotes[0])) {
                position = i;
                userFound = true;
            }
        }
        if (userFound) {
            userVotes = new ArrayList<>(Arrays.asList(voting.get(position).split(",")));
        } else {
            userVotes = new ArrayList<>();
            userVotes.add(Long.toUnsignedString(voterID));
        }
        if (userVotes.size() == voteLimit + 1) {
            return "> You have already used up all of your votes.";
        }
        try {
            int voteNumber = Integer.parseInt(vote);
            if (voteNumber > entries.size() || voteNumber < 1) {
                return "> Your vote was not counted because it was not a valid entry number.";
            }
            userVotes.add(vote);
        } catch (NumberFormatException e) {
            return "> Your vote was not counted because it wasn't a number.";
        }

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
        return "> Thank you for voting.";
    }

    public void purgeVotes() {
        voting = new ArrayList<>();
    }

    public void purgeEntries() {
        entries = new ArrayList<>();
    }

    public List<String> getVoters() {
        return voting;
    }
}
