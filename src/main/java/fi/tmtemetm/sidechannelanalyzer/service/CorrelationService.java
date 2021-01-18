package fi.tmtemetm.sidechannelanalyzer.service;

import fi.tmtemetm.sidechannelanalyzer.model.Double2DMatrix;

/**
 * @author tmtemetm
 */
public interface CorrelationService {

  Double2DMatrix computeColumnCorrelations(Double2DMatrix matrix1, Double2DMatrix matrix2);
  double computeTwoTailedPValue(double correlation, int n);

}
