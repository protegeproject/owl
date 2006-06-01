package edu.stanford.smi.protegex.owl.ui.components;

import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Mar-2006
 */
public class TooltippedSelectableList extends SelectableList {

    public TooltippedSelectableList() {
        super();
        final int oldDelay = ToolTipManager.sharedInstance().getDismissDelay();
        addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setDismissDelay(oldDelay);
            }
        });
        setToolTipText(""); // Dummy to initialize the mechanism
    }


    public String getToolTipText(MouseEvent event) {
        int row = locationToIndex(event.getPoint());
        Object o = getModel().getElementAt(row);
        if (o != null && o instanceof RDFResource) {
            ToolTipManager.sharedInstance().setDismissDelay(OWLTable.INFINITE_TIME);
            return OWLUI.getOWLToolTipText((RDFResource) o);
        }
        return null;
    }
}