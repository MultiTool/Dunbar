package dunbar;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author MultiTool
 */
public class Base {
  public static Random RandomGenerator = new Random();
  public static double Fudge = 0.00000000001;
  public static double MaxCorr = 0.0;
  public static void CheckCorr(double Corr) {
    if (MaxCorr < Corr) {
      MaxCorr = Corr;
    }
  }
  /* ********************************************************************************* */
  public static Color ToBlackBody(double Fraction) {
    if (Fraction < 0.5) {
      Fraction *= 2;
      return new Color(1.0f, (float) (1.0 - Fraction), 0);
    } else {
      Fraction = Math.min((Fraction - 0.5) * 2, 1.0);
      return new Color((float) (1.0 - Fraction), 0.0f, (float) Fraction);
    }
  }
  /* ********************************************************************************* */
  public static Color ToRainbow(double Fraction) {
    if (Fraction < 0.5) {
      Fraction *= 2;
      return new Color((float) (1.0 - Fraction), (float) Fraction, 0);
    } else {
      Fraction = Math.min((Fraction - 0.5) * 2, 1.0);
      return new Color(0, (float) (1.0 - Fraction), (float) Fraction);
    }
  }
  /* ********************************************************************************* */
  public static Color ToHeat(double Fraction) {
    return new Color((float) (1.0 - Fraction), 0, (float) Fraction);
  }
  /* ********************************************************************************* */
  public static Color ToGreenHeat(double Fraction) {
    return new Color((float) (1.0 - Fraction), (float) Fraction, 0);
  }
}
