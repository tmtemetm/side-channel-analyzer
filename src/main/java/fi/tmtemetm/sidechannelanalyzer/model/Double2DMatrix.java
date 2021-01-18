package fi.tmtemetm.sidechannelanalyzer.model;

import lombok.ToString;
import lombok.Value;

/**
 * @author tmtemetm
 */
@Value
@ToString(onlyExplicitlyIncluded = true)
public class Double2DMatrix {
  double[][] data;
  int rows, columns;

  public int getTotal() {
    return rows * columns;
  }
}
