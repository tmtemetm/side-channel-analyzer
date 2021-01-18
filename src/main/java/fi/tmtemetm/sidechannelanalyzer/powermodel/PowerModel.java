package fi.tmtemetm.sidechannelanalyzer.powermodel;

import fi.tmtemetm.sidechannelanalyzer.model.Byte2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.Double2DMatrix;

/**
 * @author tmtemetm
 */
@FunctionalInterface
public interface PowerModel {

  Double2DMatrix hypothesize(Byte2DMatrix plainTexts, int byteIndex);

}
