package fi.tmtemetm.sidechannelanalyzer.view;

import javafx.scene.Node;

import java.io.IOException;

/**
 * @author tmtemetm
 */
public interface FXPrototypeView<R extends Node, C> {
  R createRootNode(C controller) throws IOException;
}
