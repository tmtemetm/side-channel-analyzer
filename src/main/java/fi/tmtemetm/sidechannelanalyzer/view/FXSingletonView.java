package fi.tmtemetm.sidechannelanalyzer.view;

import javafx.scene.Node;

import java.io.IOException;

/**
 * @author tmtemetm
 */
public interface FXSingletonView<R extends Node> {
  R createRootNode() throws IOException;
}
