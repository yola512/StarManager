package src.models;

import java.io.Serializable;

public class Declination implements Serializable {
   private int xx;
   private int yy;
   private double zz;

   // constructor
   public Declination(int xx, int yy, double zz) {
       // xx - degrees (angles)
       if (xx < 0 || xx > 90)
       {
           throw new IllegalArgumentException("Degrees must be values between 0 and 90");
       }
       this.xx = xx;
       // yy - minutes
       if (yy < 0 || yy >= 60) {
           throw new IllegalArgumentException("Minutes must be values between 0 and 60");
       }
       this.yy = yy;
       // zz.zz - seconds
       if (zz < 0 || zz >= 60) {
           throw new IllegalArgumentException("Seconds must be values between 0 and 60");
       }
       this.zz = zz;
   }

   // GETTERS
   // xx - degrees (angles)
   public int getXX() {
       return xx;
   }

   // yy - minutes
   public int getYY() {
       return yy;
   }

   // zz.zz - seconds
   public double getZZ() {
       return zz;
   }

   // toString()
   @Override
    public String toString() {
       return String.format("%d° %d' %.2f''", getXX(), getYY(), getZZ());
   }
}
