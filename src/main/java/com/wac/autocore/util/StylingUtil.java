package com.wac.autocore.util;

import javafx.scene.Node;
import javafx.scene.Parent;

public class StylingUtil {
    /**
     * Removes the is-selected classname from each sibling node with the given styleClass. The sibling nodes
     * need to be in the same parent as selectedNode. Then the selected node gets the styleClass is-selected.
     *
     * @param selectedNode node to get the StyleClass is-selected
     * @param styleClassName is-selected will be removed from the nodes in the parent of selectedNode
     *                       with this StyleClass
     */
    public static void setSelected(Node selectedNode, String styleClassName) {
        Parent parent = selectedNode.getParent();
        //Remove is-selected styling on other nodes
        if (parent != null) {
            parent.getChildrenUnmodifiable().forEach(node -> {
                if (node.getStyleClass().contains(styleClassName)) {
                    node.getStyleClass().remove("is-selected");
                }
            });
        }

        selectedNode.getStyleClass().add("is-selected");
    }
}
