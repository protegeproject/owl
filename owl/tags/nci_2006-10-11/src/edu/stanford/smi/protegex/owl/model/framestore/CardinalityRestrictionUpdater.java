package edu.stanford.smi.protegex.owl.model.framestore;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class CardinalityRestrictionUpdater extends AbstractRestrictionUpdater {

    private Facet maxCardinalityFacet;

    private Facet minCardinalityFacet;


    CardinalityRestrictionUpdater(AbstractOWLModel owlModel) {
        super(owlModel);
        maxCardinalityFacet = owlModel.getFacet(Model.Facet.MAXIMUM_CARDINALITY);
        minCardinalityFacet = owlModel.getFacet(Model.Facet.MINIMUM_CARDINALITY);
    }


    private OWLCardinalityBase createCardinalityRestriction(RDFProperty property, Facet facet, int cardinality) {
        if (facet.equals(minCardinalityFacet)) {
            return owlModel.createOWLMinCardinality(property, cardinality);
        }
        else {
            return owlModel.createOWLMaxCardinality(property, cardinality);
        }
    }


    private void ensureCardinalityRestriction(Cls cls, RDFProperty property, Facet facet) {
        if (cls.hasDirectlyOverriddenTemplateFacet(property, facet)) {
            int cardinality = ((Integer) cls.getTemplateFacetValue(property, facet)).intValue();
            if (!hasCardinalityRestriction(cls, property, facet, cardinality)) {
                removeRestrictions(cls, property, getCardinalityRestrictionCls(facet));
                OWLCardinalityBase restriction = createCardinalityRestriction(property, facet, cardinality);
                cls.addDirectSuperclass(restriction);
                log("+ " + restriction.getClass().getName() + " " + restriction.getBrowserText() + " to " + cls.getName() + "." + property.getName());
            }
        }
    }


    private edu.stanford.smi.protege.model.Cls getCardinalityRestrictionCls(Facet facet) {
        if (facet.equals(minCardinalityFacet)) {
            return owlModel.getCls(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION);
        }
        else {
            return owlModel.getCls(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION);
        }
    }


    private boolean hasCardinalityRestriction(edu.stanford.smi.protege.model.Cls cls, Slot slot, Facet facet, int cardinality) {
        edu.stanford.smi.protege.model.Cls metaCls = getCardinalityRestrictionCls(facet);
        return hasCardinalityRestriction(cls, slot, metaCls, cardinality);
    }


    private boolean hasCardinalityRestriction(edu.stanford.smi.protege.model.Cls cls, Slot slot, edu.stanford.smi.protege.model.Cls metaCls, int cardinality) {
        for (Iterator it = cls.getDirectSuperclasses().iterator(); it.hasNext();) {
            edu.stanford.smi.protege.model.Cls superCls = (edu.stanford.smi.protege.model.Cls) it.next();
            if (superCls.getDirectType().equals(metaCls)) {
                OWLCardinalityBase cardinalityRestriction = (OWLCardinalityBase) superCls;
                if (slot.equals(cardinalityRestriction.getOnProperty()) &&
                        cardinalityRestriction.getCardinality() == cardinality) {
                    return true;
                }
            }
        }
        return false;
    }


    private void updateBothCardinalityFacets(RDFSNamedClass cls, Slot slot) {
        ((Cls) cls).setTemplateFacetValues(slot, minCardinalityFacet, Collections.EMPTY_LIST);
        ((Cls) cls).setTemplateFacetValues(slot, maxCardinalityFacet, Collections.EMPTY_LIST);
        OWLCardinality restriction =
                (OWLCardinality) getDirectRestriction(cls, slot, OWLCardinality.class);
        if (restriction != null) {
            int cardinality = restriction.getCardinality();
            ((Cls) cls).addTemplateFacetValue(slot, minCardinalityFacet, new Integer(cardinality));
            ((Cls) cls).addTemplateFacetValue(slot, maxCardinalityFacet, new Integer(cardinality));
            log("+ :Max and :MinimumCardinality overrides to " + cls.getName() +
                    "." + slot.getName() + ": " + cardinality);
        }
    }


    /**
     * Updates or creates CardinalityRestrictions in response to a change in either max or min
     * cardinality facet.  Depending on the situation this does the following:
     * <p/>
     * a) MAX has changed and MAX != MIN:
     * - Replace MaxCardiRestrictions
     * - Remove CardiRestrictions
     * - If defined then replace MinCardiRestrictions
     * b) MAX has changed and MAX == MIN
     * - Remove MaxCardiRestrictions
     * - Remove MinCardiRestrictions
     * - Replace CardiRestrictions
     * c) MAX was deleted
     * - Remove MaxCardiRestrictions
     * - Remove CardiRestrictions
     * - If defined then replace MinCardiRestrictions
     *
     * @param cls          the Cls where the facet has changed
     * @param slot         the Slot that has changed
     * @param changedFacet the Facet that has changed
     * @param otherFacet   the inverse Facet to changedFacet (unchanged)
     */
    private void updateCardinalityRestrictions(edu.stanford.smi.protege.model.Cls cls, RDFProperty slot, Facet changedFacet, Facet otherFacet) {
        if (cls.hasDirectlyOverriddenTemplateFacet(slot, changedFacet)) {

            ensureCardinalityRestriction(cls, slot, changedFacet);

            Integer cardinality = ((Integer) cls.getTemplateFacetValue(slot, changedFacet));
            if (cls.hasDirectlyOverriddenTemplateFacet(slot, otherFacet) &&
                    cardinality.equals(cls.getTemplateFacetValue(slot, otherFacet))) {
                removeRestrictions(cls, slot, owlModel.getCls(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION));
                removeRestrictions(cls, slot, owlModel.getCls(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION));
                if (!hasCardinalityRestriction(cls, slot, owlModel.getCls(OWLNames.Cls.CARDINALITY_RESTRICTION), cardinality.intValue())) {
                    removeRestrictions(cls, slot, owlModel.getCls(OWLNames.Cls.CARDINALITY_RESTRICTION));
                    OWLCardinality restriction = owlModel.createOWLCardinality(slot, cardinality.intValue());
                    cls.addDirectSuperclass(restriction);
                    log("+ OWLCardinality " + restriction.getBrowserText() + " to " + cls.getName() + "." + slot.getName());
                }
            }
            else {
                removeRestrictions(cls, slot, owlModel.getCls(OWLNames.Cls.CARDINALITY_RESTRICTION));
                ensureCardinalityRestriction(cls, slot, changedFacet);
                ensureCardinalityRestriction(cls, slot, otherFacet);
            }
        }
        else { // Perhaps we can now convert OWLCardinality into other restriction
            removeRestrictions(cls, slot, getCardinalityRestrictionCls(changedFacet));
            removeRestrictions(cls, slot, owlModel.getCls(OWLNames.Cls.CARDINALITY_RESTRICTION));
            ensureCardinalityRestriction(cls, slot, otherFacet);
        }
    }


    protected void updateMaximumCardinalityFacet(RDFSNamedClass cls, Slot slot) {
        OWLMaxCardinality restriction =
                (OWLMaxCardinality) getDirectRestriction(cls, slot, OWLMaxCardinality.class);
        Cls c = cls;
        final List oldValues = c.getDirectTemplateFacetValues(slot, maxCardinalityFacet);
        if (restriction != null) {
            int cardinality = restriction.getCardinality();
            final Integer value = new Integer(cardinality);
            if (!oldValues.contains(value)) {
                c.setTemplateFacetValue(slot, maxCardinalityFacet, value);
            }
        }
        else {
            if (!oldValues.isEmpty()) {
                c.setTemplateFacetValues(slot, maxCardinalityFacet, Collections.EMPTY_LIST);
            }
        }
    }


    protected void updateMinimumCardinalityFacet(RDFSNamedClass cls, Slot slot) {
        final Cls c = ((Cls) cls);
        final List oldValues = c.getDirectTemplateFacetValues(slot, minCardinalityFacet);
        OWLMinCardinality restriction =
                (OWLMinCardinality) getDirectRestriction(cls, slot, OWLMinCardinality.class);
        if (restriction != null) {
            int cardinality = restriction.getCardinality();
            final Integer value = new Integer(cardinality);
            if (!oldValues.contains(value)) {
                c.setTemplateFacetValue(slot, minCardinalityFacet, value);
            }
        }
        else {
            if (!oldValues.isEmpty()) {
                c.setTemplateFacetValues(slot, minCardinalityFacet, Collections.EMPTY_LIST);
            }
        }
    }


    // Implements RestrictionUpdater
    public void copyFacetValuesIntoNamedClass(RDFSNamedClass cls, OWLRestriction restriction) {
        Slot slot = restriction.getOnProperty();
        if (slot != null) {
            if (restriction instanceof OWLCardinality) {
                updateBothCardinalityFacets(cls, slot);
            }
            else if (restriction instanceof OWLMaxCardinality) {
                updateMaximumCardinalityFacet(cls, slot);
            }
            else if (restriction instanceof OWLMinCardinality) {
                updateMinimumCardinalityFacet(cls, slot);
            }
        }
    }


    // Implements RestrictionUpdater
    public void updateRestrictions(OWLNamedClass cls, RDFProperty property, Facet facet) {
        if (facet.equals(maxCardinalityFacet)) {
            updateCardinalityRestrictions(cls, property, maxCardinalityFacet, minCardinalityFacet);
        }
        else if (facet.equals(minCardinalityFacet)) {
            updateCardinalityRestrictions(cls, property, minCardinalityFacet, maxCardinalityFacet);
        }
    }
}
