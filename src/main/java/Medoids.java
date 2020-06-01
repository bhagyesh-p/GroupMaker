import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Medoids {
    private ArrayList<String> medoids;
    private int cost;
    private HashMap<String, ArrayList<String>> group;

    public Medoids(int cost,ArrayList<String> medoids,HashMap<String, ArrayList<String>> g ){
        group = g;
        this.cost = cost;
        this.medoids = medoids;
    }

    public ArrayList<String> getMedoids() {
        return medoids;
    }

    public void setMedoids(ArrayList<String> medoids) {
        this.medoids = medoids;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public HashMap<String, ArrayList<String>> getGroup() {
        return group;
    }

    public void setGroup(HashMap<String, ArrayList<String>> group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Medoids{\n" +
                "medoids=" + medoids +
                "\n, cost=" + cost +
                "\n, group=" + printGroup() +
                "\n, group size=" + group.size() +
                "\n}";
    }
    public String printGroup(){
        String s = "";
        for (Map.Entry<String, ArrayList<String>> entry : group.entrySet()) {
            s += entry.getKey()+" : "+entry.getValue() +"\n";
        }
        return s;
    }
}
