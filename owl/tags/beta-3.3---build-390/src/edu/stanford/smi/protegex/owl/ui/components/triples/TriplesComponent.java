package edu.stanford.smi.protegex.owl.ui.components.triples;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A PropertyWidget to edit the values of annotation properties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TriplesComponent extends AbstractTriplesComponent {


    private Action deleteRowAction;


    public TriplesComponent(RDFProperty predicate) {
        this(predicate, "Triples", OWLIcons.getImageIcon(OWLIcons.TRIPLES));
    }


    public TriplesComponent(RDFProperty predicate, String label, Icon icon) {
        super(predicate, label, icon);
    }


    protected void addButtons(LabeledComponent lc) {
        lc.addHeaderButton(new CreateValueAction(getTable(), "Create datatype property value...", OWLIcons.getCreateIndividualIcon(OWLIcons.DATATYPE_TRIPLE)) {
            protected Collection getAllowedProperties(OWLModel owlModel) {
                Collection results = new ArrayList();
                Iterator it = owlModel.getRDFProperties().iterator();
                RDFSNamedClass type = (RDFSNamedClass) table.getTableModel().getSubject().getRDFType();
                while (it.hasNext()) {
                    RDFProperty property = (RDFProperty) it.next();
                    if (property.isVisible() &&
                            (property.hasDatatypeRange() || isDatatypeProperty(property, type))) {
                        results.add(property);
                    }
                }
                results.remove(owlModel.getRDFProperty(Model.Slot.PAL_DESCRIPTION));
                results.remove(owlModel.getRDFProperty(Model.Slot.PAL_NAME));
                results.remove(owlModel.getRDFProperty(Model.Slot.PAL_RANGE));
                results.remove(owlModel.getRDFProperty(Model.Slot.PAL_STATEMENT));
                return results;
            }
        });
        lc.addHeaderButton(new CreateValueAction(getTable(), "Create object property value...", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
            protected Collection getAllowedProperties(OWLModel owlModel) {
                Collection results = new ArrayList();
                Iterator it = super.getAllowedProperties(owlModel).iterator();
                RDFSNamedClass type = (RDFSNamedClass) table.getTableModel().getSubject().getRDFType();
                while (it.hasNext()) {
                    RDFProperty property = (RDFProperty) it.next();
                    if (!isDatatypeProperty(property, type) && (property.isAnnotationProperty() || !property.isSystem())) {
                        results.add(property);
                    }
                }
                return results;
            }
        });
        lc.addHeaderButton(new AddResourceAction(getTable()));
        deleteRowAction = new DeleteTripleAction(getTable());
        lc.addHeaderButton(deleteRowAction);
        deleteRowAction.setEnabled(false);
    }


    private boolean isDatatypeProperty(RDFProperty property, RDFSNamedClass type) {
        if (!property.hasObjectRange()) {
            if (type instanceof OWLNamedClass) {
                RDFResource allValuesFrom = ((OWLNamedClass) type).getAllValuesFrom(property);
                if (allValuesFrom instanceof RDFSDatatype) {
                    return true;
                }
            }
            else {
                return true;
            }
        }
        return false;
    }


    protected void updateActions() {
        super.updateActions();
        final int row = getTable().getSelectedRow();
        TriplesTableModel tableModel = getTable().getTableModel();
        boolean deleteRowEnabled = false;
        if (row >= 0) {
            deleteRowEnabled = tableModel.isDeleteEnabled(row);
        }
        deleteRowAction.setEnabled(deleteRowEnabled);
    }
}
