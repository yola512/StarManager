package src.utils;

public enum Hemisphere {
    NORTHERN("N"),
    SOUTHERN("S");

    private final String abbreviation;

    // constructor
    Hemisphere(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    // GETTER
    public String getAbbreviation() {
        return abbreviation;
    }

    // hemisphere validation
    public static Hemisphere fromString(String input) {
        if (input == null){
            throw new IllegalArgumentException("Hemisphere must be either Northern or Southern. It can't be null");
        }
        switch (input.toUpperCase()) {
            case "N", "NORTHERN", "NORTH" -> {return NORTHERN;}
            case "S", "SOUTHERN", "SOUTH" -> {return SOUTHERN;}
            // if previous don't match input:
            default -> throw new IllegalArgumentException("Invalid hemisphere: " + input);
        }
    }
}
