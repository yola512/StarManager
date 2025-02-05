package src;

import src.models.*;
import src.utils.Hemisphere;
import java.util.List;
import java.util.Scanner;

import static src.models.Star.*;


public class Main {

    public static void main(String[] args) throws Exception {
        initializeStarCountMap();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n*********************************");
            System.out.println("*    Welcome to StarsManager!   *");
            System.out.println("*********************************");
            System.out.println("1. Display all Stars");
            System.out.println("2. Display Stars in selected Constellation");
            System.out.println("3. Add a new Star");
            System.out.println("4. Remove a Star");
            System.out.println("5. Display star coordinates");
            System.out.println("6. Filter stars by distance from Earth");
            System.out.println("7. Find Stars by temperature");
            System.out.println("8. Find Stars by absolute magnitude");
            System.out.println("9. Search for stars in a selected hemisphere");
            System.out.println("10. Find Supernovas");
            System.out.println("11. Save a Star to file");
            System.out.println("12. Exit program");

            System.out.println("\nChoose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewStars();
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 2:
                    System.out.println("Enter constellation name:");
                    String consName = scanner.nextLine();
                    displayContellationStars(consName);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 3:
                    // Name
                    System.out.println("Enter Star's name (3 uppercase letters and 4 digits):");
                    String name = scanner.nextLine();

                        if (!name.matches("[A-Z]{3}[0-9]{4}")) 
                        {
                            throw new IllegalArgumentException("Invalid star name format.");
                        }
                        
                    // checking if a star with that name already exist
                    List<Star> existingStars = Star.loadStarsFromFile();
                    boolean starExists = false;
                    for (Star star : existingStars)
                    {
                        if (star.getName().equals(name)) 
                        {
                            starExists = true;
                            break;
                        }
                    }
                    if (starExists) 
                    {
                        throw new IllegalArgumentException("A star with this name already exists! Please choose a different name.");
                    }
                
                    // Hemisphere (enum)
                    System.out.println("Enter hemisphere: <N/S>");
                    String hemisphereAbbr = scanner.nextLine();
                    Hemisphere hemisphere = Hemisphere.fromString(hemisphereAbbr);
                    if (hemisphere == null) {
                        throw new IllegalArgumentException("Invalid hemisphere. Choose <N> or <S>.");
                    }

                    // Declination
                    System.out.println("Enter Star's declination: ");
                    System.out.println("1) Degrees: (N: <0; 90>, S: <-90; 0) ");
                    int xx = scanner.nextInt();
                    if (hemisphere == Hemisphere.NORTHERN && (xx < 0 || xx > 90)) 
                    {
                        throw new IllegalArgumentException("For Northern Hemisphere, degrees must be between 0 and 90");
                    }
                    else if (hemisphere == Hemisphere.SOUTHERN && (xx < -90 || xx > 0))
                    {
                        throw new IllegalArgumentException("For Southern Hemisphere, degrees must be between -90 and 0.");
                    }
                    System.out.println("2) Minutes: <00; 60>");
                    int yy = scanner.nextInt();
                    if (yy < 0 || yy > 60) 
                    {
                        throw new IllegalArgumentException("Minutes must be between 0 and 60.");
                    }

                    System.out.println("3) Seconds: <00; 60>");
                    double zz = scanner.nextDouble();
                    if (zz < 0 || zz > 60) 
                    {
                        throw new IllegalArgumentException("Seconds must be between 0 and 60.");
                    }
                    
                    Declination declination = new Declination(xx, yy, zz);

                    // Right Ascension
                    System.out.println("Enter Star's right ascension: ");
                    System.out.println("1) Hours: <00; 24>");
                    int degrees = scanner.nextInt();
                    System.out.println("2) Minutes: <00; 60>");
                    int minutes = scanner.nextInt();
                    System.out.println("3) Seconds: <00; 60>");
                    double seconds = scanner.nextDouble();
                    if (degrees < 0 || degrees >= 24 || minutes < 0 || minutes >= 60 ||
                    seconds < 0 || seconds >= 60) 
                    {
                         throw new IllegalArgumentException("Invalid right ascension values.");
                    }        
                    RightAscension rightAscension = new RightAscension(degrees, minutes, seconds);

                    // Constellation
                    System.out.println("Enter constellation:");
                    scanner.nextLine();
                    String constellationName = scanner.nextLine();
                    Constellation constellation = new Constellation(constellationName);

                    // Apparent Magnitude
                    System.out.println("Enter Star's apparent magnitude <-26.74; 15.00>:");
                    double apparentMagnitude = scanner.nextDouble();
                    if (apparentMagnitude < -26.74 || apparentMagnitude > 15.00) 
                    {
                        throw new IllegalArgumentException("Invalid apparent magnitude.");
                    }

                    // Distance in light years
                    System.out.println("Enter Star's distance (in light years):");
                    double distance = scanner.nextDouble();

                    scanner.nextLine();

                    // Temperature
                    System.out.println("Enter Star's temperature in °C: (min 2000)");
                    double temperature = scanner.nextDouble();
                    if (temperature < 2000) 
                    {
                        throw new IllegalArgumentException("Temperature must be at least 2000°C.");
                    }

                    // Mass
                    System.out.println("Enter Star's mass (relative to solar mass, e.g 1.3) <0.1; 50>:");
                    double mass = scanner.nextDouble();
                    if (mass < 0.1 || mass > 50) 
                    {
                        throw new IllegalArgumentException("Mass must be between 0.1 and 50 solar masses.");
                    }
              

                    try {
                        Star star = new Star(name, hemisphere, declination, rightAscension, constellation, apparentMagnitude, distance, temperature, mass);
                        System.out.println("\nSTAR HAS BEEN CREATED :)");
                        System.out.println("\nSTAR INFO:");
                        System.out.println("* Name: " + star.getName());
                        System.out.println("* Catalog name: " + star.getCatalogName());
                        System.out.println("* Hemisphere: " + star.getHemisphere());
                        System.out.println("* Declination: " + star.getDeclination());
                        System.out.println("* Right ascension: " + star.getRightAscension());
                        System.out.println("* Constellation: " + star.getConstellation());
                        System.out.println("* Apparent magnitude: " + star.getApparentMagnitude());
                        System.out.println("* Absolute magnitude: " + star.getAbsoluteMagnitude());
                        System.out.println("* Distance: " + star.getDistance() + " light years");
                        System.out.println("* Temperature: " + star.getTemperature() + "°C");
                        System.out.println("* Mass: " + star.getMass() + " solar mass");

                        initializeStarCountMap();

                        System.out.println("\n!!! DON'T FORGET TO SAVE YOUR STAR TO FILE !!! (11. in menu :)) ");

                        System.out.println("Press Enter to continue...");
                        scanner.nextLine();
                    } 
                    catch (IllegalArgumentException e) 
                    {
                        System.out.println("Error occurred: " + e.getMessage());
                    }
                     
                    break;
                

                case 4:
                    System.out.println("Enter catalog name of the star you want to remove: ");
                    String catalogName = scanner.nextLine();
                    removeStar(catalogName);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 5:
                    System.out.println("Enter name of the star: ");
                    String name1 = scanner.nextLine();
                    getStarCoordinates(name1);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 6:
                    System.out.println("\nEnter distance from Earth (in parsecs, up to 2 spaces after coma): ");
                    double distanceParsecs = scanner.nextDouble();
                    System.out.println("\nFound stars in chosen distance: ");
                    findStarByDistance(distanceParsecs);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 7:
                    System.out.println("\nEnter min temperature: ");
                    double minTemp = scanner.nextDouble();
                    System.out.println("Enter max temperature: ");
                    double maxTemp = scanner.nextDouble();
                    System.out.println("\nFound stars in chosen temperature interval: ");
                    findStarByTemperature(minTemp, maxTemp);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 8:
                    System.out.println("\nEnter min absolute magnitude: ");
                    double minMag = scanner.nextDouble();
                    System.out.println("Enter max absolute magnitude: ");
                    double maxMag = scanner.nextDouble();
                    System.out.println("\nFound stars in chosen absolute magnitude interval: ");
                    findStarByMagnitude(minMag, maxMag);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 9:
                    System.out.println("\nEnter hemisphere: ");
                    String hemisphereName = scanner.nextLine();
                    System.out.println("\nFound stars in chosen hemisphere: ");
                    findStarByHemisphere(hemisphereName);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 10:
                    System.out.println("\nFound supernovas: ");
                    findSupernovas();
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 11:
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
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                case 12:
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