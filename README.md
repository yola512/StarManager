# Star Manager

## Overview
This project implements a Star class to model celestial stars with various astronomical properties. The class includes attributes, validation rules, and functionalities for managing a catalog of stars, searching for stars based on specific criteria, and handling catalog updates when stars are removed.


## Star Attributes
- **Name:** 3 uppercase letters followed by 4 digits (e.g., ABC1234).
- **Catalog Name:** Greek letter followed by constellation name (e.g., alpha Orion).
- **Declination:** Measured in degrees, minutes, and seconds.
  - **Northern Hemisphere:** 0° to 90°
  - **Southern Hemisphere:** 0° to -90°
- **Right Ascension:** Measured in hours, minutes, and seconds (00h to 24h).
- **Apparent Magnitude:**
  - Used to determine starlight expressed in magnitude units. Lower value means the star is brighter.
  - A measure of how bright a star would be if it were seen from Earth.
  - Ranges from -26.74 to 15.00.
- **Absolute Magnitude:**
  - A measure of how bright a star would be if it were seen from a standard distance
  - Calculated using:
    - _**M = m - 5*log10(r) + 5**_
    - where **m** is the observed magnitude and **r** is the distance in parsecs **(1 parsec = 3.26 light years)**.
- **Distance:** Given in light years.
- **Constellation:** The constellation the star belongs to.
- **Hemisphere:** Northern or Southern.
- **Temperature:** Minimum 2000°C (no upper limit).
- **Mass:** Between 0.1 and 50 solar masses.

## Functionalities
1. Adding a New Star.
2. Removing a Star based on its catalog name.
   - When a star is removed, all subsequent stars in the same constellation are renamed accordingly (e.g., beta Orion becomes alpha Orion).
   - Removes .obj file related to the Star.
4. Displaying all Stars.
5. Searching Stars based on preferences:
   - Find all stars in a specific _constellation_.
   - Find stars within a given _distance from Earth_ (in parsecs).
   - Find stars within a given _temperature range_.
   - Find stars within a given _absolute magnitude range_.
   - Find stars from a specific _hemisphere_.
   - Identify potential _supernovas_ (stars exceeding 1.44 solar masses, known as the Chandrasekhar limit).
6. Saving Star to .obj file.
   - All Stars (.obj files) are saved in src/data/stars folder.

## Possible Future Enhancements
  - Implement GUI for user interaction.

## How to use?
1. Clone repository.
2. Make sure you have the right Java version installed. (it will work with JDK 21 and higher) - we used Eclipse Temurin 21.0.5
3. Use IDE to run the program. (e.g. IntelliJ IDEA/Eclipse)
4. Run Main.java.
5. Have fun :)
