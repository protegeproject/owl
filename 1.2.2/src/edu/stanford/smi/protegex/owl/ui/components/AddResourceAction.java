package edu.stanford.smi.protegex.owl.ui.components;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddResourceAction extends ResourceSelectionAction {

    private AddablePropertyValuesComponent component;

    private boolean symmetric;


    public AddResourceAction(AddablePropertyValuesComponent component, boolean symmetric) {
        super("Add...", OWLIcons.getAddIcon(OWLIcons.RDF_INDIVIDUAL), true);
        this.component = component;
        this.symmetric = symmetric;
    }


    public Collection getSelectableResources() {
        RDFResource subject = component.getSubject();
        RDFProperty predicate = component.getPredicate();
        OWLModel owlModel = subject.getOWLModel();
        Collection values;
        if (isClassProperty()) {
            values = owlModel.getUserDefinedRDFSNamedClasses();
        }
        else {
            RDFSNamedClass type = (RDFSNamedClass) subject.getProtegeType();
            Collection clses = type.getUnionRangeClasses(predicate);
            if (clses.size() > 0 && !OWLUtil.containsAnonymousClass(clses)) {
                values = new HashSet();
                for (Iterator it = clses.iterator(); it.hasNext();) {
                    Cls cls = (Cls) it.next();
                    if (cls.isVisible()) {
                        values.addAll(cls.getInstances());
                    }
                }
            }
            else {
                if (predicate instanceof OWLProperty) {
                    values = new ArrayList(owlModel.getOWLIndividuals(true));
                }
                else {
                    values = new ArrayList(owlModel.getRDFIndividuals(true));
                }
            }
        }
        if (OWLUI.isExternalResourcesSupported(owlModel)) {
            values.addAll(owlModel.getRDFUntypedResourcesClass().getInstances(false));
        }
        values.remove(subject);
        values.removeAll(subject.getPropertyValues(predicate));
        return AbstractOWLModel.getRDFResources(owlModel, values);
    }


    private boolean isClassProperty() {
        RDFResource subject = component.getSubject();
        RDFProperty predicate = component.getPredicate();
        RDFSNamedClass type = (RDFSNamedClass) subject.getProtegeType();
        Collection clses = type.getUnionRangeClasses(predicate);
        RDFSNamedClass metaclass = predicate.getOWLModel().getRDFSNamedClassClass();
        if (clses.size() == 1) {
            RDFSClass range = (RDFSClass) clses.iterator().next();
            return range.equals(metaclass) || range.getSuperclasses(true).contains(metaclass);
        }
        return false;
    }


    private boolean isPropertyProperty() {
        RDFResource subject = component.getSubject();
        RDFProperty predicate = component.getPredicate();
        if (((Slot) predicate).getValueType() == ValueType.INSTANCE) {
            RDFSNamedClass type = (RDFSNamedClass) subject.getProtegeType();
            Collection clses = type.getUnionRangeClasses(predicate);
            RDFSNamedClass metaclass = predicate.getOWLModel().getRDFPropertyClass();
            return clses.size() == 1 && ((RDFSClass) clses.iterator().next()).getSuperclasses(true).contains(metaclass);
        }
        else {
            return false;
        }
    }


    public Collection pickResources() {
        RDFResource subject = component.getSubject();
        RDFProperty predicate = component.getPredicate();
        OWLModel owlModel = predicate.getOWLModel();
        if (OWLUI.isExternalResourcesSupported(owlModel)) {
            owlModel.getRDFUntypedResourcesClass().setVisible(true);
        }
        Collection result;
        if (isClassProperty()) {
            String title = "Select classes to add";
            result = ProtegeUI.getSelectionDialogFactory().selectClasses(component, owlModel, title);
        }
        else if (isPropertyProperty()) {
            String title = "Select properties to add";
            Collection properties = owlModel.getVisibleUserDefinedRDFProperties();
            result = ProtegeUI.getSelectionDialogFactory().selectResourcesFromCollection(component, owlModel, properties, title);
        }
        else {
            RDFSNamedClass type = (RDFSNamedClass) subject.getRDFType();
            Collection clses = type.getUnionRangeClasses(predicate);
            if (OWLUtil.containsAnonymousClass(clses) || clses.isEmpty()) {
                clses = Collections.singleton(owlModel.getOWLThingClass());
            }
            result = ProtegeUI.getSelectionDialogFactory().selectResourcesByType(component, owlModel, clses);
        }
        owlModel.getRDFUntypedResourcesClass().setVisible(false);
        return result;
    }


    public void resourceSelected(RDFResource resource) {
        RDFResource subject = component.getSubject();
        RDFProperty predicate = component.getPredicate();
        if (subject.getPropertyValues(predicate).contains(resource)) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(subject.getOWLModel(), "The object " + resource.getBrowserText() + " is already in the list.");
        }
        else {
            component.addObject(resource, symmetric);
        }
    }
}
