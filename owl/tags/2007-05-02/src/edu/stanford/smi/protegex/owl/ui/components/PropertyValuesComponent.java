package edu.stanford.smi.protegex.owl.ui.components;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface PropertyValuesComponent {

    RDFResource getSubject();


    RDFProperty getPredicate();


    Collection getObjects();


    void setSubject(RDFResource subject);


    void valuesChanged();
}
