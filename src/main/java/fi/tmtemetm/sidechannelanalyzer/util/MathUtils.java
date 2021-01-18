package fi.tmtemetm.sidechannelanalyzer.util;

/**
 * @author tmtemetm
 */
public abstract class MathUtils {

  public static int ceilingDivision(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  public static long ceilingDivision(long dividend, long divisor) {
    return (dividend + divisor - 1) / divisor;
  }

}
