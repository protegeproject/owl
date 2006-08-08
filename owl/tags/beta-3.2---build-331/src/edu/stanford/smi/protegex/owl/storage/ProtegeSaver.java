package edu.stanford.smi.protegex.owl.storage;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class can be used to convert a legacy Protege ontology (in e.g. CLIPS format) into
 * an OWLModel that can be saved into a file.  The basic idea is to copy the contents of
 * the existing ontology into an OWLModel that is automatically mapped into a set of
 * OWL objects (like done in the JenaOWLModel).  Thus, the specific logic that generates
 * the corresponding OWL objects can be reused and no additiona implementation of a Protege-to-OWL
 * mapping is required.  During that conversation all normal classes (:STANDARD-CLASS) are
 * made named OWL classes (:OWL-NAMED-CLASS) and thus "shifted up" one level in the metamodel. <BR>
 * <p/>
 * This class is supported by the RestrictionUpdater class, which generates Restrictions from
 * facet overrides while the new OWLModel is populated.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeSaver extends KnowledgeBaseCopier {

    private OWLModel owlModel;


    public ProtegeSaver(KnowledgeBase source, OWLModel target) {
        super(source, target);
        this.owlModel = target;
    }


    protected void addExtraDirectTypes(Instance oldInstance, Instance newInstance) {
        super.addExtraDirectTypes(oldInstance, newInstance);
        if (oldInstance instanceof Slot &&
                !((Slot) oldInstance).getAllowsMultipleValues() &&
                newInstance instanceof RDFProperty) {
            ((RDFProperty) newInstance).setFunctional(true);
        }
    }


    protected Collection cloneValues(Collection oldValues) {
        Collection newValues = super.cloneValues(oldValues);
        return new HashSet(newValues);  // Prevent duplicates
    }


    protected Cls createCls(String clsName, Cls metaCls) {
        String validName = getValidName(clsName);
        log("+ Creating named class " + validName + " for " + clsName + " with type " + metaCls.getName());
        OWLNamedClass cls = owlModel.createOWLNamedClass(validName, (OWLNamedClass) metaCls);
        String realName = cls.getName();
        if (!realName.equals(clsName)) {
            cls.addLabel(clsName, null);
        }
        return cls;
    }


    protected Instance createInstance(String name, Cls newType) {
        String validName = getValidName(name);
        Instance instance = super.createInstance(validName, newType);
        if (instance instanceof RDFIndividual) {
            RDFIndividual soi = (RDFIndividual) instance;
            String realName = soi.getName();
            if (!realName.equals(name)) {
                soi.addLabel(name, null);
            }
        }
        return instance;
    }


    protected Slot createSlot(String slotName, ValueType valueType) {
        String validName = getValidName(slotName);
        log("+ Creating slot " + validName);
        RDFProperty property = null;
        if (valueType == ValueType.INSTANCE || valueType == ValueType.CLS) {
            property = owlModel.createOWLObjectProperty(validName);
        }
        else if (valueType == ValueType.ANY) {
            property = owlModel.createRDFProperty(validName);
        }
        else {
            property = owlModel.createOWLDatatypeProperty(validName);
        }
        property.removeUnionDomainClass(owlModel.getOWLThingClass());
        String realName = property.getName();
        if (!realName.equals(slotName)) {
            property.addLabel(slotName, null);
        }
        return property;
    }


    protected Cls getNewCls(Cls oldCls) {
        if (oldCls.getName().equals(Model.Cls.STANDARD_CLASS)) {
            return owlModel.getOWLNamedClassClass();
        }
        else if (oldCls.getName().equals(Model.Cls.STANDARD_SLOT)) {
            return owlModel.getRDFPropertyClass();
        }
        else if (oldCls.getName().equals(Model.Cls.THING)) {
            return owlModel.getOWLThingClass();
        }
        else {
            return super.getNewCls(oldCls);
        }
    }


    protected Slot getNewSlot(Slot oldSlot) {
        if (oldSlot.getName().equals(Model.Slot.DOCUMENTATION)) {
            return owlModel.getRDFSCommentProperty();
        }
        else if (oldSlot.getName().equals(Model.Slot.DIRECT_SUPERSLOTS)) {
            return owlModel.getRDFSSubPropertyOfProperty();
        }
        else if (oldSlot.getName().equals(Model.Slot.INVERSE)) {
            return owlModel.getRDFProperty(OWLNames.Slot.INVERSE_OF);
        }
        else {
            return super.getNewSlot(oldSlot);
        }
    }


    private String getValidName(String clsName) {
        clsName = clsName.replace(':', '_');
        String suggested = AbstractOWLModel.getValidOWLFrameName((AbstractOWLModel) owlModel, clsName);
        for (int i = 1; ((KnowledgeBase) owlModel).getFrame(suggested) != null; i++) {
            suggested = AbstractOWLModel.getValidOWLFrameName((AbstractOWLModel) owlModel, clsName + i);
        }
        return suggested;
    }


    protected void setDirectType(Instance oldFrame, Instance newFrame) {
        if (oldFrame instanceof Slot && newFrame instanceof OWLProperty) {
            return;  // Catch illegal conversions
        }
        else {
            super.setDirectType(oldFrame, newFrame);
        }
    }


    protected void setInitialOwnSlotValues(Instance instance) {
        super.setInitialOwnSlotValues(instance);
        if (instance instanceof Slot) {
            Slot oldSlot = (Slot) instance;
            Slot oldInverseSlot = oldSlot.getInverseSlot();
            Slot newSlot = (Slot) getNewInstance(oldSlot);
            if (oldInverseSlot != null && newSlot != null) {
                Slot newInverseSlot = (Slot) getNewInstance(oldInverseSlot);
                newSlot.setInverseSlot(newInverseSlot);
            }
        }
    }


    protected void setValueType(Slot oldSlot, Slot newSlot) {
        final ValueType valueType = oldSlot.getValueType();
        if (valueType != ValueType.ANY) {
            if (newSlot instanceof RDFProperty &&
                    (valueType == ValueType.BOOLEAN ||
                            valueType == ValueType.FLOAT ||
                            valueType == ValueType.INTEGER ||
                            valueType == ValueType.STRING)) {
                String uri = XMLSchemaDatatypes.getDefaultXSDDatatype(valueType).getURI();
                RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(uri);
                ((RDFProperty) newSlot).setRange(datatype);
            }
            super.setValueType(oldSlot, newSlot);
        }
        else {
            System.err.println("[ProtegeSaver] Warning: Slot " + oldSlot + " has value type ANY");
        }
    }
}
