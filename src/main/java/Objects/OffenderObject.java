package Objects;

import Main.Globals;

/**
 * Created by Vaerys on 04/11/2016.
 */
public class OffenderObject extends UserTypeObject{
    int count;

    public OffenderObject(String ID) {
        super(Globals.getClient().getUserByID(ID).getName() + "#" + Globals.getClient().getUserByID(ID).getDiscriminator(), ID);
        count = 1;
    }

    public void addOffence(){
        count++;
    }

    public int getCount() {
        return count;
    }
}
