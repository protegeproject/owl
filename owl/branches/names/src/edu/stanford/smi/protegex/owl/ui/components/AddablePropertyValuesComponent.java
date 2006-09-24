package edu.stanford.smi.protegex.owl.ui.components;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AddablePropertyValuesComponent extends AbstractPropertyValuesComponent {

    protected AddablePropertyValuesComponent(RDFProperty predicate) {
        super(predicate);
    }


    public void addObject(RDFResource resource, boolean symmetric) {
        getSubject().addPropertyValue(getPredicate(), resource);
        if (symmetric) {
            resource.addPropertyValue(getPredicate(), getSubject());
        }
    }
}
