/**
 * Second attempt at a star field screensaver (in honor of Star Wars).
 * <p/>
 * The first version simulated stars by having them move in straight lines out from
 * the center of the screen at random (but increasing) speeds and random (but increasing)
 * sizes.  This version will attempt an actual 3D simulation, with stars' locations fixed
 * in 3D space and a camera that moves along the z-axis.
 * <p/>
 * TODO: Figure out how the 2D projection should actually work: current implementation is
 * just a trial-and-error based approximation that seems to look pretty.
 * TODO: Add color to some stars, or different shapes/twinkle effects.
 * TODO: Add speed controls for the camera.
 * 
 * @author A. Jacoby (23 Dec 2015)
 */
public class StarField3D {
  // Note on time: Units of time are roughly 10ms ticks since that's how long it
  // sleeps between updating t by 1 and redrawing everything.
  
  // Indices in stars' data array:
  private static final int X  = 0; // Index of X coordinate of star (horizontal)
  private static final int Y  = 1; // Index of Y coordinate of star (vertical)
  private static final int Z  = 2; // Index of Z coordinate of star (depth)
  private static final int A  = 3; // Index of relative size (Amplitude) of star
  // Misc constants
  private static final double FIELD_RADIUS = 100; // Radius of view screen
  private static final double STAR_PEN_SIZE = 0.005; // Minimum pen size to draw a star
  private static final int WIN_WIDTH = 1400; // window width (pixels)
  private static final int WIN_HEIGHT = 800; // window height (pixels)
  
  /** Defaults to 1000 stars, but number may be specified on the command line. */
  public static void main(String[] args) throws Exception {
    int stars = (args.length > 0)? Integer.parseInt(args[0]) : 1000;
    showField(stars);
  }
  
  /** Draws a star field with numStars in it. */
  public static void showField(int numStars) throws Exception {
    /*
     * Draws stars in fixed locations as camera moves forward along the z axis.
     * There are always numStars stars in the field: as soon as one is passed on the z axis,
     * a new one takes its place.
     */
    double[][] data = new double[numStars][4]; // stores data on each star currently in the field (X, Y, Z, and A)
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
    // Run the simulation! Move z (camera position) forward through the field.
    for (int z = 0; true; z++) { // infinite loop
      StdDraw.clear(StdDraw.BLACK);
      for (int s = 0; s < numStars; s++) {
        drawStar(data[s], z);
      }
      StdDraw.show(0); // Flush all the drawing to the screen
      Thread.sleep(10);
      for (int s = 0; s < numStars; s++) {
        replaceIfNecessary(data[s], z);
      }
    }
  }

  /** Initialize new coordinates and size for a star, relative to current camera position z. */
  private static void birthNewStar(double[] starInfo, int z) {
    starInfo[X] = -20*FIELD_RADIUS + 40*FIELD_RADIUS*Math.random(); // Random x value between +-20*FIELD_RADIUS
    starInfo[Y] = -20*FIELD_RADIUS + 40*FIELD_RADIUS*Math.random(); // Random y value between +-20*FIELD_RADIUS
    starInfo[Z] = z + 3*FIELD_RADIUS*Math.random(); // Random z value up to 3*FIELD_RADIUS away from current camera location
    starInfo[A] = 0.5 + 2*Math.random(); // relative size (0.5 - 2.5)
    // TODO: Replace magic numbers above with named constants
  }

  /** Replaces the star with a new one if camera has passed it by. */
  private static void replaceIfNecessary(double[] starInfo, int z) {
    if (starInfo[Z] <= z) {
      birthNewStar(starInfo, z);
      // System.out.println("New star: z=" + z + ", star=" + java.util.Arrays.toString(starInfo));
    }
  }
  
  /** Draws star relative to given camera position on the z axis. */
  private static void drawStar(double[] starInfo, int z) {
    double deltaZ = starInfo[Z] - z; // z-distance between star and camera
    double distanceFactor = 1/Math.sqrt(1 + deltaZ); // Bigger as star gets closer to camera (max=1)
    double xProjection = starInfo[X]*distanceFactor;
    double yProjection = starInfo[Y]*distanceFactor;
    // Don't draw off-screen stars
    if (Math.abs(xProjection) > FIELD_RADIUS || Math.abs(yProjection) > FIELD_RADIUS) { return; }
    // Increase pen size based on star's amplitude and distance
    StdDraw.setPenRadius(STAR_PEN_SIZE * 5 * starInfo[A] * Math.pow(distanceFactor, 0.8));
    StdDraw.point(xProjection, yProjection);
  }
  
  /** Returns the length of the diagonal of a right rectangular prism with sides x, y, and z. */
  private static double hypot(double x, double y, double z) {
    return Math.sqrt(x*x + y*y + z*z);
  }
}
