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
    private Declination declination;
    private RightAscension rightAscension;
    private double apparentMagnitude;
    private double absoluteMagnitude;
    private double distance; // in light years
    private Constellation constellation;
    private Hemisphere hemisphere;
    private double temperature;
    private double mass;

    //catalogs that will contain any stars and constellatons created
    private static final String STARS_FOLDER = "src/data/stars/"; 
    private static final String CONSTELLATIONS_FOLDER = "src/data/constellations/";

    // constructor - to update (exceptions)
    public Star(String name, String catalogName, Declination declination, RightAscension rightAscension,
                double apparentMagnitude, double distance, Constellation constellation, String hemisphere,
                double temperature, double mass) {
        // validate name
        if (!name.matches("[A-Z]{3}[0-9]{4}")) {
            throw new IllegalArgumentException("Name must contain 3 uppercase letters and 4 digits");
        }
        this.name = name;
        // !!! TO DO: validate catalogName -> 1 greek letter + constellation name which it was added to
        this.catalogName = createCatalogName(constellation); 
        this.declination = declination;
        this.rightAscension = rightAscension;
        // validate apparentMagnitude
        if (apparentMagnitude < -26.74 || apparentMagnitude > 15.00) {
            throw new IllegalArgumentException("Apparent magnitude must be between -26.74 and 15.00");
        }
        this.apparentMagnitude = apparentMagnitude;
        this.absoluteMagnitude = calculateAbsoluteMagnitude(apparentMagnitude, distance);
        // validate distance
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        this.distance = distance;
        this.constellation = constellation;
        // validate hemisphere using fromString
        this.hemisphere = Hemisphere.fromString(hemisphere);
        // validate temperature
        if (temperature < 2000) {
            throw new IllegalArgumentException("Temperature must be at least 2000 degrees Celsius");
        }
        this.temperature = temperature;
        // validate mass
        if (mass < 0.1 || mass > 50) {
            throw new IllegalArgumentException("Mass must be min. 0.1 to max. 50 solar masses");
        }
        this.mass = mass;

        saveStar(); //after creating a star it will get saved to the catalog
        updateCatalog(constellation);
    }

    // Star coordinates
    public String getStarCoordinates() {
        return String.format(
                "1. Declination: %d° %d' %.2f''%n2. Right ascension: %02d h %d m %d s",
                declination.getXX(),
                declination.getYY(),
                declination.getZZ(),
                rightAscension.getXX(),
                rightAscension.getYY(),
                rightAscension.getZZ()
        );
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
        List<Star> stars = loadStarsFromFile(constellation.getName());
        int count = stars.size();

        if (count >= GreekAlphabet.values().length) 
        {
            throw new IllegalStateException("Limit of stars in a constellation has been reached!");
            //since theres 24? letters in greek alphabet there cant be more stars names after that
        }
        String greekLetter = GreekAlphabet.values()[count].name();
        return greekLetter + " " + constellation.getName();
    }

      // method that loads all created stars (and returns it as a list)
      private static List<Star> loadStarsFromFile(String constellationName)
       {
        File file = new File(STARS_FOLDER + constellationName + ".obj");
        if (!file.exists()) 
        {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
        {
            return (List<Star>) ois.readObject();
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
        List<Star> stars = loadStarsFromFile(constellation.getName());

        // sorted stars
        stars.sort(Comparator.comparingInt(star -> Arrays.asList(GreekAlphabet.values())
                .indexOf(GreekAlphabet.valueOf(star.catalogName.split(" ")[0]))));
        // updated names
        for (int i = 0; i < stars.size(); i++) 
        {
            stars.get(i).catalogName = GreekAlphabet.values()[i].name() + " " + constellation.getName();
        }
        saveStarsToFile(stars, constellation.getName());
    }

    // method that helps with updating catalog 
    private static void saveStarsToFile(List<Star> stars, String constellationName) 
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STARS_FOLDER 
        + constellationName + ".obj"))) 
        {
            oos.writeObject(stars);
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // metod that saves a created star (used in constructor)
    private void saveStar() 
    {
        List<Star> stars = loadStarsFromFile(constellation.getName());
        stars.add(this);
        saveStarsToFile(stars, constellation.getName());
    }

    // method that deletes a star 
    public static void removeStar(String name, String constellationName) 
    {
        List<Star> stars = loadStarsFromFile(constellationName);
        stars.removeIf(star -> star.catalogName.equals(name)); // if a star exist in catalog
        updateCatalog(new Constellation(constellationName)); // updating constellation's stars' names 
                                                            // in catalog after a star has been removed
        saveStarsToFile(stars, constellationName); // saving changes
    }

    // method that finds supernovas (if there are any) and returns a list of them
    public static List<Star> Supernovas(String constellationName) 
    {
        List<Star> stars = loadStarsFromFile(constellationName);
        List<Star> supernovas = new ArrayList<>();
        for (Star star : stars) 
        {
            if (star.mass > 1.44) 
            {
                supernovas.add(star);
            }
        }
        return supernovas;
    }


    // method that prints all existing stars with their attributes
    /*
     Oprócz dodania nowej gwiazdy możemy również wyświetlić wszystkie
     gwiazdy w bazie,
     */
    public static void viewStars() 
    {
        File folder = new File(STARS_FOLDER);
        File[] files = folder.listFiles();
    
        if (files == null || files.length == 0) 
        {
            System.out.println("There are no stars in database");
            return;
        }
    
        for (File file : files) 
        {
            // removing .obj to get constellation's name
            List<Star> stars = loadStarsFromFile(file.getName().replace(".obj", ""));
            for (Star star : stars) 
            {
                System.out.println("-------------------------");
                System.out.println("Name: " + star.name);
                System.out.println("Catalog name: " + star.catalogName);
                System.out.println("Declination: " + star.declination.getXX() + "° " + star.declination.getYY() + "' " + star.declination.getZZ() + "''");
                System.out.println("Right ascention: " + star.rightAscension.getXX() + "h " + star.rightAscension.getYY() + "m " + star.rightAscension.getZZ() + "s");
                System.out.println("Apparent magnitude: " + star.apparentMagnitude);
                System.out.println("Absolute magnitude: " + star.absoluteMagnitude);
                System.out.println("Distance: " + star.distance);
                System.out.println("Constellation: " + star.constellation.getName());
                System.out.println("Hemisphere: " + star.hemisphere);
                System.out.println("Temperature: " + star.temperature + "°C");
                System.out.println("Mass: " + star.mass + " mas Słońca");
                System.out.println("-------------------------\n");
            }
        }
    }
    
}
