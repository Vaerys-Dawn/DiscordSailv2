package POGOs;

import Main.Constants;
import Objects.BlackListObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 04/04/2017.
 */

// TODO: 04/04/2017 add to backups and load ups.
public class BlackLists {
    ArrayList<BlackListObject> blackListObjects = new ArrayList<>();

    public List<BlackListObject> getBlackListObjects() {
        blackListObjects.sort((o1,o2) -> o1.getType().compareTo(o2.getType()));
        return blackListObjects;
    }

    public List<BlackListObject> getPending() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_PENDING)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getBlockedForAll() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_ALL)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getBelowTrusted() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_TRUSTED)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getCustomCommands() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_CC)).collect(Collectors.toList());
        return list;
    }

    public List<BlackListObject> getServerListings() {
        List<BlackListObject> list = blackListObjects.stream().filter(b -> b.getType().equalsIgnoreCase(Constants.BL_SERVER)).collect(Collectors.toList());
        return list;
    }



}
