package edu.stanford.smi.protegex.owl.ui.components.datarangefield;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DataRangeFieldComponent extends AbstractPropertyValuesComponent {

    private JComboBox comboBox;

    private Action deleteAction = new AbstractAction("Delete value", OWLIcons.getDeleteIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleDeleteAction();
        }
    };


    public DataRangeFieldComponent(RDFProperty predicate) {
        super(predicate);

        comboBox = new JComboBox();
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignComboBoxValue();
            }
        });

        String label = getLabel();
        OWLLabeledComponent lc = new OWLLabeledComponent(label, comboBox);
        lc.addHeaderButton(deleteAction);

        add(BorderLayout.CENTER, lc);
    }


    private void assignComboBoxValue() {
        Object value = comboBox.getSelectedItem();
        getSubject().setPropertyValue(getPredicate(), value);
    }


    private OWLDataRange getDataRange() {
        RDFProperty predicate = getPredicate();
        OWLDataRange dataRange = null;
        for (Iterator it = getSubject().getRDFTypes().iterator(); it.hasNext();) {
            RDFSNamedClass type = (RDFSNamedClass) it.next();
            if (type instanceof OWLNamedClass) {
                final OWLNamedClass namedClass = ((OWLNamedClass) type);
                RDFResource allValuesFrom = namedClass.getAllValuesFrom(predicate);
                if (allValuesFrom instanceof OWLDataRange) {
                    dataRange = (OWLDataRange) allValuesFrom;
                    break;
                }
                RDFResource someValuesFrom = namedClass.getSomeValuesFrom(predicate);
                if (someValuesFrom instanceof OWLDataRange) {
                    dataRange = (OWLDataRange) someValuesFrom;
                    break;
                }
            }
        }
        if (dataRange == null) {
            RDFResource range = predicate.getRange(true);
            if (range instanceof OWLDataRange) {
                dataRange = (OWLDataRange) range;
            }
        }
        return dataRange;
    }


    private Collection getDataRangeValues() {
        if (getSubject() != null) {
            OWLDataRange dataRange = getDataRange();
            if (dataRange != null) {
                return dataRange.getOneOfValueLiterals();
            }
        }
        return Collections.EMPTY_LIST;
    }


    private void handleDeleteAction() {
        getSubject().setPropertyValue(getPredicate(), null);
    }


    public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        updateActionState();
        Collection values = getDataRangeValues();
        Object[] items = values.toArray();
        comboBox.setModel(new DefaultComboBoxModel(items));
        updateComboBoxState();
    }


    private void updateActionState() {
        deleteAction.setEnabled(getSubject() != null &&
                getSubject().getPropertyValue(getPredicate()) != null &&
                hasOnlyEditableValues());
    }


    private void updateComboBoxState() {
        comboBox.setEnabled(getSubject() != null && hasOnlyEditableValues());
    }


    public void valuesChanged() {
        Object value = getObject();
        if (value != null &&
                !(value instanceof RDFSLiteral) &&
                !(value instanceof RDFResource)) {
            value = getOWLModel().createRDFSLiteral(value);
        }
        comboBox.setSelectedItem(value);
        updateActionState();
        updateComboBoxState();
    }
}
