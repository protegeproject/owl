package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class RDFPropertyPostProcessor {

    private KnowledgeBase kb;

    private OWLModel owlModel;

    private Set specialPropertyTypes = new HashSet();

    private RDFSNamedClass symmetricPropertyClass;

    private RDFSNamedClass transitivePropertyClass;


    RDFPropertyPostProcessor(OWLModel owlModel) {

        this.owlModel = owlModel;
        this.kb = owlModel;

        symmetricPropertyClass = owlModel.getRDFSNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY);
        transitivePropertyClass = owlModel.getRDFSNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY);

        specialPropertyTypes.add(owlModel.getOWLAnnotationPropertyClass());
        specialPropertyTypes.add(owlModel.getOWLFunctionalPropertyClass());
        specialPropertyTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY));
        specialPropertyTypes.add(symmetricPropertyClass);
        specialPropertyTypes.add(transitivePropertyClass);
        specialPropertyTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.DEPRECATED_PROPERTY));

        // updateExternalResources();

        Iterator it = owlModel.getUserDefinedRDFProperties().iterator();
        while (it.hasNext()) {
            RDFProperty property = (RDFProperty) it.next();
            TripleStoreUtil.ensureActiveTripleStore(property);
            property = updatePrimaryDirectType(property);
            updateValueType(property);
            updateDirectDomain(property);
        }
    }

    /*private void updateExternalResources() {
       RDFProperty[] properties = new RDFProperty[] {
           owlModel.getRDFSSubPropertyOfProperty()
       };
       Collection es = new ArrayList(owlModel.getRDFExternalResourceClass().getInstances(false));
       for (Iterator it = es.iterator(); it.hasNext();) {
           RDFExternalResource e = (RDFExternalResource) it.next();
           for (int i = 0; i < properties.length; i++) {
               RDFProperty property = properties[i];
               if(!kb.getFramesWithValue(property, null, false, e).isEmpty()) {
                   e.setDirectType(owlModel.getRDFPropertyClass());
                   break;
               }
           }
       }
   } */


    /**
     * Makes sure that all domainless top-level properties have owl:Thing in their :DIRECT-DOMAIN.
     *
     * @param slot the slot to update
     */
    private void updateDirectDomain(Slot slot) {
        boolean oldValue = false;
        if (owlModel.getOWLFrameStore() != null) {
          oldValue = owlModel.getOWLFrameStore().suppressUpdateRDFSDomain;
          owlModel.getOWLFrameStore().suppressUpdateRDFSDomain = true;
        }
        
        Collection newDirectDomain = new ArrayList();

        Collection domains = slot.getDirectOwnSlotValues(owlModel.getRDFSDomainProperty());
        if (domains.size() == 1) {
            RDFSClass domainClass = (RDFSClass) domains.iterator().next();
            if (domainClass instanceof OWLUnionClass) {
                newDirectDomain.addAll(((OWLUnionClass) domainClass).getOperands());
            }
            else {
                newDirectDomain.add(domainClass);
            }
        }
        if (domains.size() > 1) {
            Cls firstClass = (Cls) domains.iterator().next();
            newDirectDomain.add(firstClass);
        }

        Collection directDomain = new ArrayList(slot.getDirectDomain());
        for (Iterator it = directDomain.iterator(); it.hasNext();) {
            Cls domainCls = (Cls) it.next();
            if (!newDirectDomain.contains(domainCls)) {
                domainCls.removeDirectTemplateSlot(slot);
            }
        }
        for (Iterator it = newDirectDomain.iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (!cls.hasDirectTemplateSlot(slot)) {
                cls.addDirectTemplateSlot(slot);
            }
        }

        if (slot.getDirectSuperslotCount() == 0) {
            if (slot.getDirectDomain().isEmpty()) {
                kb.getRootCls().addDirectTemplateSlot(slot);
            }
        }
        if (owlModel.getOWLFrameStore() != null) {
          owlModel.getOWLFrameStore().suppressUpdateRDFSDomain = oldValue;
        }
    }


    private RDFProperty updatePrimaryDirectType(Slot property) {
        Collection types = property.getDirectTypes();
        boolean hasNoOtherType = true;
        RDFSClass newType = owlModel.getRDFPropertyClass();
        for (Iterator it = types.iterator(); it.hasNext();) {
            Cls type = (Cls) it.next();
            if (!specialPropertyTypes.contains(type)) {
                hasNoOtherType = false;
                break;
            }
            else if (symmetricPropertyClass.equals(type) || transitivePropertyClass.equals(type)) {
                newType = owlModel.getOWLObjectPropertyClass();
            }
        }
        if (hasNoOtherType) {
          property.addDirectType(newType);
        }

        // Ensure that the special types are at the end of the types list
        if (property.getDirectTypes().size() > 1) {
            for (Iterator it = specialPropertyTypes.iterator(); it.hasNext();) {
                RDFSNamedClass type = (RDFSNamedClass) it.next();
                if (types.contains(type)) {
                    property.removeDirectType(type);
                    property.addDirectType(type);
                    property = owlModel.getRDFProperty(property.getName());
                }
            }
        }
        return (RDFProperty) property;
    }


    private void updateValueType(RDFProperty property) {
        Slot slot = property;
        Collection ranges = property.getRanges(false);
        if (ranges.isEmpty()) {
            if (property.getSuperpropertyCount() == 0) {
                if (property instanceof OWLObjectProperty) {
                    slot.setValueType(ValueType.INSTANCE);
                }
                else {
                    slot.setValueType(ValueType.ANY);
                }
            }
            else {
                Slot valueTypeSlot = kb.getSlot(Model.Slot.VALUE_TYPE);
                if (!slot.getDirectOwnSlotValues(valueTypeSlot).isEmpty()) {
                    slot.setOwnSlotValues(valueTypeSlot, Collections.EMPTY_LIST);
                }
            }
        }
        else {
            Object range = ranges.iterator().next();
            if (range instanceof RDFSDatatype) {
                String uri = ((RDFSDatatype) range).getURI();
                ValueType valueType = XMLSchemaDatatypes.getValueType(uri);
                slot.setValueType(valueType);
            }
            else if (range instanceof OWLDataRange) {
                OWLDataRange dataRange = (OWLDataRange) range;
                List literals = dataRange.getOneOfValueLiterals();
                if (literals.isEmpty()) {
                    slot.setValueType(ValueType.ANY);
                }
                else {
                    RDFSLiteral literal = (RDFSLiteral) literals.iterator().next();
                    String uri = literal.getDatatype().getURI();
                    ValueType valueType = XMLSchemaDatatypes.getValueType(uri);
                    slot.setValueType(valueType);
                    // slot.setAllowedValues(literals);
                }
            }
            else {
                Collection oldAllowedClses; // To optimize performance: Skip redundant update
                if (slot.getValueType() != ValueType.INSTANCE) {
                    slot.setValueType(ValueType.INSTANCE);
                    oldAllowedClses = Collections.EMPTY_LIST;
                }
                else {
                    oldAllowedClses = slot.getAllowedClses();
                }
                Collection newAllowedClses = null;
                if (range instanceof OWLUnionClass) {
                    newAllowedClses = ((OWLUnionClass) range).getOperands();
                }
                else {
                    newAllowedClses = Collections.singleton(range);
                }
                if (!CollectionUtilities.containSameItems(oldAllowedClses, newAllowedClses)) {
                    slot.setAllowedClses(newAllowedClses);
                }
            }
        }
    }
}
