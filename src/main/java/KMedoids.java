import java.util.*;

public class KMedoids {
    //k is number of clusters
    private int k;
    // distance between zip codes map
    HashMap<String ,HashMap<String,Integer>> distance;
    //zip code data
    private HashMap<String,Zip> zipsHash = null;
    private Random rg;
    /* The maximum number of iterations the algorithm is allowed to run. */
    private int maxIterations;

    public KMedoids(HashMap<String ,HashMap<String,Integer>> distance,HashMap<String,Zip> zipsHash, int k,int maxIterations){
        this.distance = distance;
        this.zipsHash = zipsHash;
        this.k = k;
        rg = new Random(System.currentTimeMillis());
        this.maxIterations = maxIterations;
    }

    public void calcDist(){
        for (Map.Entry<String ,HashMap<String,Integer>> entry : distance.entrySet()) {
            String key = entry.getKey();
            HashMap<String,Integer> value = entry.getValue();
            HashMap<String,Integer> value2 = new HashMap<>();

            for (Map.Entry<String,Integer> entry2 : value.entrySet()) {
                String key2 = entry2.getKey();
                if(!key.equals(key2)){
                    Zip z1 = zipsHash.get(key);
                    Zip z2 = zipsHash.get(key2);
                    int dist = (int) Math.ceil(distance(z1.getLatitude(),z1.getLongitude(),z2.getLatitude(),z2.getLongitude(),"M"));
                    value2.put(key2,dist);
                }
            }
            distance.replace(key,value2);
        }
    }

    //https://www.geodatasource.com/developers/java
    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }


    public Medoids cluster(){
        String[] medoids = new String[k];
        Medoids LowestMedoid = null;
        int itter = 0;
        main:while(maxIterations != itter) {
            ArrayList<String[]> output = new ArrayList<>();

            // create random medoids
            Object firstKey = distance.keySet().toArray()[0];
            for (int i = 0; i < k; i++) {
                int random = rg.nextInt(distance.get(firstKey).size());
                // pad 0s infront
                // have no duplicate values/zips
                Object secKey = distance.get(firstKey).keySet().toArray()[random];

                String temp = String.valueOf(secKey);
                if (!contains(medoids, temp)) {
                    if(!temp.equals("00000")){
                        medoids[i] = temp;
                    }else{
                        i--;
                    }
                } else {
                    i--;
                }
            }
            //create empty table
            HashMap<String ,HashMap<String,Integer>> table = new HashMap<>();
            HashMap<String,Integer> temp = new HashMap<String,Integer>();
            // x axis, medoids
            for (int i = 0; i <medoids.length; i++)
                temp.put(medoids[i], 0);
            // y axis zip codes
            for (Map.Entry<String, HashMap<String, Integer>> entry : distance.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if(!contains(medoids,key)) {
                    table.put(key, temp);
                }
            }

            // fill the distance graph between the medoids and keys
            for (Map.Entry<String ,HashMap<String,Integer>> entry : table.entrySet()) {
                String key = entry.getKey();
                HashMap<String,Integer> value = entry.getValue();
                HashMap<String,Integer> value2 = new HashMap<>();

                for (Map.Entry<String,Integer> entry2 : value.entrySet()) {
                    String key2 = entry2.getKey();
                    if(!contains(medoids,key)) {
                        if (!key.equals(key2)) {
                            int dist = distance.get(key).get(key2);
                            value2.put(key2, dist);
                        }
                    }
                }
                table.replace(key,value2);
            }


            //calculate cost
            int cost = 0;
            HashMap<String, ArrayList<String>> g = new HashMap<>();

            for (Map.Entry<String ,HashMap<String,Integer>> entry : table.entrySet()) {
                // get Y axis zip code
                String key = entry.getKey();
                // X axis and the table values (distance)
                HashMap<String,Integer> value = entry.getValue();
                // [distance, medoid]
                String[] costData = getMin(value);

                if(g.containsKey(costData[1])){
                    ArrayList<String> t = g.get(costData[1]);
                    t.add(key);
                    g.replace(costData[1], t);
                }else{
                    g.put(costData[1], new ArrayList<>());
                    g.get(costData[1]).add(key);
                }
                cost += Integer.valueOf(costData[0]);
            }

            // analyze cost for the lowest amount
            for (String m : medoids ) {
                if(g.get(m) !=null){
                    g.get(m).add(m);
                }else{
                    g.put(m, new ArrayList<>());
                    g.get(m).add(m);
                }
            }

            // see if cost is a new lowest
            if(itter == 0){
                LowestMedoid = new Medoids(cost, new ArrayList<String>(Arrays.asList(medoids)), g);
            }else{
                if(LowestMedoid.getCost() >cost){
                    LowestMedoid = new Medoids(cost, new ArrayList<String>(Arrays.asList(medoids)), g);

                }
            }
            itter++;
        }
        return LowestMedoid;
    }

    public boolean contains(final String[] array, final String v) {
        boolean result = false;
        if(array[0] == null){
            return false;
        }
        for(String i : array){
            if(i == null){
                break;
            }
            if(i.equals(v)){
                result = true;
                break;
            }
        }

        return result;
    }

    public String[] getMin(HashMap<String,Integer> inputArray){
        Object Key = inputArray.keySet().toArray()[0];
        if(Key == null){
            return new String[]{"-1","-1"};
        }
        int minValue = inputArray.get(Key);
        String index = "";
        for (Map.Entry<String,Integer> entry : inputArray.entrySet()) {
            Integer value = entry.getValue();
            if(value < minValue){
                minValue = value;
                index = entry.getKey();
            }

        }
        if(index.isEmpty()){
            index = String.valueOf(inputArray.keySet().toArray()[0]);
        }
        return new String[]{String.valueOf(minValue),index};
    }


}
