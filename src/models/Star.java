package src.models;
import src.utils.GreekAlphabet;
import src.utils.Hemisphere;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Star {
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

    // map that contains the number of stars in each catalog
    private static final Map<String, Integer> constellationStarCount = new HashMap<>();

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
        this.catalogName = catalogName;
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


    // method that creates star's catalog name based on it's constellation
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
        String name = constellation.getName();
        int count = constellationStarCount.getOrDefault(name,0); // 0 if theres no stars in a contellation

        if (count >= GreekAlphabet.values().length)
        {
            throw new IllegalStateException("Limit of stars in a constellation has been reached!");
            //since theres 24? letters in greek alphabet there cant be more stars names after that
        }

        // naming a star
        String greekLetter = GreekAlphabet.values()[count].name();
        constellationStarCount.put(name, count+1);
        return greekLetter + " " + name;
    }


    // method that updates stars names +numbers of stars in a constellation after a star was deleted
    /* 
     W przypadku usunięcia np. gwiazdy beta w danym gwiazdozbiorze, należy
     zadbać, o to, aby wszystkie pozostałe nazwy katalogowe zostały
     uaktualnione. Np. po usunięciu gwiazdy alfa Ryb, wszystkie pozostałe
     gwiazdy w gwiazdozbiorze są aktualizowane, tj. beta Ryb na alfa Ryb,
     gamma Ryb na beta Ryb i tak dalej.
     */
    private static void updateCatalog(Constellation constellation, List<Star> starList)
    {
        List<Star> stars = new ArrayList<>(); //list of stars from a particular constellation

        for(Star star: starList)
        {
            if (star.constellation.getName().equals(constellation.getName()))
            {
                stars.add(star);
            }
        }

        stars.sort(Comparator.comparingInt(star -> 
        {
            String greekName = star.catalogName.split(" ")[0]; 
            //sorting by greekletters (their index in GreekAlphabet)
            return Arrays.asList(GreekAlphabet.values()).indexOf(GreekAlphabet.valueOf(greekName));
        }
        ));

        // new names
        for (int i=0; i < stars.size(); i++)
        {
            stars.get(i).catalogName = GreekAlphabet.values()[i].name()+ " " +constellation.getName();    
        }

        // updating numbers of stars in a catalog
        constellationStarCount.put(constellation.getName(), stars.size());
    }

    // method that deletes a star 
    public static void removeStar(String name, List<Star> starList)
    {
        Star starRemove = null; 
        
        for (Star star : starList)
        {
            if (star.catalogName.equals(name))
            {
                starRemove = star; //if the star's name has been found in starList 
                                  //it's marked as a star that will be removed
                break;
            }
        }

        //a marked star is getting removed
        if (starRemove != null)
        {
            starList.remove(starRemove);
            updateCatalog(starRemove.constellation, starList);
        }
    }

    // method that finds supernovas (if there are any) and returns a list of them
    public static List<Star> Supernovas(List<Star> starsList)
    {
        List<Star> supernovas = new ArrayList<>();

        for (Star star : starsList)
        {
            if (star.mass > 1.44)
            {
                supernovas.add(star);
            }
        }
        return supernovas;
    }
}
