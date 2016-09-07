package Objects;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class ChannelTypeObject {
    String Type;
    String ID;

    public ChannelTypeObject(String type, String ID) {
        Type = type;
        this.ID = ID;
    }

    public String getType() {
        return Type;
    }

    public String getID() {
        return ID;
    }
}
