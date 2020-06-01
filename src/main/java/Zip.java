
public class Zip {
        private String ZipCode;
    private String City;
    private String State;
    private double Latitude;
    private double Longitude;
    private String Classification;
    private int Population;

    public Zip(String zipCode, String city, String state, double latitude, double longitude, String classification, int population) {
        ZipCode = zipCode;
        City = city;
        State = state;
        Latitude = latitude;
        Longitude = longitude;
        Classification = classification;
        Population = population;
    }

    @Override
    public String toString() {
        return "Zip{" +
                "ZipCode=" + ZipCode +
                ", City='" + City + '\'' +
                ", State='" + State + '\'' +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                ", Classification='" + Classification + '\'' +
                ", Population=" + Population +
                "}\n";
    }

    public String getZipCode() {
        return ZipCode;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public String getClassification() {
        return Classification;
    }

    public int getPopulation() {
        return Population;
    }
}
