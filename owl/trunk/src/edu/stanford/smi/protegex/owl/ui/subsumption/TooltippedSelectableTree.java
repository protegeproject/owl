package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.ui.ParentChildNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.menu.OWLMenuProjectPlugin;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TooltippedSelectableTree extends SelectableTree {

    public TooltippedSelectableTree(Action doubleClickAction, LazyTreeRoot root) {
        super(doubleClickAction, root);
        final int oldDelay = ToolTipManager.sharedInstance().getDismissDelay();
        addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setDismissDelay(oldDelay);
            }
        });
        setToolTipText(""); // Dummy to initialize the mechanism
    }


    public String getToolTipText(MouseEvent event) {
        if (OWLMenuProjectPlugin.isProseActivated()) {
            int row = getRowForLocation(event.getX(), event.getY());
            if (row >= 0) {
                TreePath path = getPathForRow(row);
                if (path != null) {
                    Object last = path.getLastPathComponent();
                    Cls cls = null;
                    if (last instanceof ParentChildNode) {
                        ParentChildNode node = (ParentChildNode) path.getLastPathComponent();
                        cls = (Cls) node.getUserObject();
                    }
                    else if (last instanceof SubsumptionTreeNode) {
                        cls = ((SubsumptionTreeNode) last).getCls();
                    }
                    if (cls instanceof RDFSClass) {
                        ToolTipManager.sharedInstance().setDismissDelay(OWLTable.INFINITE_TIME);
                        return OWLUI.getOWLToolTipText((RDFSClass) cls);
                    }
                }
            }
        }
        return null;
    }
}
