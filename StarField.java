/**
 * First attempt at a star field screensaver (in honor of Star Wars).
 * 
 * TODO: Stars could start from different locations (currently all start at the center)
 *       Deal with occasional flashes (or not)
 * 
 * @author A. Jacoby (20 Dec 2015)
 */
public class StarField {
  // Note on time: Units of time are roughly 10ms ticks since that's how long it
  // sleeps between updating t by 1 and redrawing everything.
  
  // Indices in stars' data array:
  private static final int X  = 0; // Index of X coordinate of star
  private static final int Y  = 1; // Index of Y coordinate of star
  private static final int TH = 2; // Index of angle (THeta) of star's path (0..2pi)
  private static final int T0 = 3; // Index of start Time of star's journey
  private static final int S  = 4; // Index of relative Speed of star
  private static final int A  = 5; // Index of relative size (Amplitude) of star
  // Misc constants
  private static final double FIELD_RADIUS = 100; // Radius of star field
  private static final double MIN_SIZE = 0.005; // Minimum pen size to draw a star
  private static final int WIN_WIDTH = 1400; // pixels
  private static final int WIN_HEIGHT = 800; // pixels
  
  /** Defaults to 100 stars, but number may be specified on the command line. */
  public static void main(String[] args) throws Exception {
    int stars = (args.length > 0)? Integer.parseInt(args[0]) : 1000;
    showField(stars);
  }
  
  /** Draws a star field with numStars in it. */
  public static void showField(int numStars) throws Exception {
    /*
     * Draws each star on a line passing through the origin.
     * There are always numStars stars in the field: as soon as one leaves, a new one takes its place.
     */
    double[][] data = new double[numStars][6]; // stores data on each star currently in the field
    // Initialize star field
    for (int s = 0; s < numStars; s++) {
      birthNewStar(data[s], 0);
    }
    // Set up drawing window
    StdDraw.setCanvasSize(WIN_WIDTH, WIN_HEIGHT);
    StdDraw.setScale(-FIELD_RADIUS, FIELD_RADIUS);
    StdDraw.clear(StdDraw.BLACK);
    StdDraw.setPenColor(StdDraw.WHITE);
    StdDraw.show(0); // Turn on animation mode
    // Run the simulation!
    for (int t = 0; true; t++) { // infinite loop
      StdDraw.clear(StdDraw.BLACK);
      for (int s = 0; s < numStars; s++) {
        if (t >= data[s][T0]) { drawStar(data[s]); }
      }
      StdDraw.show(0); // Flush all the drawing to the screen
      Thread.sleep(10);
      for (int s = 0; s < numStars; s++) {
        updateCoordinates(data[s], t);
        replaceIfNecessary(data[s], t);
      }
    }
  }

  /** Initialize new slope, start time, speed, size, and coordinates for a star. */
  private static void birthNewStar(double[] starInfo, int t) {
    starInfo[TH] = 2*Math.PI*Math.random(); // angle of star's path from origin
    starInfo[T0] = t + 200 * Math.random(); // start time (randomly chosen from next 2 seconds)
    starInfo[S] = 0.8 + 0.4*Math.random(); // relative speed (0.8 - 1.2)
    starInfo[A] = 0.5 + 2*Math.random(); // relative size (0.5 - 2.5)
    updateCoordinates(starInfo, t);
    // TODO: Replace magic numbers above with named constants
  }

  /** Updates x and y coordinates of star if t >= start time of star. */
  private static void updateCoordinates(double[] starInfo, int t) {
    final double deltaT = t - starInfo[T0];
    if (deltaT < 0) { return; } // star hasn't started yet
    double amp = Math.pow(deltaT, starInfo[S]);
    starInfo[X] = Math.cos(starInfo[TH]) * amp;
    starInfo[Y] = Math.sin(starInfo[TH]) * amp;
  }
  
  /** Replaces the star with a new one if it's now off the screen. */
  private static void replaceIfNecessary(double[] starInfo, int t) {
    if (t < starInfo[T0]) { return; } // star hasn't even started yet
    if (Math.abs(starInfo[X]) > FIELD_RADIUS || Math.abs(starInfo[Y]) > FIELD_RADIUS) {
      birthNewStar(starInfo, t);
    }
  }
  
  /** Draws star. */
  private static void drawStar(double[] starInfo) {
    double percentOfTrip = hypot(starInfo[X], starInfo[Y])/FIELD_RADIUS;
    StdDraw.setPenRadius(MIN_SIZE * starInfo[A]*percentOfTrip);
    StdDraw.point(starInfo[X], starInfo[Y]);
  }
  
  /** Returns the length of the hypotenuse of a right triangle with legs x and y. */
  private static double hypot(double x, double y) {
    return Math.sqrt(x*x + y*y); // Note: Math.hypot is more accurate but slower
  }
  
}
