package src.models;
import src.utils.GreekAlphabet;
import src.utils.Hemisphere;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Star implements Serializable {

    private static final long serialVersionUID = 4747420996408461500L;

    private String name;
    private String catalogName;
    private Hemisphere hemisphere;
    private Declination declination;
    private RightAscension rightAscension;
    private double apparentMagnitude;
    private double absoluteMagnitude;
    private double distance; // in light years
    private Constellation constellation;
    private double temperature;
    private double mass;

    // catalog that will contain stars
    private static final String STARS_FOLDER = "src/data/stars/";

    // Constructor - to update (exceptions)
    public Star(String name, Hemisphere hemisphere, Declination declination, RightAscension rightAscension,
                Constellation constellation, double apparentMagnitude, double distance,
                double temperature, double mass) {
        if (!name.matches("[A-Z]{3}[0-9]{4}")) {
            throw new IllegalArgumentException("Name must contain 3 uppercase letters and 4 digits");
        }
        // validate other required fields
        if (hemisphere == null) {
            throw new IllegalArgumentException("Hemisphere cannot be null.");
        }
        if (declination == null) {
            throw new IllegalArgumentException("Declination cannot be null.");
        }
        if (rightAscension == null) {
            throw new IllegalArgumentException("Right Ascension cannot be null.");
        }
        if (constellation == null) {
            throw new IllegalArgumentException("Constellation cannot be null.");
        }

        // assign hemisphere before validating declination
        this.hemisphere = hemisphere;
        validateDeclination(declination); // validate declination based on hemisphere

        if (apparentMagnitude < -26.74 || apparentMagnitude > 15.00) {
            throw new IllegalArgumentException("Apparent magnitude must be between -26.74 and 15.00");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        if (temperature < 2000) {
            throw new IllegalArgumentException("Temperature must be at least 2000 degrees Celsius");
        }
        if (mass < 0.1 || mass > 50) {
            throw new IllegalArgumentException("Mass must be min. 0.1 to max. 50 solar masses");
        }

        this.name = name;
        this.declination = declination;
        this.rightAscension = rightAscension;
        this.constellation = constellation;
        this.apparentMagnitude = apparentMagnitude;
        this.distance = distance;
        this.temperature = temperature;
        this.mass = mass;

        // calculate values not provided in constructor
        this.catalogName = createCatalogName(constellation);
        this.absoluteMagnitude = calculateAbsoluteMagnitude(apparentMagnitude, distance);

        saveStar(); // after creating a star it will get saved to the catalog
        updateCatalog(constellation);
    }


    // GETTERS:
    // Name
    public String getName() {
        return name;
    }
    // Catalog Name
    public String getCatalogName() {
        return catalogName;
    }
    // Declination
    public String getDeclination() {
        return declination.toString();
    }
    // Right Ascension
    public String getRightAscension() {
        return rightAscension.toString();
    }
    // Apparent Magnitude
    public double getApparentMagnitude() {
        return apparentMagnitude;
    }
    // Absolute Magnitude
    public double getAbsoluteMagnitude() {
        return absoluteMagnitude;
    }
    // Distance (in light years)
    public double getDistance() {
        return distance;
    }
    // Constellation
    public String getConstellation() {
        return constellation.getName();
    }

    // for updateCatalog() method
    public Constellation getConstellation2() {
        return constellation;
    }

    // Hemisphere
    public Hemisphere getHemisphere() {
        return hemisphere;
    }
    // Temperature
    public double getTemperature() {
        return temperature;
    }
    // Mass
    public double getMass() {
        return mass;
    }


    // Method for validating declination based on hemisphere
    private void validateDeclination(Declination declination) {
        int degrees = declination.getXX();

        if (hemisphere == Hemisphere.NORTHERN) {
            if (degrees < 0 || degrees > 90) {
                throw new IllegalArgumentException("For the Northern Hemisphere, declination must be between 0° and 90°.");
            }
        }
        else if (hemisphere == Hemisphere.SOUTHERN) {
            if (degrees < -90 || degrees > 0) {
                throw new IllegalArgumentException("For the Southern Hemisphere, declination must be between -90° and 0°.");
            }
        }
        else {
            throw new IllegalArgumentException("Invalid hemisphere");
        }
    }


    // Method: Calculate Absolute Magnitude
    public double calculateAbsoluteMagnitude(double apparentMagnitude, double distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        if (apparentMagnitude < -26.74 || apparentMagnitude > 15.00) {
            throw new IllegalArgumentException("Apparent magnitude must be between -26.74 and 15.00");
        }
        double distanceInParsecs = distance / 3.26; // 1 parsec = 3.26 light years
        return apparentMagnitude - 5 * Math.log10(distanceInParsecs) + 5;
    }

    // method that creates star's catalog name based on it's constellation (used in constructor)
    /*
        nazwa katalogowa – nazwa katalogowa składa się litery alfabetu
        greckiego oraz nazwy gwiazdozbioru. Najjaśniejsza gwiazda w
        gwiazdozbiorze oznaczana jest jako alfa, kolejna jako beta i tak
        dalej. Na potrzeby projektu zakładamy, iż kolejne litery greckie
        nadawane są gwiazdom w takiej kolejności, w jakiej dodane zostały
        do gwiazdozbioru. np gamma Wolarza
     */
    private String createCatalogName(Constellation constellation)
    {
        List<Star> allStars = loadStarsFromFile();
        List<Star> starsInConstellation = new ArrayList<>();

        // find stars only from constellation
        for (Star star : allStars) {
            if (star.getConstellation2().equals(constellation)) {
                starsInConstellation.add(star);
            }
        }
        int count = starsInConstellation.size();

        if (count >= GreekAlphabet.values().length)
        {
            throw new IllegalStateException("Limit of stars in a constellation has been reached!");
            //since theres 24? letters in greek alphabet there cant be more stars names after that
        }
        String greekLetter = GreekAlphabet.values()[count].name();
        return greekLetter + " " + constellation.getName();
    }

    // method that loads ALL created stars (and returns it as a list)
    public static List<Star> loadStarsFromFile()
    {
        List<Star> allStars = new ArrayList<>();
        File directory = new File(STARS_FOLDER);

        // check if directory exists
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Star directory not found: " + STARS_FOLDER);
            return allStars; // returns empty list if dir doesn't exist
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".obj"));
        if (files == null) {
            System.out.println("No stars found in directory: " + STARS_FOLDER);
            return allStars;
        }

        // load stars from files
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Star star = (Star) ois.readObject();
                allStars.add(star);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading stars from file: " + file.getName());
                e.printStackTrace();
            }
        }

        return allStars;
    }

    // method that updates stars names +numbers of stars in a constellation after a star was deleted
    /*
     W przypadku usunięcia np. gwiazdy beta w danym gwiazdozbiorze, należy
     zadbać, o to, aby wszystkie pozostałe nazwy katalogowe zostały
     uaktualnione. Np. po usunięciu gwiazdy alfa Ryb, wszystkie pozostałe
     gwiazdy w gwiazdozbiorze są aktualizowane, tj. beta Ryb na alfa Ryb,
     gamma Ryb na beta Ryb i tak dalej.
     */
    private static void updateCatalog(Constellation constellation)
    {
        List<Star> stars = loadStarsFromFile();
        // load all stars from specific constellation
        List<Star> starsInConstellation = new ArrayList<>();
        for (Star star : stars) {
            if (star.getConstellation2().equals(constellation)) {
                starsInConstellation.add(star);
            }
        }

        // sorted stars
        starsInConstellation.sort(Comparator.comparingInt(star -> Arrays.asList(GreekAlphabet.values())
                .indexOf(GreekAlphabet.valueOf(star.catalogName.split(" ")[0]))));
        // updated names
        for (int i = 0; i < starsInConstellation.size(); i++) {
            starsInConstellation.get(i).catalogName = GreekAlphabet.values()[i].name() + " " + constellation.getName();
            saveStarToFile(starsInConstellation.get(i));
        }
    }

    // method that helps with updating catalog
    // Method: Saving added star to file
    public static void saveStarToFile(Star star)
    {
        String filePath = STARS_FOLDER + star.getName() + ".obj";

        // check if directory exists, otherwise -> create one
        File directory = new File(STARS_FOLDER);
        if (!directory.exists()) {
            System.err.println("Error: Could not create directory " + STARS_FOLDER);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(star);
            System.out.println("Star saved to: " + filePath);
        }
        catch (IOException e) {
            System.err.println("Error: Couldn't save star to file: " + filePath);
            e.printStackTrace();
        }
    }

    // method that saves a created star (used in constructor)
    private void saveStar()
    {
        List<Star> stars = loadStarsFromFile();
        stars.add(this);
        saveStarToFile(this);
    }

    // method that deletes a star BASED ON CHOSEN CATALOG NAME
    public static void removeStar(String name)
    {
        List<Star> stars = loadStarsFromFile();
        Star starToRemove = null;

        // finding star to remove
        for (Star star : stars) {
            if (star.getCatalogName().equalsIgnoreCase(name)) {
                starToRemove = star;
                break;
            }
        }

        // if star exists, remove it
        if (starToRemove != null) {
            stars.remove(starToRemove);

            // delete file associated with the star
            String filePath = STARS_FOLDER + starToRemove.getName() + ".obj";
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                System.out.println("Star file deleted: " + filePath);
            } else {
                System.err.println("Error: Could not delete file " + filePath);
            }

            // update catalog (renaming other stars)
            updateCatalog(starToRemove.getConstellation2());
            System.out.println("Star has been removed from catalog.");
        }

        else {
            System.out.println("Error: Star not found in catalog.");
        }
    }

    // METHODS FOR SEARCHING STARS BASED ON CRITERIAS

    // Method that finds stars in x parsecs distance from Earth
    public static void findStarByDistance(double distanceInput) {
        List<Star> stars = loadStarsFromFile();
        boolean foundStars = false;

        try {
            for (Star star: stars) {
                double distanceInParsecs = star.getDistance() * 0.3066013938; // convert light years to parsecs
                double roundedDistance = Math.round(distanceInParsecs * 100.00)/100.00; // rounding up to 2 places
                if (roundedDistance == distanceInput) {
                    foundStars = true;
                    System.out.println("* Star Name: " + star.getName() + ";" + " Rounded Star distance in parsecs: " + roundedDistance + ";" + " Precise Star distance in parsecs: " + distanceInParsecs);
                }
            }
            if (!foundStars) {
                System.out.println("No stars found in this distance ;(");
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid distance input. " + e.getMessage());
        }
    }

    // Method that finds stars based on temperature (in chosen interval)
    public static void findStarByTemperature(double intervalStart, double intervalEnd) {
        List<Star> stars = loadStarsFromFile();
        boolean foundStars = false;

        try {
            for (Star star: stars) {
                if (star.temperature >= intervalStart && star.temperature <= intervalEnd) {
                    System.out.println("* Star Name: " + star.getName() + ";" + " Star temperature: " + star.getTemperature() + "°C");
                    foundStars = true;
                }
            }
            if (!foundStars) {
                System.out.println("No stars found in this temperature interval ;(");
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid temperature input. " + e.getMessage());
        }
    }

    // Method that finds stars based on absolute magnitude (in chosen interval)
    public static void findStarByMagnitude(double intervalStart, double intervalEnd) {
        List<Star> stars = loadStarsFromFile();
        boolean foundStars = false;
        try {
            for (Star star: stars) {
                if (star.getAbsoluteMagnitude() >= intervalStart && star.getAbsoluteMagnitude() <= intervalEnd) {
                    System.out.println("* Star Name: " + star.getName() + ";" + " Absolute magnitude: " + star.getAbsoluteMagnitude());
                    foundStars = true;
                }
            }
            if (!foundStars) {
                System.out.println("No stars found in this absolute magnitude interval ;(");
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid magnitude input. " + e.getMessage());
        }
    }
    // Method that finds stars based on hemisphere
    public static void findStarByHemisphere(String hemisphereName) {
        List<Star> stars = loadStarsFromFile();
        boolean foundStars = false;

        try {
            Hemisphere hemisphere = Hemisphere.fromString(hemisphereName);

            for (Star star : stars) {
                if (star.getHemisphere() == hemisphere) {
                    System.out.println("* Star Name: " + star.getName() + "; Hemisphere: " + star.getHemisphere().getAbbreviation());
                    foundStars = true;
                }
            }
            if (!foundStars) {
                System.out.println("No stars found in this hemisphere ;(");
            }

        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid hemisphere input. " + e.getMessage());
        }
    }

    // method that finds supernovas (if there are any)
    public static void findSupernovas()
    {
        List<Star> stars = loadStarsFromFile();
        boolean potentialSupernova = false;

        try {
            for (Star star : stars)
            {
                if (star.getMass() > 1.44) // Chandrasekhar limit
                {
                    potentialSupernova = true;
                    System.out.println("* Star Name: " + star.getName() + ";" + " Star mass: " + star.getMass() + " solar masses");
                }
            }
            if (!potentialSupernova) {
                System.out.println("No supernovas found ;(.");
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid mass input. " + e.getMessage());
        }

    }

    // Star coordinates - not necessary but wanted to add it
    public static void getStarCoordinates(String name) {
        List<Star> allStars = loadStarsFromFile();
        boolean foundStar = false;

        try {
            for (Star star : allStars)
            {
                if (star.getName().equalsIgnoreCase(name))
                {
                    foundStar = true;
                    System.out.println("* Star Name: " + star.getName() + ";" + " 1. Declination: " + star.getDeclination() + " 2. Right ascension: " + star.getRightAscension());
                }
            }
            if (!foundStar) {
                System.out.println("No stars of this name found ;(.");
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("Incorrect star name " + e.getMessage());
        }
    }

    // METHODS FOR DISPLAYING STARS
    // method that prints all existing stars with their attributes
    /*
     Oprócz dodania nowej gwiazdy możemy również wyświetlić wszystkie
     gwiazdy w bazie,
     */
    public static void viewStars()
    {
        List<Star> stars = loadStarsFromFile();
        int size = stars.size();

        if (size == 0) {
            System.out.println("There are no stars to display");
        }
        else {
            for (Star star : stars) {
                System.out.println("-------------------------");
                System.out.println("Name: " + star.name);
                System.out.println("Catalog name: " + star.catalogName);
                System.out.println("Hemisphere: " + star.hemisphere);
                System.out.println("Declination: " + star.declination.getXX() + "° " + star.declination.getYY() + "' " + star.declination.getZZ() + "''");
                System.out.println("Right ascension: " + star.rightAscension.getXX() + "h " + star.rightAscension.getYY() + "m " + star.rightAscension.getZZ() + "s");
                System.out.println("Constellation: " + star.constellation.getName());
                System.out.println("Apparent magnitude: " + star.apparentMagnitude);
                System.out.println("Absolute magnitude: " + star.absoluteMagnitude);
                System.out.println("Distance: " + star.distance + " light years");
                System.out.println("Temperature: " + star.temperature + "°C");
                System.out.println("Mass: " + star.mass + " solar mass");
                System.out.println("-------------------------\n");
            }
        }
    }


    // Method: Display Stars from a specific constellation
    public static void displayContellationStars(String constellationName) {
        List<Star> stars = loadStarsFromFile();
        boolean foundStars = false;

        for (Star star : stars) {
            if (star.getConstellation().equalsIgnoreCase(constellationName)) {
                foundStars = true;
                System.out.println("-------------------------");
                System.out.println("Name: " + star.name);
                System.out.println("Catalog name: " + star.catalogName);
                System.out.println("Hemisphere: " + star.hemisphere);
                System.out.println("Declination: " + star.declination.getXX() + "° " + star.declination.getYY() + "' " + star.declination.getZZ() + "''");
                System.out.println("Right ascension: " + star.rightAscension.getXX() + "h " + star.rightAscension.getYY() + "m " + star.rightAscension.getZZ() + "s");
                System.out.println("Constellation: " + star.constellation.getName());
                System.out.println("Apparent magnitude: " + star.apparentMagnitude);
                System.out.println("Absolute magnitude: " + star.absoluteMagnitude);
                System.out.println("Distance: " + star.distance + " light years");
                System.out.println("Constellation: " + star.constellation.getName());
                System.out.println("Temperature: " + star.temperature + "°C");
                System.out.println("Mass: " + star.mass + " solar mass");
                System.out.println("-------------------------\n");
            }
        }
        if (!foundStars) {
            System.out.println("No stars in this constellation have been found ;(");
        }
    }
}
