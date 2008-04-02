package edu.stanford.smi.protegex.owl.ui.components.annotations;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.components.triples.*;
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
public class AnnotationsComponent extends AbstractTriplesComponent {

    private Action deleteRowAction;


    public AnnotationsComponent(RDFProperty predicate) {
        super(predicate, "Annotations", OWLIcons.getImageIcon(OWLIcons.ANNOTATIONS_TABLE));
    }


    protected void addButtons(LabeledComponent lc) {
        lc.addHeaderButton(new CreateValueAction(getTable(), "Create new annotation value", OWLIcons.getCreateIcon(OWLIcons.ANNOTATION)) {


            public Collection getSelectableResources() {
                TriplesTableModel tableModel = table.getTableModel();
                OWLModel owlModel = tableModel.getOWLModel();
                Collection properties = new ArrayList();
                Collection annotationProperties = owlModel.getOWLAnnotationProperties();
                Collection ontologyProperties = owlModel.getOWLOntologyProperties();
                RDFResource resource = tableModel.getSubject();
                for (Iterator it = annotationProperties.iterator(); it.hasNext();) {
                    RDFProperty property = (RDFProperty) it.next();
                    if (ontologyProperties.contains(property)) {
                        if (resource instanceof OWLOntology) {
                            properties.add(property);
                        }
                    }
                    else {
                        RDFSClass type = resource.getProtegeType();
                        Collection domainProperties = type.getUnionDomainProperties(true);
                        if (domainProperties.contains(property)) {
                            boolean zero = resource.getPropertyValues(property, true).isEmpty();
                            if (!property.isFunctional() || zero) {
                                properties.add(property);
                            }
                        }
                    }
                }
                return properties;
            }
        });
        lc.addHeaderButton(new AddTodoAction((AnnotationsTable) getTable()));
        lc.addHeaderButton(new AddResourceAction(getTable()) {
            protected Collection getAllowedProperties(OWLModel owlModel) {
                return owlModel.getOWLAnnotationProperties();
            }
        });
        deleteRowAction = new DeleteTripleAction(getTable(), "Delete selected annotation", OWLIcons.getDeleteIcon(OWLIcons.ANNOTATION));
        deleteRowAction.setEnabled(false);
        lc.addHeaderButton(deleteRowAction);
    }


    protected TriplesTable createTable(Project project) {
        return new AnnotationsTable(project, (AnnotationsTableModel) getTableModel());
    }


    protected TriplesTableModel createTableModel() {
        return new AnnotationsTableModel();
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
