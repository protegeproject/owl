package edu.stanford.smi.protegex.owl.ui.properties.range;

import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.metaproject.impl.OperationImpl;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDataRangeComponent extends JComponent {

    private Action createAction = new AbstractAction("Create value...", OWLIcons.getAddIcon()) {
        public void actionPerformed(ActionEvent e) {
            createValue();
        }
    };

    private RDFSDatatype datatype;

    private Action deleteAction = new AbstractAction("Delete selected value...", OWLIcons.getDeleteIcon()) {
        public void actionPerformed(ActionEvent e) {
            deleteSelectedValue();
        }
    };

    private JList list;

    private DefaultListModel listModel;

    private OWLRangeWidget rangeWidget;


    public OWLDataRangeComponent(OWLRangeWidget rangeWidget) {
        this.rangeWidget = rangeWidget;
        listModel = new DefaultListModel();
        list = new JList(listModel);
        setLayout(new BorderLayout());
        LabeledComponent lc = new LabeledComponent("Allowed values", new JScrollPane(list));
        lc.addHeaderButton(createAction);
        lc.addHeaderButton(deleteAction);
        add(BorderLayout.CENTER, lc);
        deleteAction.setEnabled(false);
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActions(true);
            }
        });
    }


    private void createValue() {
        String newValue = ProtegeUI.getModalDialogFactory().showInputDialog(this, "Enter a new " + datatype.getBrowserText() + " literal", null);
        if (newValue != null) {
            RDFProperty property = rangeWidget.getEditedProperty();
            newValue = newValue.trim();
            if (newValue.length() > 0) {
                OWLModel owlModel = property.getOWLModel();
                OWLDataRange newDataRange = null;
                RDFSLiteral newLiteral = owlModel.createRDFSLiteral(newValue, datatype);
                if (property.getRange() instanceof OWLDataRange) {
                    OWLDataRange dataRange = (OWLDataRange) property.getRange();
                    java.util.List values = dataRange.getOneOfValueLiterals();
                    RDFSLiteral[] newLiterals = new RDFSLiteral[values.size() + 1];
                    Iterator it = values.iterator();
                    for (int i = 0; it.hasNext(); i++) {
                        RDFSLiteral oldValue = (RDFSLiteral) it.next();
                        if (newLiteral.equals(oldValue)) {
                            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                    "The value " + newValue + " is already among the values.");
                            return;
                        }
                        newLiterals[i] = oldValue;
                    }
                    newLiterals[newLiterals.length - 1] = newLiteral;
                    newDataRange = owlModel.createOWLDataRange(newLiterals);
                }
                else {
                    newDataRange = owlModel.createOWLDataRange(new RDFSLiteral[]{
                            newLiteral
                    });
                }
                property.setRange(newDataRange);
            }
        }
    }


    private void deleteSelectedValue() {
        int index = list.getSelectedIndex();
        if (index >= 0) {
            RDFProperty property = rangeWidget.getEditedProperty();
            OWLModel owlModel = (OWLModel) property.getOWLModel();
            OWLDataRange oldDataRange = (OWLDataRange) property.getRange();
            java.util.List oldLiterals = oldDataRange.getOneOfValueLiterals();
            if (oldLiterals.size() == 1) {
                property.setRange(null);
            }
            else {
                oldLiterals.remove(index);
                RDFSLiteral[] newLiterals = (RDFSLiteral[]) oldLiterals.toArray(new RDFSLiteral[0]);
                property.setRange(owlModel.createOWLDataRange(newLiterals));
            }
        }
    }


    public void refill() {
        listModel.clear();
        RDFProperty property = rangeWidget.getEditedProperty();
        if (property != null) {
            RDFResource range = property.getRange();
            if (range instanceof OWLDataRange) {
                OWLDataRange dataRange = (OWLDataRange) range;
                Collection values = dataRange.getOneOfValueLiterals();
                for (Iterator it = values.iterator(); it.hasNext();) {
                    RDFSLiteral literal = (RDFSLiteral) it.next();
                    listModel.addElement(literal);
                }
            }
        }
    }


    public void setDatatype(RDFSDatatype datatype) {
        this.datatype = datatype;
        listModel.clear();
    }


    public void setEditable(boolean editable) {
        list.setEnabled(editable);
        updateActions(editable);
    }


    private void updateActions(boolean editable) {
    	editable = editable && RemoteClientFrameStore.isOperationAllowed(rangeWidget.getOWLModel(), OperationImpl.PROPERTY_TAB_WRITE);
        createAction.setEnabled(editable);
        deleteAction.setEnabled(editable && list.getSelectedValue() != null);
    }
    
    public void setEnabled(boolean enabled) {
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(rangeWidget.getOWLModel(), OperationImpl.PROPERTY_TAB_WRITE);
    	createAction.setEnabled(enabled);
    	deleteAction.setEnabled(enabled);
    	setEditable(enabled);
    	super.setEnabled(enabled);
    };
}
