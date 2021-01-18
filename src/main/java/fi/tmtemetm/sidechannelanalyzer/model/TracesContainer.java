package fi.tmtemetm.sidechannelanalyzer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author tmtemetm
 */
@Getter
@RequiredArgsConstructor
public class TracesContainer {
  protected final Double2DMatrix traces;
  protected final int[] times;
  protected final List<Integer> indices;
  protected final int start;
  protected final int end;

  public int getNumberOfTraces() {
    return traces.getRows();
  }

  public int getTraceLength() {
    return traces.getColumns();
  }

  public double[] getTrace(int i) {
    return traces.getData()[i];
  }

  public int getIndex(int i) {
    return indices.get(i);
  }

  public int getTime(int i) {
    return times[i];
  }
}
