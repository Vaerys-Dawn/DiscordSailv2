package POGOs;

import Objects.CompetitionObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 29/08/2016.
 */
public class Competition {
    public boolean properlyInit = false;
    ArrayList<CompetitionObject> entries = new ArrayList<>();

    public void newEntry(CompetitionObject entry){
        entries.add(entry);
    }

    public ArrayList<CompetitionObject> getEntries() {
        return entries;
    }
}
