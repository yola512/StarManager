package src.models;
import src.utils.Hemisphere;

import java.lang.Math;

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




}
