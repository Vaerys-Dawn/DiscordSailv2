package Objects;

import java.util.ArrayList;

/**
 * Created by Vaerys on 07/04/2017.
 */
public class PatchObject {
    String PatchLevel = "";
    ArrayList<String> patchedGuildIDs = new ArrayList<>();

    public PatchObject(String patchLevel) {
        PatchLevel = patchLevel;
    }

    public String getPatchLevel() {
        return PatchLevel;
    }

    public void setPatchLevel(String patchLevel) {
        PatchLevel = patchLevel;
    }

    public ArrayList<String> getPatchedGuildIDs() {
        return patchedGuildIDs;
    }

    public void setPatchedGuildIDs(ArrayList<String> patchedGuildIDs) {
        this.patchedGuildIDs = patchedGuildIDs;
    }
}
