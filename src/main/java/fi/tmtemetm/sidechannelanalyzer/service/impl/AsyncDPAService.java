package fi.tmtemetm.sidechannelanalyzer.service.impl;

import fi.tmtemetm.sidechannelanalyzer.model.Byte2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.Double2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.DPAResult;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import fi.tmtemetm.sidechannelanalyzer.powermodel.PowerModel;
import fi.tmtemetm.sidechannelanalyzer.service.CorrelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

/**
 * @author tmtemetm
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncDPAService {
  protected final CorrelationService correlationService;

  @Async
  public CompletableFuture<DPAResult> analyze(TracesContainer tracesContainer, Byte2DMatrix filteredPlainTexts,
                                              PowerModel powerModel, int byteIndex) {
    Double2DMatrix traces = tracesContainer.getTraces();
    int traceLength = traces.getColumns();

    Double2DMatrix correlations = correlationService.computeColumnCorrelations(powerModel.hypothesize(
            filteredPlainTexts, byteIndex), traces);
    double[][] correlationsData = correlations.getData();

    int nofKeys = correlations.getRows();
    Queue<DPAResult.KeyByteHypothesis> hypothesisPriorityQueue = new PriorityQueue<>(nofKeys);

    for (int key = 0; key < nofKeys; key++) {
      int maxTime = 0;
      double maxCorrelation = 0;
      for (int timeIndex = 0; timeIndex < traceLength; timeIndex++) {
        double correlation = Math.abs(correlationsData[key][timeIndex]);
        if (correlation > maxCorrelation) {
          maxCorrelation = correlation;
          maxTime = timeIndex;
        }
      }
      double singlePValue = correlationService.computeTwoTailedPValue(maxCorrelation, traces.getRows());
      double adjustedPValue = computeAdjustedPValue(singlePValue, correlations.getTotal());
      hypothesisPriorityQueue.add(new DPAResult.KeyByteHypothesis((byte) key, tracesContainer.getTime(maxTime),
              maxCorrelation, adjustedPValue));
    }
    List<DPAResult.KeyByteHypothesis> keyByteHypotheses = new ArrayList<>(hypothesisPriorityQueue.size());
    while (!hypothesisPriorityQueue.isEmpty()) {
      keyByteHypotheses.add(hypothesisPriorityQueue.poll());
    }
    return CompletableFuture.completedFuture(new DPAResult(byteIndex, keyByteHypotheses));
  }

  private double computeAdjustedPValue(double pValue, int totalPairs) {
    return  1 - Math.pow(1 - pValue, totalPairs);
  }

}
