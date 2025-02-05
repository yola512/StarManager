package src.models;

import java.io.Serializable;
import java.util.Objects;

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

   // overriding equals() to compare constellations based on the name
    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Constellation that = (Constellation) obj;
        return Objects.equals(name, that.name);  // compare names
    }

    // overriding hashCode() for consistency with equals()
    @Override
    public int hashCode() 
    {
        return Objects.hash(name);
    }
}
