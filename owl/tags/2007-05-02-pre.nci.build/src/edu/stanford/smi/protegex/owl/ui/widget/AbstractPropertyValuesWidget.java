package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * A container for PropertyValuesComponents, so that they can be placed on a
 * Protege FormWidget.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractPropertyValuesWidget extends AbstractSlotWidget {

    private PropertyValuesComponent component;


    protected abstract PropertyValuesComponent createComponent(RDFProperty predicate);


    public void initialize() {
        component = createComponent((RDFProperty) getSlot());
        add((Component) component);
    }


    public static boolean isInvalid(RDFResource subject, RDFProperty predicate, Collection values) {
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object value = it.next();
            if (!subject.isValidPropertyValue(predicate, value)) {
                return true;
            }
        }
        RDFResource type = subject.getRDFType();
        if (type instanceof OWLNamedClass) {
            OWLNamedClass namedClass = (OWLNamedClass) type;
            int min = namedClass.getMinCardinality(predicate);
            if (min >= 0 && values.size() < min) {
                return true;
            }
            int max = namedClass.getMaxCardinality(predicate);
            if (max >= 0 && values.size() > max) {
                return true;
            }
            if(values.size() == 0 && namedClass.getSomeValuesFrom(predicate) != null) {
                return true;
            }
        }
        return false;
    }


    public void setInstance(Instance newInstance) {
        RDFResource subject = null;
        if (newInstance instanceof RDFResource) {
            subject = (RDFResource) newInstance;
        }
        component.setSubject(subject);
        super.setInstance(newInstance);
    }


    public void setValues(Collection values) {
        super.setValues(values);
        component.valuesChanged();
    }


    protected void updateBorder(Collection values) {
        if (OWLUI.isConstraintChecking((OWLModel) getKnowledgeBase())) {
            RDFResource subject = (RDFResource) getInstance();
            RDFProperty predicate = (RDFProperty) getSlot();
            if (subject != null && predicate != null) {
                boolean invalid = isInvalid(subject, predicate, values);
                if (invalid) {
                    setInvalidValueBorder();
                }
                else {
                    setNormalBorder();
                }
                repaint();
            }
        }
    }
}
