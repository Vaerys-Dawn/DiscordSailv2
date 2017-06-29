package Objects;

/**
 * Created by Vaerys on 29/06/2017.
 */
public class RandomStatusObject {
    String status;
    int weight;

    public RandomStatusObject(String status, int weight) {
        this.status = status;
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public int getWeight() {
        return weight;
    }
}
