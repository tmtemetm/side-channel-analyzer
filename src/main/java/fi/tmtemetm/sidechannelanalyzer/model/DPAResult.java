package fi.tmtemetm.sidechannelanalyzer.model;

import lombok.Value;

import java.util.List;

/**
 * @author tmtemetm
 */
@Value
public class DPAResult {

  int byteIndex;
  List<KeyByteHypothesis> byteHypotheses;

  @Value
  public static class KeyByteHypothesis implements Comparable<KeyByteHypothesis> {
    byte keyByte;
    int time;
    double correlation, pValue;

    @Override
    public int compareTo(KeyByteHypothesis o) {
      return Double.compare(o.correlation, correlation);
    }
  }

}
