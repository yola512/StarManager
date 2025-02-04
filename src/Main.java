package src;

import src.models.*;
import src.models.Constellation;
import src.utils.Hemisphere;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static src.models.Star.*;


public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n*********************************");
            System.out.println("*    Welcome to StarsManager!   *");
            System.out.println("*********************************");
            System.out.println("1. Display all Stars");
            System.out.println("2. Display Stars in selected Constellation");
            System.out.println("3. Add a new Star");
            System.out.println("4. Remove a Star");
            System.out.println("5. Filter stars by distance from Earth");
            System.out.println("6. Find Stars by temperature");
            System.out.println("7. Find Stars by absolute magnitude");
            System.out.println("8. Search for stars in a selected hemisphere");
            System.out.println("9. Find Supernovas");
            System.out.println("10. Save a Star to file");
            System.out.println("11. Exit program");

            System.out.println("\nChoose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewStars();
                    break;
                case 2:
                    System.out.println("Enter constellation name:");
                    String consName = scanner.nextLine();
                    displayContellationStars(consName);
                    break;
                case 3:
                    // Name
                    System.out.println("Enter Star's name (3 uppercase letters and 4 digits):");
                    String name = scanner.nextLine();

                    // Declination
                    System.out.println("Enter Star's declination: ");
                    System.out.println("1) Degrees: ");
                    int xx = scanner.nextInt();
                    System.out.println("2) Minutes: ");
                    int yy = scanner.nextInt();
                    System.out.println("3) Seconds: ");
                    double zz = scanner.nextDouble();
                    Declination declination = new Declination(xx, yy, zz);

                    // Right Ascension
                    System.out.println("Enter Star's right ascension: ");
                    System.out.println("1) Hours: ");
                    int degrees = scanner.nextInt();
                    System.out.println("2) Minutes: ");
                    int minutes = scanner.nextInt();
                    System.out.println("3) Seconds: ");
                    double seconds = scanner.nextDouble();
                    RightAscension rightAscension = new RightAscension(degrees, minutes, seconds);

                    // Apparent Magnitude
                    System.out.println("Enter Star's apparent magnitude <-26.74; 15.00>:");
                    double apparentMagnitude = scanner.nextDouble();

                    // Distance in light years
                    System.out.println("Enter Star's distance (in light years):");
                    double distance = scanner.nextDouble();

                    scanner.nextLine();

                    // Constellation
                    System.out.println("Enter constellation:");
                    String constellationName = scanner.nextLine();
                    Constellation constellation = new Constellation(constellationName);

                    // Hemisphere (enum)
                    System.out.println("Enter hemisphere:");
                    String hemisphereAbbr = scanner.nextLine();
                    Hemisphere hemisphere = Hemisphere.fromString(hemisphereAbbr);

                    // Temperature
                    System.out.println("Enter Star's temperature in °C:");
                    double temperature = scanner.nextDouble();

                    // Mass
                    System.out.println("Enter Star's mass (relative to solar mass, e.g 1.3) <0.1; 50>:");
                    double mass = scanner.nextDouble();

                    try {
                        Star star = new Star(name, declination, rightAscension, apparentMagnitude, distance, constellation,
                                hemisphere, temperature, mass);
                        System.out.println("\nSTAR HAS BEEN CREATED :)");
                        System.out.println("\nSTAR INFO:");
                        System.out.println("* Name: " + star.getName());
                        System.out.println("* Catalog name: " + star.getCatalogName());
                        System.out.println("* Declination: " + star.getDeclination());
                        System.out.println("* Right ascension: " + star.getRightAscension());
                        System.out.println("* Apparent magnitude: " + star.getApparentMagnitude());
                        System.out.println("* Absolute magnitude: " + star.getAbsoluteMagnitude());
                        System.out.println("* Distance: " + star.getDistance() + " light years");
                        System.out.println("* Constellation: " + star.getConstellation());
                        System.out.println("* Hemisphere: " + star.getHemisphere());
                        System.out.println("* Temperature: " + star.getTemperature() + "°C");
                        System.out.println("* Mass: " + star.getMass() + " solar mass");

                        System.out.println("\n!!! DON'T FORGET TO SAVE YOUR STAR TO FILE !!! (10. in menu :)) ");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error occurred: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("Enter catalog name of the star you want to remove: ");
                    String catalogName = scanner.nextLine();
                    removeStar(catalogName);
                    break;
                case 5:
                    System.out.println("\nEnter distance from Earth (in parsecs, up to 2 spaces after coma): ");
                    double distanceParsecs = scanner.nextDouble();
                    System.out.println("\nFound stars in chosen distance: ");
                    findStarByDistance(distanceParsecs);
                    break;
                case 6:
                    System.out.println("\nEnter min temperature: ");
                    double minTemp = scanner.nextDouble();
                    System.out.println("Enter max temperature: ");
                    double maxTemp = scanner.nextDouble();
                    System.out.println("\nFound stars in chosen temperature interval: ");
                    findStarByTemperature(minTemp, maxTemp);
                    break;
                case 7:
                    System.out.println("\nEnter min absolute magnitude: ");
                    double minMag = scanner.nextDouble();
                    System.out.println("Enter max absolute magnitude: ");
                    double maxMag = scanner.nextDouble();
                    System.out.println("\nFound stars in chosen absolute magnitude interval: ");
                    findStarByMagnitude(minMag, maxMag);
                    break;
                case 8:
                    System.out.println("\nEnter hemisphere: ");
                    String hemisphereName = scanner.nextLine();
                    System.out.println("\nFound stars in chosen hemisphere: ");
                    findStarByHemisphere(hemisphereName);
                    break;
                case 9:
                    System.out.println("\nFound supernovas: ");
                    findSupernovas();
                    break;
                case 10:
                    System.out.println("\nWhich Star would you like to save? (enter Star's name): ");
                    String sName = scanner.nextLine();

                    List<Star> stars = loadStarsFromFile();

                    Star foundStar = null;
                    for (Star star : stars) {
                        if (star.getName().equalsIgnoreCase(sName)) {
                            foundStar = star;
                            break;
                        }
                    }
                    if (foundStar != null) {
                        Star.saveStarToFile(foundStar);
                        System.out.println("\nSelected star has been saved :)");
                    } else {
                        System.out.println("\nStar not found. You have to create a star first.");
                    }

                    break;
                case 11:
                    System.out.println("\nSee you soon!");
                    System.out.println("\nExiting StarManager...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Incorrect choice.");
            }

        }
    }
}