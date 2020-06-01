public class Person {
    private int age;
    private String location;
    private String gender;
    private String snap;

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", location=" + location +
                ", gender='" + gender + '\'' +
                ", snap='" + snap + '\'' +
                "}\n";
    }

    public Person(int age, String location, String gender, String snap) {
        this.age = age;
        this.location = location;
        this.gender = gender;
        this.snap = snap;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSnap() {
        return snap;
    }

    public void setSnap(String snap) {
        this.snap = snap;
    }

}
