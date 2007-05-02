package edu.stanford.smi.protegex.owl.model.framestore;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

class HasValueRestrictionUpdater extends AbstractRestrictionUpdater {

    private Facet valuesFacet;


    HasValueRestrictionUpdater(AbstractOWLModel owlModel) {
        super(owlModel);
        valuesFacet = owlModel.getFacet(Model.Facet.VALUES);
    }


    // Implements RestrictionUpdater
    public void copyFacetValuesIntoNamedClass(RDFSNamedClass cls, OWLRestriction restriction) {
        Slot slot = restriction.getOnProperty();
        if (slot != null) {
            updateValuesFacet(cls, slot);
        }
    }


    // Implements RestrictionUpdater
    public void updateRestrictions(OWLNamedClass cls, RDFProperty property, Facet facet) {
        removeRestrictions(cls, property, owlModel.getCls(OWLNames.Cls.HAS_VALUE_RESTRICTION));
        if (((Cls) cls).hasDirectlyOverriddenTemplateFacet(property, valuesFacet)) {
            Collection values = ((Cls) cls).getTemplateSlotValues(property);
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object value = it.next();
                OWLHasValue restriction = owlModel.createOWLHasValue(property, value);
                cls.addSuperclass(restriction);
                log("+ OWLHasValue " + restriction.getBrowserText() + " to " + cls.getName() + "." + property.getName());
            }
        }
    }


    void updateValuesFacet(RDFSNamedClass cls, Slot slot) {
        ((Cls) cls).setTemplateFacetValues(slot, valuesFacet, Collections.EMPTY_LIST); // was: null
        log("- :VALUES override from " + cls.getName() + "." + slot.getName());
        for (Iterator it = getDirectRestrictions(cls, slot, OWLHasValue.class).iterator(); it.hasNext();) {
            OWLHasValue r = (OWLHasValue) it.next();
            if (r != null) {
                Object value = r.getHasValue();
                if (value != null) {
                    if (value instanceof RDFSLiteral) {
                        value = value.toString();
                    }
                    ((Cls) cls).addTemplateFacetValue(slot, valuesFacet, value);
                }
            }
        }
    }
}
