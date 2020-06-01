import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();

        ArrayList<Person> LOP = new ArrayList<>();
        String[] zip;
        HashMap<String, Zip> zipsHash = null;
        Random rand = new Random();
        try {
            zipsHash = zips();
            // gets the list of zips form db
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        Set<String> zipsUsed = new HashSet<>();
        zip = zipsHash.keySet().toArray(new String[0]);
        int leng = zip.length - 1;
        // generates 30,000 users
        for (int i = 0; i < 30000; i++) {
            String ZipUsed = (zip[rand.nextInt((leng - 0) + 1) + 0]);
            LOP.add(new Person(
                    rand.nextInt((25 - 14) + 1) + 14,
                    ZipUsed,
                    ((rand.nextInt((25 - 14) + 1) + 14) % 2 == 0) ? "M" : "F",
                    String.valueOf(i)
            ));
            zipsUsed.add(ZipUsed);
        }

        HashMap<Integer, ArrayList<Person>> hm = new HashMap<>();
        for (int i = 1; i < 13; i++) {
            hm.put(i, new ArrayList<Person>());
        }
        // put them in subsets
        for (Person p : LOP) {
            if (group11(p)) {
                hm.get(11).add(p);
            } else if (group3(p)) {
                hm.get(3).add(p);
            } else if (group10(p)) {
                hm.get(10).add(p);
            } else if (group9(p)) {
                hm.get(9).add(p);
            } else if (group6(p)) {
                hm.get(6).add(p);
            } else if (group1(p)) {
                hm.get(1).add(p);
            } else if (group12(p)) {
                hm.get(12).add(p);
            } else if (group8(p)) {
                hm.get(8).add(p);
            } else if (group7(p)) {
                hm.get(7).add(p);
            } else if (group4(p)) {
                hm.get(4).add(p);
            } else if (group5(p)) {
                hm.get(5).add(p);
            } else if (group2(p)) {
                hm.get(2).add(p);
            } else {
                //invalid zip codes
                System.out.println(p);
            }
        }
        // Algorithm applied to subsets
        for (Map.Entry<Integer, ArrayList<Person>> entry2 : hm.entrySet()) {
            System.out.println("===========[" + entry2.getKey() + "]==========");
            System.out.println(entry2.getValue().size());
            //create hash maps based on zip code and people.
            if (entry2.getValue().size() > 1) {
                ArrayList<Person> listOfPeople = entry2.getValue();
                zipsUsed.clear();
                for (Person p : listOfPeople) {
                    zipsUsed.add(p.getLocation());
                }
                HashMap<Integer, ArrayList<Person>> map = new HashMap<>();
                for (Person p : listOfPeople) {
                    int k = Integer.valueOf(p.getLocation());
                    if (map.containsKey(k)) {
                        map.get(k).add(p);
                    } else {
                        map.put(k, new ArrayList<Person>());
                        map.get(k).add(p);
                    }
                }

                // Lower L = more groups
                int l = 20;
                int k = (int) Math.ceil(map.size() / l);
                if ((k * l < map.size())) {
                    k += 1;
                }
                // creates the 2d chart for less than 5 distance graph
                try {
                    zipsHash = zips();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CsvValidationException e) {
                    e.printStackTrace();
                }
                zip = zipsHash.keySet().toArray(new String[0]);
                HashMap<String, HashMap<String, Integer>> distance = new HashMap<>();
                HashMap<String, Integer> temp = new HashMap<String, Integer>();

                for (int i = 0; i < zipsUsed.size(); i++)
                    temp.put(zipsUsed.toArray()[i].toString(), 0);

                for (int i = 0; i < zipsUsed.size(); i++)
                    distance.put(zipsUsed.toArray()[i].toString(), temp);


                KMedoids kMedoids = new KMedoids(distance, zipsHash, k, 100);
                // populate the distance graph
                kMedoids.calcDist();
                // have the group algorithm exicute
                Medoids m = kMedoids.cluster();

                // create groups based off zip codes
                HashMap<String, ArrayList<String>> grouping = m.getGroup();
                HashMap<String, ArrayList<Person>> groupingPeople = new HashMap<>();
                for (String Mstr : m.getMedoids()) {
                    String temp1 = String.format("%05d", Integer.valueOf(Mstr));
                    groupingPeople.put(temp1, new ArrayList<Person>());
                }

                for (Person p : listOfPeople) {
                    for (Map.Entry<String, ArrayList<String>> entry : grouping.entrySet()) {
                        String key = entry.getKey();
                        ArrayList<String> value = entry.getValue();
                        String temp1 = p.getLocation();
                        if (value.contains(temp1)) {
                            if (groupingPeople.containsKey(key) || key.equals(temp1)) {
                                ArrayList<Person> t = groupingPeople.get(key);
                                t.add(p);
                                groupingPeople.replace(temp1, t);
                            }
                        }
                    }
                }

                //Separates grouping by age groups
                HashMap<String, ArrayList<ArrayList<Person>>> byAge = new HashMap<>();
                ArrayList<Person> age14to17 = new ArrayList<>();
                ArrayList<Person> age18to20 = new ArrayList<>();
                ArrayList<Person> age21to25 = new ArrayList<>();
                for (Map.Entry<String, ArrayList<Person>> entry : groupingPeople.entrySet()) {
                    String key = entry.getKey();
                    ArrayList<Person> value = entry.getValue();

                    age14to17 = getAgeRange(value, 14, 17);
                    age18to20 = getAgeRange(value, 18, 20);
                    age21to25 = getAgeRange(value, 21, 25);
                    byAge.put(key, new ArrayList<>(Arrays.asList(age14to17, age18to20, age21to25)));
                }
                //print groups
                printByAge(byAge);
                long endTime = System.nanoTime();
                System.out.println("Took " + (endTime - startTime) + " ns ");
            }
        }
    }

    public static HashMap<String, Zip> zips() throws IOException, CsvValidationException {
        HashMap<String, Zip> zips = new HashMap<>();
        CSVReader reader = new CSVReader(new FileReader("src/main/resources/zipCodes.csv"));
        String[] nextLine;
        int i = 0;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            if (i > 0) {
                Zip z = new Zip(nextLine[0], nextLine[1], nextLine[2], Double.valueOf(nextLine[3]), Double.valueOf(nextLine[4]), nextLine[5], Integer.parseInt(nextLine[6]));
                zips.put(nextLine[0], z);
            } else {
                i++;
            }
        }
        return zips;
    }

    public static void printArray(int[][] myArray1) {
        for (int[] row : myArray1) {
            for (int elem : row) {
                System.out.printf("%4d", elem);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static ArrayList<Person> getAgeRange(ArrayList<Person> sample, int start, int end) {
        return sample.stream().filter(person1 -> start <= person1.getAge() && person1.getAge() <= end).collect(Collectors.toCollection(ArrayList::new));
    }

    public static void printByAge(HashMap<String, ArrayList<ArrayList<Person>>> byAge) {
        for (Map.Entry<String, ArrayList<ArrayList<Person>>> entry : byAge.entrySet()) {
            System.out.println("=====[" + entry.getKey() + "]=====");

            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i == 0) {
                    System.out.print("age: 14-17: ");
                } else if (i == 1) {
                    System.out.print("age: 18-20: ");
                } else {
                    System.out.print("age: 21-25: ");
                }
                for (Person p : entry.getValue().get(i)) {
                    System.out.print(p.getSnap() + ", ");
                }
                System.out.println();
            }
        }
        System.out.println("=================");
    }

    public static boolean group1(Person p) {
        // 980-994, 97, 900-961
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(98[0-9]{3}|99[0-3][0-9]{2}|994[0-8][0-9]|9949[0-9])");// 980-994
        Pattern pattern1 = Pattern.compile("(97[0-8][0-9]{2}|979[0-8][0-9]|9799[0-9])");// 97
        Pattern pattern2 = Pattern.compile("(9[0-5][0-9]{3}|960[0-9]{2}|961[0-8][0-9]|9619[0-9])");// 900-961

        if (pattern.matcher(zip).find()) { // WA
            return true;
        } else if (pattern1.matcher(zip).find()) { // OR
            return true;
        } else if (pattern2.matcher(zip).find()) { // CA
            return true;
        }

        return false;
    }

    public static boolean group2(Person p) {
        // 995-999
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(99[5-8][0-9]{2}|999[0-8][0-9]|9999[0-9])");// 995-999

        if (pattern.matcher(zip).find()) { // AK
            return true;
        }

        return false;
    }

    public static boolean group3(Person p) {
        // 967-968
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(967[0-9]{2}|968[0-8][0-9]|9689[0-9])");// 07-08

        if (pattern.matcher(zip).find()) {
            return true;
        }

        return false;
    }

    public static boolean group4(Person p) {
        // 832-839, 84, 889-899, 80-81, 870-884, 820-831, 85-86
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(83[2-8][0-9]{2}|839[0-8][0-9]|8399[0-9])");// 832-839
        Pattern pattern1 = Pattern.compile("(84[0-8][0-9]{2}|849[0-8][0-9]|8499[0-9])");// 84
        Pattern pattern2 = Pattern.compile("(889[0-8][0-9]|8899[0-9]|89[0-9]{3})");// 889-899
        Pattern pattern3 = Pattern.compile("(80[0-9]{3}|81[0-8][0-9]{2}|819[0-8][0-9]|8199[0-9])");// 80-81
        Pattern pattern4 = Pattern.compile("(87[0-9]{3}|88[0-3][0-9]{2}|884[0-8][0-9]|8849[0-9])");// 870-884
        Pattern pattern5 = Pattern.compile("(82[0-9]{3}|830[0-9]{2}|831[0-8][0-9]|8319[0-9])");// 820-831
        Pattern pattern6 = Pattern.compile("(85[0-9]{3}|86[0-8][0-9]{2}|869[0-8][0-9]|8699[0-9])");// 85-86

        if (pattern.matcher(zip).find()) { // ID
            return true;
        } else if (pattern1.matcher(zip).find()) { // UT
            return true;
        } else if (pattern2.matcher(zip).find()) { // NV
            return true;
        } else if (pattern3.matcher(zip).find()) { // CO
            return true;
        } else if (pattern4.matcher(zip).find()) { // NM
            return true;
        } else if (pattern5.matcher(zip).find()) { // WY
            return true;
        } else if (pattern6.matcher(zip).find()) { // AZ
            return true;
        }

        return false;
    }

    public static boolean group5(Person p) {
        //59 , 58, 57
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(59[0-8][0-9]{2}|599[0-8][0-9]|5999[0-9])");// 59
        Pattern pattern1 = Pattern.compile("(58[0-8][0-9]{2}|589[0-8][0-9]|5899[0-9])");// 58
        Pattern pattern2 = Pattern.compile("(57[0-8][0-9]{2}|579[0-8][0-9]|5799[0-9])");// 57

        if (pattern.matcher(zip).find()) { // MT
            return true;
        } else if (pattern1.matcher(zip).find()) { // ND
            return true;
        } else if (pattern2.matcher(zip).find()) { // SD
            return true;
        }

        return false;
    }

    public static boolean group6(Person p) {
        // 550-567,53-54,60-62,46-47,43-45,48-49,247-269,40-42

        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(55[0-9]{3}|56[0-6][0-9]{2}|567[0-8][0-9]|5679[0-9])");// 550-567
        Pattern pattern1 = Pattern.compile("(53[0-9]{3}|54[0-8][0-9]{2}|549[0-8][0-9]|5499[0-9])");// 53-54
        Pattern pattern2 = Pattern.compile("(6[01][0-9]{3}|62[0-8][0-9]{2}|629[0-8][0-9]|6299[0-9])");// 60-62
        Pattern pattern3 = Pattern.compile("(46[0-9]{3}|47[0-8][0-9]{2}|479[0-8][0-9]|4799[0-9])");// 46-47
        Pattern pattern4 = Pattern.compile("(4[34][0-9]{3}|45[0-8][0-9]{2}|459[0-8][0-9]|4599[0-9])");// 43-45
        Pattern pattern5 = Pattern.compile("(48[0-9]{3}|49[0-8][0-9]{2}|499[0-8][0-9]|4999[0-9])");// 48-49
        Pattern pattern6 = Pattern.compile("(24[78][0-9]{2}|249[0-8][0-9]|2499[0-9]|2[56][0-9]{3})");// 247-269
        Pattern pattern7 = Pattern.compile("(4[01][0-9]{3}|42[0-8][0-9]{2}|429[0-8][0-9]|4299[0-9])");// 40-42

        if (pattern.matcher(zip).find()) { //MN
            return true;
        } else if (pattern1.matcher(zip).find()) { // WI
            return true;
        } else if (pattern2.matcher(zip).find()) { // IL
            return true;
        } else if (pattern3.matcher(zip).find()) { //IN
            return true;
        } else if (pattern4.matcher(zip).find()) { // OH
            return true;
        } else if (pattern5.matcher(zip).find()) { //MI
            return true;
        } else if (pattern6.matcher(zip).find()) {  // WV
            return true;
        } else if (pattern7.matcher(zip).find()) {// KY
            return true;
        }

        return false;
    }

    public static boolean group7(Person p) {
        // 68-69, 66-67, 50-52, 63-65
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(68[0-9]{3}|69[0-8][0-9]{2}|699[0-8][0-9]|6999[0-9])");// 68-69
        Pattern pattern1 = Pattern.compile("(66[0-9]{3}|67[0-8][0-9]{2}|679[0-8][0-9]|6799[0-9])");// 66-67
        Pattern pattern2 = Pattern.compile("(5[01][0-9]{3}|52[0-8][0-9]{2}|529[0-8][0-9]|5299[0-9])");// 50-52
        Pattern pattern3 = Pattern.compile("(6[34][0-9]{3}|65[0-8][0-9]{2}|659[0-8][0-9]|6599[0-9])");// 63-65

        if (pattern.matcher(zip).find()) { // NE
            return true;
        } else if (pattern1.matcher(zip).find()) {// KS
            return true;
        } else if (pattern2.matcher(zip).find()) {// IA
            return true;
        } else if (pattern3.matcher(zip).find()) {// MO
            return true;
        }
        return false;
    }

    public static boolean group8(Person p) {
        // 75-79, 73-74, 716-729,700-715
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(7[5-8][0-9]{3}|79[0-8][0-9]{2}|799[0-8][0-9]|7999[0-9])");// 75-79
        Pattern pattern1 = Pattern.compile("(73[0-9]{3}|74[0-8][0-9]{2}|749[0-8][0-9]|7499[0-9])");// 73-74
        Pattern pattern2 = Pattern.compile("(71[6-8][0-9]{2}|719[0-8][0-9]|7199[0-9]|72[0-9]{3})");// 716-729
        Pattern pattern3 = Pattern.compile("(70[0-9]{3}|71[0-4][0-9]{2}|715[0-8][0-9]|7159[0-9])");// 700-715
        Pattern pattern4 = Pattern.compile("(885[1-7][0-9]|8858[0-9])");// 88510-88589 TX el Paso

        if (pattern4.matcher(zip).find()) {//el paso
            return true;
        } else if (pattern.matcher(zip).find()) { // TX
            return true;
        } else if (pattern1.matcher(zip).find()) {// LA
            return true;
        } else if (pattern2.matcher(zip).find()) {// OK
            return true;
        } else if (pattern3.matcher(zip).find()) {// AR
            return true;
        }
        return false;
    }

    public static boolean group9(Person p) {
        // 386-399, 35-36, 370-385,30-31,32-34
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(38[6-8][0-9]{2}|389[0-8][0-9]|3899[0-9]|39[0-9]{3})");// 386-399
        Pattern pattern1 = Pattern.compile("(35[0-9]{3}|36[0-8][0-9]{2}|369[0-8][0-9]|3699[0-9])");// 35-36,
        Pattern pattern2 = Pattern.compile("(37[0-9]{3}|38[0-4][0-9]{2}|385[0-8][0-9]|3859[0-9])");// 370-385
        Pattern pattern3 = Pattern.compile("(30[0-9]{3}|31[0-8][0-9]{2}|319[0-8][0-9]|3199[0-9])");// 30-31
        Pattern pattern4 = Pattern.compile("(3[23][0-9]{3}|34[0-8][0-9]{2}|349[0-8][0-9]|3499[0-9])");// 32-34

        if (pattern.matcher(zip).find()) { // MS
            return true;
        } else if (pattern1.matcher(zip).find()) { // AL
            return true;
        } else if (pattern2.matcher(zip).find()) { // TN
            return true;
        } else if (pattern3.matcher(zip).find()) { // GA
            return true;
        } else if (pattern4.matcher(zip).find()) { // FL
            return true;
        }

        return false;
    }

    public static boolean group10(Person p) {
        // 27-28, 29
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("(27[0-9]{3}|28[0-8][0-9]{2}|289[0-8][0-9]|2899[0-9])");// 27-28
        Pattern pattern1 = Pattern.compile("(29[0-8][0-9]{2}|299[0-8][0-9]|2999[0-9])");// 29

        if (pattern.matcher(zip).find()) { // NC
            return true;
        } else if (pattern1.matcher(zip).find()) { // SC
            return true;
        }

        return false;
    }

    public static boolean group11(Person p) {
        // 220-246,206-219,150-196,197-199,028-029,010-027
        // 07-08,10-14,06

        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("^(0{1}[7-8])");// 07-08
        Pattern pattern1 = Pattern.compile("^([0][6])");//06
        Pattern pattern2 = Pattern.compile("^(0{1}2[8-9])");//028-029
        Pattern pattern3 = Pattern.compile("^(0{1}(1[0-9]|2[0-7]))");// 010-027

        if (pattern.matcher(zip).find()) {
            return true;
        } else if (zip.matches("^([1][0-4])")) {// 10-14
            return true;
        } else if (pattern1.matcher(zip).find()) {// 06
            return true;
        } else if (zip.matches("(200[4-8][0-9]|2009[0-9]|20[1-9][0-9]{2}|2[1-3][0-9]{3}|24[0-5][0-9]{2}|246[0-4][0-9]|2465[0-8])")) { // VA/MD ??, 220-246 and 206-219 (picks up MD for some reason at times, should be only VA)
            return true;
        } else if (zip.matches("(20[0-4][0-9]{2}|20500)")) {// VA/DC ??, 200-205 (picks up VA for some reason at times, should be only DC)
            return true;
        } else if (zip.matches("(1[5-8][0-9]{3}|19[0-5][0-9]{2}|19600)")) { // PA 150-196
            return true;
        } else if (zip.matches("(1[0-3][0-9]{3}|14[0-8][0-9]{2}|149[0-8][0-9]|1499[0-9])")) {// NY 150-196
            return true;
        } else if (zip.matches("(19[78][0-9]{2}|199[0-8][0-9]|1999[0-9])")) {// DL 197-199
            return true;
        } else if (pattern2.matcher(zip).find()) {// RI
            return true;
        } else if (pattern3.matcher(zip).find()) {// MA 010-027
            return true;
        }

        return false;
    }

    public static boolean group12(Person p) {
        // 05, 030-038, 039-049
        String zip = p.getLocation();
        Pattern pattern = Pattern.compile("^(0{1}5[0-8][0-9]{2}|59[0-8][0-9]|599[0-9])");// 05
        Pattern pattern1 = Pattern.compile("^(0{1}(3[0-7][0-9]{2}|38[0-8][0-9]|389[0-9]))");// 030-038
        Pattern pattern2 = Pattern.compile("^(0{1}(39[0-8][0-9]|399[0-9]|4[0-9]{3}))");// 039-049

        if (pattern.matcher(zip).find()) {
            return true;
        } else if (pattern1.matcher(zip).find()) {
            return true;
        } else if (pattern2.matcher(zip).find()) {
            return true;
        }

        return false;
    }

}
