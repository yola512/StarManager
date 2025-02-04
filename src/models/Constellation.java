package src.models;

import java.io.Serializable;

public class Constellation implements Serializable {
    private String name;

    // constructor
    public Constellation(String name) {
        this.name = name;
    }

    // name getter
    public String getName() {
        return name;
    }
   // didn't use setter for names bc they're determined by IAU and don't change over time
}
