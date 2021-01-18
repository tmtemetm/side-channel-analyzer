package fi.tmtemetm.sidechannelanalyzer.service.impl;

import fi.tmtemetm.sidechannelanalyzer.model.Double2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.service.CorrelationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.TDistribution;
import org.springframework.stereotype.Service;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * @author tmtemetm
 */
@Service
public class CorrelationServiceImpl implements CorrelationService {

  @Override
  public Double2DMatrix computeColumnCorrelations(Double2DMatrix matrix1, Double2DMatrix matrix2) {
    int rows = matrix1.getRows();
    int columns1 = matrix1.getColumns();
    int columns2 = matrix2.getColumns();
    double[][] data1 = matrix1.getData();
    double[][] data2 = matrix2.getData();
    double[][] result = new double[columns1][columns2];

    double[] negativeMeans1 = computeNegativeColumnMeans(data1, rows, columns1);
    double[] negativeMeans2 = computeNegativeColumnMeans(data2, rows, columns2);
    double[] deviations1 = computeColumnStandardDeviations(data1, negativeMeans1, rows, columns1);
    double[] deviations2 = computeColumnStandardDeviations(data2, negativeMeans2, rows, columns2);

    CorrelationFirstColumnRecursiveAction action = new CorrelationFirstColumnRecursiveAction(data1, data2,
            negativeMeans1, negativeMeans2, deviations1, deviations2, result, rows, columns2, 0, columns1);
    ForkJoinPool.commonPool().execute(action);
    action.join();

    return new Double2DMatrix(result, columns1, columns2);
  }

  protected double[] computeNegativeColumnMeans(double[][] data, int rows, int columns) {
    double[] negativeMeans = new double[columns];
    for (int col = 0; col < columns; col++) {
      double sum = 0;
      for (int row = 0; row < rows; row++) {
        sum += data[row][col];
      }
      negativeMeans[col] = - sum / rows;
    }
    return negativeMeans;
  }

  protected double[] computeColumnStandardDeviations(double[][] data, double[] negativeMeans, int rows, int columns) {
    double[] stds = new double[columns];
    for (int col = 0; col < columns; col++) {
      double sumOfSquareDifferences = 0;
      for (int row = 0; row < rows; row++) {
        double diff = data[row][col] + negativeMeans[col];
        sumOfSquareDifferences += diff * diff;
      }
      stds[col] = Math.sqrt(sumOfSquareDifferences);
    }
    return stds;
  }


  @Override
  public double computeTwoTailedPValue(double correlation, int n) {
    TDistribution tDistribution = new TDistribution(n - 2);
    double testStatistic = Math.abs(correlation * Math.sqrt((n - 2)/(1 - correlation * correlation)));
    return 2 * tDistribution.cumulativeProbability(-testStatistic);
  }

  @RequiredArgsConstructor
  protected static class CorrelationFirstColumnRecursiveAction extends RecursiveAction {
    protected static final int MAX_LENGTH = 100;

    protected final double[][] data1;
    protected final double[][] data2;
    protected final double[] negativeMeans1;
    protected final double[] negativeMeans2;
    protected final double[] deviations1;
    protected final double[] deviations2;
    protected final double[][] result;
    protected final int rows;
    protected final int columns2;
    protected final int start;
    protected final int end;

    @Override
    protected void compute() {
      if (end - start > MAX_LENGTH) {
        int middle = (start + end) / 2;
        ForkJoinTask.invokeAll(
                new CorrelationFirstColumnRecursiveAction(data1, data2, negativeMeans1, negativeMeans2, deviations1,
                        deviations2, result, rows, columns2, start, middle),
                new CorrelationFirstColumnRecursiveAction(data1, data2, negativeMeans1, negativeMeans2, deviations1,
                        deviations2, result, rows, columns2, middle, end)
        );
      } else {
        for (int col1 = start; col1 < end; col1++) {
          for (int col2 = 0; col2 < columns2; col2++) {
            double covariance = 0;
            for (int row = 0; row < rows; row++) {
              covariance += (data1[row][col1] + negativeMeans1[col1]) * (data2[row][col2] + negativeMeans2[col2]);
            }
            result[col1][col2] = covariance / (deviations1[col1] * deviations2[col2]);
          }
        }
      }
    }
  }
}
