package edu.stanford.smi.protegex.owl.jena.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

/**
 * An object that is capable of creating a default (CLIPS) knowledge base
 * from an OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLImporter {
    private static transient final Logger log = Log.getLogger(OWLImporter.class);
    /**
     * The target KB
     */
    private KnowledgeBase kb;

    /**
     * The source KB
     */
    private OWLModel owlModel;


    public OWLImporter(OWLModel owlModel, KnowledgeBase kb) {
        this.owlModel = owlModel;
        this.kb = kb;
        importClses();
        importSlots();
        importInstances();
        updateSlotDomain();
    }


    private void createAllowedClses(Slot oldSlot, Slot slot) {
        Collection newAllowedClses = new ArrayList();
        for (Iterator it = oldSlot.getAllowedClses().iterator(); it.hasNext();) {
            RDFSNamedClass oldAllowedClass = (RDFSNamedClass) it.next();
            Cls newAllowedCls = getCls(oldAllowedClass);
            if (newAllowedCls != null) {
                newAllowedClses.add(newAllowedCls);
            }
        }
        slot.setAllowedClses(newAllowedClses);
    }


    private void createAllowedParents(Slot oldSlot, Slot slot) {
        RDFProperty allowedParentProperty = owlModel.getProtegeAllowedParentProperty();
        if (allowedParentProperty != null) {
            Collection newAllowedParents = new ArrayList();
            for (Iterator it = oldSlot.getDirectOwnSlotValues(allowedParentProperty).iterator(); it.hasNext();) {
                RDFSNamedClass oldAllowedParent = (RDFSNamedClass) it.next();
                Cls newAllowedParent = getCls(oldAllowedParent);
                if (newAllowedParent != null) {
                    newAllowedParents.add(newAllowedParent);
                }
            }
            slot.setAllowedParents(newAllowedParents);
        }
    }


    private Cls createCls(Cls oldCls) {
        Cls metaCls = getCls(oldCls.getDirectType());
        Cls cls = kb.createCls(oldCls.getName(), Collections.EMPTY_LIST, metaCls);
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Created Cls " + oldCls.getName());
        }
        cls.setAbstract(oldCls.isAbstract());
        createDirectSuperclasses(oldCls, cls);
        createDirectTemplateSlots(oldCls, cls);
        createDirectOwnSlotValues(oldCls, cls);
        return cls;
    }


    private void createDirectOwnSlotValues(Instance oldInstance, Instance newInstance) {
        for (Iterator it = oldInstance.getOwnSlots().iterator(); it.hasNext();) {
            Slot oldSlot = (Slot) it.next();
            if (isRelevantOwnSlot(oldSlot)) {
                Slot newSlot = getSlot(oldSlot);
                if (newSlot != null) {
                    createDirectOwnSlotValues(oldInstance, newInstance, oldSlot, newSlot);
                }
            }
        }
    }


    private void createDirectOwnSlotValues(Instance oldInstance, Instance newInstance, Slot oldSlot, Slot newSlot) {
        for (Iterator it = oldInstance.getDirectOwnSlotValues(oldSlot).iterator(); it.hasNext();) {
            Object oldValue = it.next();
            Object newValue = getNewValue(oldValue);
            if (newValue != null) {
                newInstance.addOwnSlotValue(newSlot, newValue);
            }
        }
    }


    private void createDirectSuperclasses(Cls rdfsCls, Cls cls) {
        for (Iterator it = rdfsCls.getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls oldSuperCls = (Cls) it.next();
            if (oldSuperCls instanceof RDFSNamedClass) {
                Cls newSuperCls = getCls(oldSuperCls);
                cls.addDirectSuperclass(newSuperCls);
            }
        }
    }


    private void createDirectTemplateSlots(Cls oldCls, Cls cls) {
        for (Iterator it = oldCls.getDirectTemplateSlots().iterator(); it.hasNext();) {
            Slot oldSlot = (Slot) it.next();
            if (oldSlot instanceof OWLProperty) {
                Slot newSlot = getSlot(oldSlot);
                cls.addDirectTemplateSlot(newSlot);
            }
        }
        if (oldCls instanceof OWLNamedClass) {
            for (Iterator it = oldCls.getTemplateSlots().iterator(); it.hasNext();) {
                Slot oldSlot = (Slot) it.next();
                if (oldSlot instanceof RDFProperty && isRelevantOwnSlot(oldSlot)) {
                    Slot newSlot = getSlotForced(oldSlot);
                    createDirectTemplateSlotOverloads((OWLNamedClass) oldCls, cls, oldSlot, newSlot);
                }
            }
        }
    }


    private void createDirectTemplateSlotOverloads(OWLNamedClass namedCls, Cls cls, Slot oldSlot, Slot newSlot) {
        Collection restrictions = namedCls.getRestrictions(false);
        for (Iterator it = restrictions.iterator(); it.hasNext();) {
            OWLRestriction restriction = (OWLRestriction) it.next();
            if (restriction.getOnProperty().equals(oldSlot)) {
                //if(!cls.hasTemplateSlot(newSlot)) {
                cls.addDirectTemplateSlot(newSlot);
                //}
                if (restriction instanceof OWLCardinality) {
                    int cardi = ((OWLCardinality) restriction).getCardinality();
                    cls.setTemplateSlotMinimumCardinality(newSlot, cardi);
                    cls.setTemplateSlotMaximumCardinality(newSlot, cardi);
                }
                else if (restriction instanceof OWLMinCardinality) {
                    int minCardi = ((OWLMinCardinality) restriction).getCardinality();
                    cls.setTemplateSlotMinimumCardinality(newSlot, minCardi);
                }
                else if (restriction instanceof OWLMaxCardinality) {
                    int maxCardi = ((OWLMaxCardinality) restriction).getCardinality();
                    cls.setTemplateSlotMaximumCardinality(newSlot, maxCardi);
                }
                else if (restriction instanceof OWLAllValuesFrom) {
                    OWLAllValuesFrom allRestriction = (OWLAllValuesFrom) restriction;
                    if (oldSlot instanceof OWLDatatypeProperty && allRestriction.getFiller() instanceof RDFSDatatype) {
                        RDFSDatatype datatype = (RDFSDatatype) allRestriction.getFiller();
                        ValueType valueType = XMLSchemaDatatypes.getValueType(datatype.getURI());
                        cls.setTemplateSlotValueType(newSlot, valueType);
                    }
                    else if (oldSlot instanceof OWLObjectProperty && allRestriction.getFiller() instanceof RDFSNamedClass) {
                        Cls newCls = getCls((RDFSNamedClass) allRestriction.getFiller());
                        cls.setTemplateSlotAllowedClses(newSlot, Collections.singleton(newCls));
                    }
                }
                else if (restriction instanceof OWLHasValue) {
                    Object oldValue = ((OWLHasValue) restriction).getHasValue();
                    Object newValue = getNewValue(oldValue);
                    if (newValue != null) {
                        cls.setTemplateSlotValue(newSlot, newValue);
                    }
                }
            }
        }
    }


    private Instance createInstance(Instance oldInstance) {
        Cls oldType = oldInstance.getDirectType();
        Cls newType = getCls(oldType);
        Instance instance = newType.createDirectInstance(oldInstance.getName());
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Created instance " + oldInstance.getName());
        }
        createDirectOwnSlotValues(oldInstance, instance);
        return instance;
    }


    private Slot createSlot(Slot oldSlot) {
        Cls metaCls = getCls(oldSlot.getDirectType());
        Slot slot = kb.createSlot(oldSlot.getName(), metaCls);
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Created slot " + oldSlot.getName());
        }
        if (oldSlot instanceof RDFProperty && !((RDFProperty) oldSlot).isDomainDefined()) {
            kb.getRootCls().addDirectTemplateSlot(slot);
        }
        slot.setAllowsMultipleValues(oldSlot.getAllowsMultipleValues());
        ValueType valueType = oldSlot.getValueType();
        slot.setValueType(valueType);
        if (valueType == ValueType.INSTANCE) {
            createAllowedClses(oldSlot, slot);
        }
        else if (valueType == ValueType.CLS) {
            createAllowedParents(oldSlot, slot);
        }
        else if (valueType == ValueType.SYMBOL) {
            slot.setAllowedValues(oldSlot.getAllowedValues());
        }
        if (oldSlot instanceof RDFProperty) {
            RDFProperty property = (RDFProperty) oldSlot;
            RDFResource range = property.getRange();
            if (range instanceof OWLDataRange) {
                slot.setValueType(ValueType.SYMBOL);
                Collection allowedValues = new ArrayList();
                for (Iterator it = ((OWLDataRange) range).getOneOfValues().iterator(); it.hasNext();) {
                    Object value = it.next();
                    allowedValues.add(value.toString());
                }
                slot.setAllowedValues(allowedValues);
            }
        }
        createDirectOwnSlotValues(oldSlot, slot);
        if (oldSlot.getInverseSlot() instanceof RDFProperty) {
            Slot newInverseSlot = getSlot(oldSlot.getInverseSlot());
            slot.setInverseSlot(newInverseSlot);
        }
        return slot;
    }


    private Cls getCls(Cls oldCls) {
        if (oldCls.equals(owlModel.getOWLThingClass())) {
            return kb.getRootCls();
        }
        else if (oldCls.equals(owlModel.getRDFSNamedClassClass()) ||
                oldCls.equals(owlModel.getOWLNamedClassClass())) {
            return kb.getDefaultClsMetaCls();
        }
        else if (oldCls.getName().equals(RDFNames.Cls.PROPERTY) ||
                oldCls.getName().equals(OWLNames.Cls.DATATYPE_PROPERTY) ||
                oldCls.getName().equals(OWLNames.Cls.OBJECT_PROPERTY)) {
            return kb.getDefaultSlotMetaCls();
        }
        Cls cls = kb.getCls(oldCls.getName());
        if (cls == null) {
            cls = createCls(oldCls);
        }
        return cls;
    }


    private Instance getInstance(Instance oldInstance) {
        if (oldInstance instanceof Cls) {
            return getCls((Cls) oldInstance);
        }
        else if (oldInstance instanceof Slot) {
            return getSlot((Slot) oldInstance);
        }
        else {
            Instance instance = kb.getInstance(oldInstance.getName());
            if (instance == null) {
                instance = createInstance(oldInstance);
            }
            return instance;
        }
    }


    private Object getNewValue(Object oldValue) {
        Object newValue = null;
        if (oldValue instanceof RDFResource) {
            newValue = getInstance((RDFResource) oldValue);
        }
        else if (oldValue instanceof Boolean ||
                oldValue instanceof String ||
                oldValue instanceof Number) {
            newValue = oldValue;
        }
        return newValue;
    }


    private Slot getSlot(Slot oldSlot) {
        if (!(oldSlot instanceof RDFProperty) || ((RDFProperty) oldSlot).isDomainDefined()) {
            return getSlotForced(oldSlot);
        }
        else {
            return null;
        }
    }


    private Slot getSlotForced(Slot oldSlot) {
        Slot slot = kb.getSlot(oldSlot.getName());
        if (slot == null) {
            slot = createSlot(oldSlot);
        }
        return slot;
    }


    private void importClses() {
        for (Iterator it = owlModel.getUserDefinedRDFSNamedClasses().iterator(); it.hasNext();) {
            RDFSNamedClass rdfsClass = (RDFSNamedClass) it.next();
            getCls(rdfsClass);
        }
    }


    private void importInstances() {
        for (Iterator it = owlModel.getUserDefinedRDFSNamedClasses().iterator(); it.hasNext();) {
            RDFSNamedClass aClass = (RDFSNamedClass) it.next();
            for (Iterator nt = aClass.getInstances(false).iterator(); nt.hasNext();) {
                RDFResource instance = (RDFResource) nt.next();
                getInstance(instance);
            }
        }
    }


    private void importSlots() {
        for (Iterator it = owlModel.getUserDefinedRDFProperties().iterator(); it.hasNext();) {
            RDFProperty rdfProperty = (RDFProperty) it.next();
            if (!rdfProperty.isDomainDefined() && rdfProperty.isEditable()) {
                getSlotForced(rdfProperty);
            }
        }
    }


    private boolean isRelevantOwnSlot(Slot slot) {
        return (slot instanceof RDFProperty &&
                slot.isEditable() &&
                !slot.getName().startsWith(ProtegeNames.PREFIX)) ||
                Model.Slot.CONSTRAINTS.equals(slot.getName()) ||
                Model.Slot.PAL_DESCRIPTION.equals(slot.getName()) ||
                Model.Slot.PAL_NAME.equals(slot.getName()) ||
                Model.Slot.PAL_RANGE.equals(slot.getName()) ||
                Model.Slot.PAL_STATEMENT.equals(slot.getName());
    }

    private void updateSlotDomain() {
        Cls rootCls = kb.getRootCls();
        for (Iterator it = new ArrayList(rootCls.getDirectTemplateSlots()).iterator(); it.hasNext();) {
            Slot slot = (Slot) it.next();
            if (slot.getDirectDomain().size() > 1) {
                rootCls.removeDirectTemplateSlot(slot);
            }
        }
    }
}
