package dunbar;

import java.util.Random;

/**
 *
 * @author MultiTool
 */
public class Base {
  public static Random RandomGenerator = new Random();
  public static double Fudge = 0.00000000001;
  public static double MaxCorr = 0.0;
  public static void CheckCorr(double Corr){
    if (MaxCorr<Corr){MaxCorr=Corr;}
  }
}
