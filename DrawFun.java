/**
 * Spinning star with oscillating concentric circles.
 * 
 * @author A. Jacoby (18 Dec 2015)
 */
public class DrawFun {
  public static void main(String[] args) throws Exception {
    int i = 0;
    final int numParts = (args.length == 0)? 64 : Integer.parseInt(args[0]); // Slow down for more parts!
    // final double slowDown = 1/800.0; // Smaller number -> slower
    final double slowDown = 1/(100*(Math.log(numParts)+2)); // Automatically calculate a reasonable slowdown
    while (true) {
      for (double deltaTheta = -Math.PI/2; deltaTheta < Math.PI/2; deltaTheta += Math.PI/numParts) {
        double sinI = Math.sin(i*slowDown + deltaTheta);
        double cosI = Math.cos(i*slowDown + deltaTheta);
        StdDraw.line(0.5 + 0.5*cosI, 0.5 + 0.5*sinI,
                     0.5 - 0.5*cosI, 0.5 - 0.5*sinI);
        StdDraw.circle(0.5, 0.5, 0.25 + 0.25*sinI);
      }
      StdDraw.show(0);
      Thread.sleep(10); // Pause for 10 ms
      i++;
      StdDraw.clear();
    }
  }
}
