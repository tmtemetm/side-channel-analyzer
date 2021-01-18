package fi.tmtemetm.sidechannelanalyzer.service;

import fi.tmtemetm.sidechannelanalyzer.model.Byte2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.BitSet;

/**
 * @author tmtemetm
 */
public interface TraceInputService {

  int readTotalNumberOfTraces(Resource resource, int traceLength) throws IOException;
  TracesContainer readTraces(Resource resource, int traceLength, int start, int end, BitSet includeTraces)
          throws IOException;
  Byte2DMatrix readPlainTexts(Resource resource) throws IOException;

}
