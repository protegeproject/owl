package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.model.util.ResourceCopier;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.*;
import java.util.logging.Level;

/**
 * A basic implementation of the RDFSClass interface that provides support for disjoint classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractRDFSClass extends DefaultCls implements RDFSClass {


    public AbstractRDFSClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    AbstractRDFSClass() {
    }


    protected void addAnonymousClses(Collection target, Collection clses) {
        for (Iterator it = clses.iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof OWLAnonymousClass) {
                target.add(cls);
            }
        }
    }


    public void addClassListener(ClassListener listener) {
        if (!(listener instanceof ClassAdapter)) {
            throw new IllegalArgumentException("Listener must be a ClassAdapter");
        }
        addClsListener(listener);
    }


    public void addSuperclass(RDFSClass superclass) {
        getKnowledgeBase().addDirectSuperclass(this, superclass);
    }


    public void addToUnionDomainOf(RDFProperty property) {
        getKnowledgeBase().addDirectTemplateSlot(this, property);
    }


    public RDFSClass createClone() {
    	RDFSClass clone = null;
    	
    	try {
    		getOWLModel().beginTransaction("Created clone of " + this.getBrowserText());
            ResourceCopier copier = new ResourceCopier();
            accept(copier);
            
            clone = (RDFSClass) copier.getCopy();
            getOWLModel().commitTransaction();
		} catch (Throwable e) {
			getOWLModel().rollbackTransaction();
			Log.getLogger().log(Level.WARNING, "There were errors while creating clone of " + this, e);
			throw new RuntimeException(e);
		}
        return clone;
    }


    public RDFResource createInstance(String name) {
        return (RDFResource) getKnowledgeBase().createInstance(name, this);
    }


    protected AbstractOWLModel getAbstractOWLModel() {
        return (AbstractOWLModel) getKnowledgeBase();
    }


    public RDFSClass getDefinition() {
        for (Iterator it = getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof RDFSClass && cls.hasDirectSuperclass(this)) {
                return (RDFSClass) cls;
            }
        }
        return null;
    }


    public Collection getUnionDomainProperties() {
        return getUnionDomainProperties(false);
    }


    public Collection getPureSuperclasses() {
        Collection directSuperclasses = getDirectSuperclasses();
        Collection result = new ArrayList(directSuperclasses);
        for (Iterator it = directSuperclasses.iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof RDFSClass && cls.hasDirectSuperclass(this)) {
                result.remove(cls);
                if (cls instanceof OWLIntersectionClass) {
                    for (Iterator operands = ((OWLIntersectionClass) cls).getOperands().iterator(); operands.hasNext();) {
                        RDFSClass operand = (RDFSClass) operands.next();
                        if (operand instanceof OWLNamedClass) {
                            result.remove(operand);
                        }
                    }
                }
            }
        }
        return result;
    }


    public Object getDirectTemplateFacetValue(Slot slot, Facet facet) {
        Collection values = getDirectTemplateFacetValues(slot, facet);
        if (values.isEmpty()) {
            return null;
        }
        else {
            return values.iterator().next();
        }
    }


    public Collection getUnionDomainProperties(boolean transitive) {
        if (transitive) {
            return AbstractOWLModel.getRDFResources(getKnowledgeBase(), getKnowledgeBase().getTemplateSlots(this));
        }
        else {
            return AbstractOWLModel.getRDFResources(getKnowledgeBase(), getKnowledgeBase().getDirectTemplateSlots(this));
        }
    }


    private Collection getVisibleFrames(Collection frames) {
        Collection visibleFrames = new ArrayList();
        Iterator i = frames.iterator();
        while (i.hasNext()) {
            Frame frame = (Frame) i.next();
            if (frame.isVisible()) {
                visibleFrames.add(frame);
            }
        }
        return visibleFrames;
    }


    public Collection getDisjointClasses() {
        Slot disjointClassesSlot = getOWLModel().getOWLDisjointWithProperty();
        return getOwnSlotValues(disjointClassesSlot);
    }


    public Collection getEquivalentClasses() {
        Collection result = new ArrayList();
        for (Iterator it = getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof RDFSClass && cls.hasDirectSuperclass(this)) {
                result.add(cls);
            }
        }
        return result;
    }


    public int getInferredInstanceCount() {
        return getInferredInstances(false).size();
    }


    public Collection getInferredInstances(boolean includingSubclasses) {
        RDFProperty inferredTypesProperty = getOWLModel().getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE);
        if (includingSubclasses) {
            Collection results = new HashSet();
            results.addAll(getKnowledgeBase().getFramesWithValue(inferredTypesProperty, null, false, this));
            for (Iterator it = getSubclasses(true).iterator(); it.hasNext();) {
                RDFSClass subclass = (RDFSClass) it.next();
                results.addAll(getKnowledgeBase().getFramesWithValue(inferredTypesProperty, null, false, subclass));
            }
            return results;
        }
        else {
            return getKnowledgeBase().getFramesWithValue(inferredTypesProperty, null, false, this);
        }
    }


    public int getInstanceCount(boolean includingSubclasses) {
        if (includingSubclasses) {
            return getKnowledgeBase().getInstanceCount(this);
        }
        else {
            return getKnowledgeBase().getDirectInstanceCount(this);
        }
    }


    public Collection getInstances(boolean includingSubclasses) {
        if (includingSubclasses) {
            return getKnowledgeBase().getInstances(this);
        }
        else {
            return getKnowledgeBase().getDirectInstances(this);
        }
    }


    public Collection getNamedSubclasses() {
        return getNamedSubclasses(false);
    }


    public Collection getNamedSuperclasses() {
        return getNamedSuperclasses(false);
    }


    public Collection getNamedSubclasses(boolean transitive) {
        Collection result = new ArrayList();
        for (Iterator it = getSubclasses(transitive).iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof RDFSNamedClass) {
                result.add(cls);
            }
        }
        return result;
    }


    public Collection getNamedSuperclasses(boolean transitive) {
        Collection result = new ArrayList();
        for (Iterator it = getSuperclasses(transitive).iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof RDFSNamedClass) {
                result.add(cls);
            }
        }
        return result;
    }


    public String getParsableExpression() {
        return getBrowserText();
    }


    public int getSubclassCount() {
        if (Model.ClsID.THING.equals(getFrameID())) {
            return getSubclasses(false).size();
        }
        else {
            return getKnowledgeBase().getDirectSubclassCount(this);
        }
    }


    public Collection getSubclasses(boolean transitive) {
        if (Model.ClsID.THING.equals(getFrameID())) {
            if (transitive) {
                return AbstractOWLModel.getRDFResources(getKnowledgeBase(),
                                                        getKnowledgeBase().getSubclasses(this));
            }
            else {
                return AbstractOWLModel.getRDFResources(getKnowledgeBase(), getKnowledgeBase().getDirectSubclasses(this));
            }
        }
        else {
            if (transitive) {
                return getKnowledgeBase().getSubclasses(this);
            }
            else {
                return getKnowledgeBase().getDirectSubclasses(this);
            }
        }
    }


    public int getSuperclassCount() {
        return getSuperclasses(false).size(); // getKnowledgeBase().getDirectSuperclassCount(this);
    }


    public Collection getSuperclasses(boolean transitive) {
        if (transitive) {
            return AbstractOWLModel.getRDFResources(getKnowledgeBase(), getKnowledgeBase().getSuperclasses(this));
        }
        else {
            return AbstractOWLModel.getRDFResources(getKnowledgeBase(), getKnowledgeBase().getDirectSuperclasses(this));
        }
    }


    public boolean hasPropertyValueWithBrowserText(RDFProperty property, String browserText) {
        return hasPropertyValueWithBrowserText((Slot) property, browserText);
    }


    public boolean hasPropertyValueWithBrowserText(Slot property, String browserText) {
        for (Iterator it = getDirectOwnSlotValues(property).iterator(); it.hasNext();) {
            Frame value = (Frame) it.next();
            if (browserText.equals(value.getBrowserText())) {
                return true;
            }
        }
        return false;
    }


    public boolean hasEquivalentClass(RDFSClass other) {
        return hasDirectSuperclass(other) && other.isSubclassOf(this);
    }


    public boolean isMetaclass() {
        return getKnowledgeBase().isMetaCls(this);
    }


    public boolean isSubclassOf(RDFSClass superclass) {
        return getKnowledgeBase().hasDirectSuperclass(this, superclass);
    }


    public void removeClassListener(ClassListener listener) {
        if (!(listener instanceof ClassAdapter)) {
            throw new IllegalArgumentException("Listener must be a ClassAdapter");
        }
        removeClsListener(listener);
    }


    public void removeSuperclass(RDFSClass superclass) {
        getKnowledgeBase().removeDirectSuperclass(this, superclass);
    }

    // RDFResource implementation methods --------------------------------------------------------


    public void addComment(String comment) {
        OWLUtil.addComment(this, comment);
    }


    public void addDifferentFrom(RDFResource resource) {
        OWLUtil.addDifferentFrom(this, resource);
    }


    public void addIsDefinedBy(RDFResource instance) {
        OWLUtil.addIsDefinedBy(this, instance);
    }


    public void addLabel(String label, String language) {
        OWLUtil.addLabel(this, label, language);
    }


    public void addPropertyValue(RDFProperty property, Object value) {
        OWLUtil.addPropertyValue(this, property, value);
    }


    public void addPropertyValueListener(PropertyValueListener listener) {
        OWLUtil.addPropertyValueListener(this, listener);
    }


    public void addProtegeType(RDFSClass type) {
        OWLUtil.addProtegeType(this, type);
    }


    public void addRDFType(RDFSClass type) {
        OWLUtil.addRDFType(this, type);
    }


    public void addResourceListener(ResourceListener listener) {
        OWLUtil.addResourceListener(this, listener);
    }


    public void addSameAs(RDFResource resource) {
        OWLUtil.addSameAs(this, resource);
    }


    public void addVersionInfo(String versionInfo) {
        OWLUtil.addVersionInfo(this, versionInfo);
    }


    public RDFResource as(Class javaInterface) {
        return OWLUtil.as(this, javaInterface);
    }


    public boolean canAs(Class javaInterface) {
        return OWLUtil.canAs(this, javaInterface);
    }


    public RDFResource getAllValuesFromOnTypes(RDFProperty property) {
        return OWLUtil.getAllValuesFromOnTypes(this, property);
    }


    public Collection getComments() {
        return OWLUtil.getComments(this);
    }


    public Collection getDifferentFrom() {
        return OWLUtil.getDifferentFrom(this);
    }


    public Collection getHasValuesOnTypes(RDFProperty property) {
        return OWLUtil.getHasValuesOnTypes(this, property);
    }


    public Class getIconLocation() {
        return OWLIcons.class;
    }


    public RDFSClass getProtegeType() {
        return OWLUtil.getDirectRDFType(this);
    }


    public Collection getProtegeTypes() {
        return OWLUtil.getDirectRDFTypes(this);
    }


    public Collection getDocumentation() {
        return OWLUtil.getComments(this);
    }


    public Collection getInferredTypes() {
        return OWLUtil.getInferredDirectTypes(this);
    }


    public Collection getIsDefinedBy() {
        return OWLUtil.getIsDefinedBy(this);
    }


    public Collection getLabels() {
        return OWLUtil.getLabels(this);
    }


    public String getLocalName() {
        final String name = getName();
        final OWLModel nskb = (OWLModel) getKnowledgeBase();
        return nskb.getLocalNameForResourceName(name);
    }


    public String getNamespace() {
        final OWLModel nskb = ((OWLModel) getKnowledgeBase());
        final String name = getName();
        return nskb.getNamespaceForResourceName(name);
    }


    public String getNamespacePrefix() {
        final OWLModel nskb = ((OWLModel) getKnowledgeBase());
        String name = getName();
        return nskb.getPrefixForResourceName(name);
    }


    public OWLModel getOWLModel() {
        return (OWLModel) getKnowledgeBase();
    }


    public Collection getPossibleRDFProperties() {
        return OWLUtil.getPossibleRDFProperties(this);
    }


    public Object getPropertyValue(RDFProperty property) {
        return OWLUtil.getPropertyValue(this, property, false);
    }


    public RDFResource getPropertyValueAs(RDFProperty property, Class javaInterface) {
        return OWLUtil.getPropertyValueAs(this, property, javaInterface);
    }


    public Object getPropertyValue(RDFProperty property, boolean includingSubproperties) {
        return OWLUtil.getPropertyValue(this, property, includingSubproperties);
    }


    public int getPropertyValueCount(RDFProperty property) {
        return OWLUtil.getPropertyValueCount(this, property);
    }


    public RDFSLiteral getPropertyValueLiteral(RDFProperty property) {
        return OWLUtil.getPropertyValueLiteral(this, property);
    }


    public Collection getPropertyValueLiterals(RDFProperty property) {
        return OWLUtil.getPropertyValueLiterals(this, property);
    }


    public Collection getPropertyValues(RDFProperty property) {
        return OWLUtil.getPropertyValues(this, property, false);
    }


    public Collection getPropertyValuesAs(RDFProperty property, Class javaInterface) {
        return OWLUtil.getPropertyValuesAs(this, property, javaInterface);
    }


    public Collection getPropertyValues(RDFProperty property, boolean includingSubproperties) {
        return OWLUtil.getPropertyValues(this, property, includingSubproperties);
    }


    public Set getReferringAnonymousClasses() {
        return OWLUtil.getReferringAnonymousClses(this);
    }


    public Collection getRDFProperties() {
        return OWLUtil.getRDFProperties(this);
    }


    public RDFSClass getRDFType() {
        return OWLUtil.getRDFType(this);
    }


    public Collection getRDFTypes() {
        return OWLUtil.getRDFTypes(this);
    }


    public Collection getSameAs() {
        return OWLUtil.getSameAs(this);
    }


    public String getURI() {
        return getOWLModel().getURIForResourceName(getName());
    }


    public Collection getVersionInfo() {
        return OWLUtil.getVersionInfo(this);
    }


    public boolean hasPropertyValue(RDFProperty property) {
        return OWLUtil.hasPropertyValue(this, property);
    }


    public boolean hasPropertyValue(RDFProperty property, boolean includingSubproperties) {
        return OWLUtil.hasPropertyValue(this, property, includingSubproperties);
    }


    public boolean hasPropertyValue(RDFProperty property, Object value) {
        return hasPropertyValue(property, value, false);
    }


    public boolean hasPropertyValue(RDFProperty property, Object value, boolean includingSuperproperties) {
        return OWLUtil.hasPropertyValue(this, property, value, includingSuperproperties);
    }


    public boolean hasProtegeType(RDFSClass type) {
        return OWLUtil.hasProtegeType(this, type);
    }


    public boolean hasProtegeType(RDFSClass type, boolean includingSuperclasses) {
        return OWLUtil.hasProtegeType(this, type, includingSuperclasses);
    }


    public boolean hasRDFType(RDFSClass type) {
        return OWLUtil.hasRDFType(this, type);
    }


    public boolean hasRDFType(RDFSClass type, boolean includingSuperclasses) {
        return OWLUtil.hasRDFType(this, type, includingSuperclasses);
    }


    public boolean isValidPropertyValue(RDFProperty property, Object object) {
        return OWLUtil.isValidPropertyValue(this, property, object);
    }


    public Iterator listPropertyValues(RDFProperty property) {
        return OWLUtil.listPropertyValues(this, property, false);
    }


    public Iterator listPropertyValuesAs(RDFProperty property, Class javaInterface) {
        return OWLUtil.listPropertyValuesAs(this, property, javaInterface);
    }


    public Iterator listPropertyValues(RDFProperty property, boolean includingSubproperties) {
        return OWLUtil.listPropertyValues(this, property, includingSubproperties);
    }


    public Iterator listRDFTypes() {
        return getRDFTypes().iterator();
    }

    //public boolean isSystem() {
    //    return OWLUtil.isSystem(this);
    //}


    public void removeComment(String value) {
        OWLUtil.removeComment(this, value);
    }


    public void removeDifferentFrom(RDFResource resource) {
        OWLUtil.removeDifferentFrom(this, resource);
    }


    public void removeIsDefinedBy(RDFResource resource) {
        OWLUtil.removeIsDefinedBy(this, resource);
    }


    public void removeLabel(String label, String language) {
        OWLUtil.removeLabel(this, label, language);
    }


    public void removePropertyValue(RDFProperty property, Object value) {
        OWLUtil.removePropertyValue(this, property, value);
    }


    public void removePropertyValueListener(PropertyValueListener listener) {
        OWLUtil.removePropertyValueListener(this, listener);
    }


    public void removeProtegeType(RDFSClass type) {
        OWLUtil.removeProtegeType(this, type);
    }


    public void removeRDFType(RDFSClass type) {
        OWLUtil.removeRDFType(this, type);
    }


    public void removeResourceListener(ResourceListener listener) {
        OWLUtil.removeResourceListener(this, listener);
    }


    public void removeSameAs(RDFResource resource) {
        OWLUtil.removeSameAs(this, resource);
    }


    public void removeVersionInfo(String versionInfo) {
        OWLUtil.removeVersionInfo(this, versionInfo);
    }


    public void setComment(String comment) {
        OWLUtil.setComment(this, comment);
    }


    public void setComments(Collection comments) {
        OWLUtil.setComments(this, comments);
    }


    public void setDocumentation(String value) {
        OWLUtil.setComment(this, value);
    }


    public void setInferredTypes(Collection types) {
        OWLUtil.setInferredTypes(this, types);
    }


    public void setPropertyValue(RDFProperty property, Object value) {
        OWLUtil.setPropertyValue(this, property, value);
    }


    public void setPropertyValues(RDFProperty property, Collection values) {
        OWLUtil.setPropertyValues(this, property, values);
    }


    public void setProtegeType(RDFSClass type) {
        OWLUtil.setProtegeType(this, type);
    }


    public void setRDFType(RDFSClass type) {
        OWLUtil.setRDFType(this, type);
    }


    public void setRDFTypes(Collection types) {
        OWLUtil.setRDFTypes(this, types);
    }


    public void setProtegeTypes(Collection types) {
        OWLUtil.setProtegeTypes(this, types);
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();       
        buffer.append(this.getClass().getSimpleName());
        buffer.append("(");
        buffer.append(getName());
        buffer.append(", ");
        buffer.append(getFrameID());
        buffer.append(")");
        return buffer.toString();    	
    }
}
