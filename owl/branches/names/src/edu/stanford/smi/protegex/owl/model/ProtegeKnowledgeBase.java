package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.framestore.FrameStore;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ProtegeKnowledgeBase extends KnowledgeBase {

    /**
     * @see OWLModel#addClassListener
     * @deprecated
     */
    void addClsListener(ClsListener listener);


    /**
     * @see RDFSClass#addClassListener
     * @deprecated
     */
    void addClsListener(Cls cls, ClsListener listener);


    /**
     * @see RDFResource#addPropertyValueListener
     * @deprecated not needed
     */
    void addFrameListener(Frame frame, FrameListener listener);


    /**
     * @see OWLModel#addPropertyValueListener
     * @deprecated
     */
    void addFrameListener(FrameListener listener);


    /**
     * @see RDFResource#addResourceListener
     * @deprecated
     */
    void addInstanceListener(Instance instance, InstanceListener listener);


    /**
     * @see OWLModel#addResourceListener
     * @deprecated
     */
    void addInstanceListener(InstanceListener listener);


    /**
     * @see OWLModel#addModelListener
     * @deprecated
     */
    void addKnowledgeBaseListener(KnowledgeBaseListener listener);


    /**
     * @see RDFProperty#addPropertyListener
     * @deprecated
     */
    void addSlotListener(Slot slot, SlotListener listener);


    /**
     * @see OWLModel#addPropertyListener
     * @deprecated
     */
    void addSlotListener(SlotListener listener);


    /**
     * @deprecated this method is based on facets which are not supported in OWL
     */
    boolean areValidOwnSlotValues(Frame frame, Slot slot, Collection values);


    /**
     * @deprecated not needed
     */
    boolean containsFrame(String name);


    /**
     * @see OWLModel#createRDFSNamedClass
     * @see OWLModel#createOWLNamedClass
     * @deprecated
     */
    Cls createCls(String name, Collection parents);


    /**
     * @see RDFSNamedClass#createInstance
     * @deprecated
     */
    Cls createCls(String name, Collection parents, Cls metaCls);


    /**
     * @deprecated not needed in OWL
     */
    Cls createCls(String name, Collection parents, Cls metaCls, boolean initializeDefaults);


    /**
     * @deprecated not needed in OWL
     */
    Cls createCls(FrameID id, String name, Collection parents, Collection metaClses, boolean initializeDefaults);


    /**
     * @deprecated no OWL equivalent
     */
    Facet createFacet(String name);


    /**
     * @param name Pass null to cause the system to generate a name
     */
    Facet createFacet(String name, Cls metaCls);


    /**
     * @deprecated no OWL equivalent
     */
    Facet createFacet(String name, Cls metaCls, boolean initializeDefaults);


    /**
     * @see RDFSClass#createInstance
     * @deprecated
     */
    Instance createInstance(String name, Cls directType);


    /**
     * @deprecated not needed
     */
    Instance createInstance(String name, Cls directType, boolean initializeDefaults);


    /**
     * @deprecated not needed
     */
    Instance createInstance(FrameID id, Cls directType, boolean initializeDefaults);


    /**
     * @deprecated not needed
     */
    Instance createInstance(FrameID id, Collection directTypes, boolean initializeDefaults);


    /**
     * @see RDFSClass#createInstance
     * @deprecated
     */
    SimpleInstance createSimpleInstance(FrameID id, Collection directTypes, boolean initializeDefaults);


    /**
     * @see OWLModel#createRDFProperty
     * @see OWLModel#createOWLDatatypeProperty
     * @see OWLModel#createOWLObjectProperty
     * @deprecated
     */
    Slot createSlot(String name);


    /**
     * @see RDFSClass#createInstance
     * @deprecated
     */
    Slot createSlot(String name, Cls metaCls);


    /**
     * @deprecated not needed
     */
    Slot createSlot(String name, Cls metaCls, boolean initializeDefaults);


    /**
     * @see OWLModel#createSubproperty
     * @deprecated
     */
    Slot createSlot(String name, Cls metaCls, Collection superslots, boolean initializeDefaults);


    /**
     * @see RDFResource#delete
     * @deprecated
     */
    void deleteCls(Cls cls);


    /**
     * @deprecated not needed in OWL
     */
    void deleteFacet(Facet facet);


    /**
     * @see RDFResource#delete
     * @deprecated
     */
    void deleteFrame(Frame frame);


    /**
     * @see RDFResource#delete
     * @deprecated
     */
    void deleteInstance(Instance instance);


    /**
     * @see RDFResource#delete
     * @deprecated
     */
    void deleteSlot(Slot slot);


    /**
     * @see OWLModel#getRDFSNamedClass
     * @deprecated
     */
    Cls getCls(String name);


    /**
     * @see OWLModel#getRDFSClassCount
     * @deprecated
     */
    int getClsCount();


    /**
     * @see OWLModel#getRDFSClasses
     * @deprecated
     */
    Collection getClses();


    /**
     * Get classes whose name matches the give string.  This string allows "*" for "match any sequence" of characters.
     * The string is not a regular expression.  The matching is case-insensitive.
     */
    Collection getClsNameMatches(String s, int maxMatches);


    /**
     * @deprecated
     */
    Cls getDefaultClsMetaCls();


    /**
     * @deprecated not supported in OWL
     */
    Cls getDefaultFacetMetaCls();


    /**
     * @deprecated
     */
    Cls getDefaultSlotMetaCls();


    /**
     * @deprecated not supported in OWL
     */
    Facet getFacet(String name);


    /**
     * @deprecated not supported in OWL
     */
    int getFacetCount();


    /**
     * @return
     * @deprecated not supported in OWL
     */
    Collection getFacets();


    /**
     * @see OWLModel#getRDFResource
     * @deprecated
     */
    Frame getFrame(String name);


    /**
     * @deprecated not needed
     */
    Frame getFrame(FrameID id);


    /**
     * @see OWLModel#getRDFResourceCount
     * @deprecated
     */
    int getFrameCount();


    /**
     * @deprecated not needed
     */
    String getFrameCreationTimestamp(Frame frame);


    /**
     * @deprecated not needed
     */
    String getFrameCreator(Frame frame);


    /**
     * @deprecated not needed
     */
    String getFrameLastModificationTimestamp(Frame frame);


    /**
     * @deprecated not needed
     */
    String getFrameLastModifier(Frame frame);


    /**
     * @see OWLModel#getResourceNameMatches
     * @deprecated
     */
    Collection getFrameNameMatches(String s, int maxMatches);


    /**
     * @deprecated not supported in OWL
     */
    String getFrameNamePrefix();


    /**
     * @see OWLModel#getRDFResources
     * @deprecated
     */
    Collection getFrames();


    /**
     * @see OWLModel#getRDFResourcesWithPropertyValue
     * @deprecated
     */
    Collection getFramesWithValue(Slot slot, Facet facet, boolean isTemplate, Object value);


    /**
     * @see OWLModel#getRDFResource
     * @deprecated
     */
    Instance getInstance(String fullname);


    /**
     * @see OWLModel#getRDFResources
     * @deprecated
     */
    Collection getInstances();


    /**
     * @see RDFSClass#getInstances
     * @deprecated
     */
    Collection getInstances(Cls cls);


    /**
     * @deprecated not supported
     */
    String getInvalidOwnSlotValuesText(Frame frame, Slot slot, Collection values);


    /**
     * @deprecated not supported
     */
    String getInvalidOwnSlotValueText(Frame frame, Slot slot, Object value);


    /**
     * @see OWLModel#getMatchingResources
     * @deprecated
     */
    Collection getMatchingFrames(Slot slot, Facet facet, boolean isTemplate, String matchString, int maxMatches);


    /**
     * @deprecated not needed
     */
    String getName();


    /**
     * @deprecated not needed
     */
    int getNextFrameNumber();


    /**
     * Gets the Protege project of this.
     *
     * @return the Protege project
     */
    Project getProject();


    /**
     * @deprecated not needed
     */
    Collection getReachableSimpleInstances(Collection roots);


    /**
     * @deprecated
     * @see OWLModel#listReferences
     */
    Collection getReferences(Object o, int maxReferences);


    /**
     * @deprecated
     */
    Collection getMatchingReferences(String s, int maxReferences);


    /**
     * @see OWLModel#getOWLThingClass
     * @deprecated
     */
    Cls getRootCls();


    /**
     * @deprecated not needed
     */
    Collection getRootClses();


    /**
     * @deprecated not needed
     */
    Cls getRootClsMetaCls();


    /**
     * @deprecated not needed
     */
    Cls getRootFacetMetaCls();


    /**
     * @deprecated not needed
     */
    Cls getRootSlotMetaCls();


    /**
     * @deprecated not needed
     */
    Collection getRootSlots();


    /**
     * @see OWLModel#getRDFProperty
     * @deprecated
     */
    Slot getSlot(String name);


    /**
     * @deprecated not needed
     */
    int getSlotCount();


    /**
     * @see OWLModel#getRDFProperties
     * @deprecated
     */
    Collection getSlots();


    /**
     * @deprecated not needed
     */
    String getSlotValueLastModificationTimestamp(Frame frame, Slot slot, boolean isTemplate);


    /**
     * @deprecated not needed
     */
    String getSlotValueLastModifier(Frame frame, Slot slot, boolean isTemplate);


    /**
     * @see RDFSClass#getSubclasses
     * @deprecated
     */
    Collection getSubclasses(Cls cls);


    /**
     * @deprecated not needed
     */
    Collection getUnreachableSimpleInstances(Collection roots);


    /**
     * @deprecated not needed
     */
    String getUserName();


    /**
     * @deprecated not needed
     */
    String getVersionString();


    /**
     * @deprecated not supported in OWL
     */
    boolean isAutoUpdatingFacetValues();


    /**
     * @deprecated not needed
     */
    boolean isClsMetaCls(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean isDefaultClsMetaCls(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean isDefaultFacetMetaCls(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean isDefaultSlotMetaCls(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean isFacetMetaCls(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean isLoading();


    /**
     * @deprecated not needed
     */
    boolean isSlotMetaCls(Cls cls);


    /**
     * @deprecated not supported in OWL
     */
    boolean isValidOwnSlotValue(Frame frame, Slot slot, Object value);


    /**
     * @see RDFSClass#removeClassListener
     * @deprecated
     */
    void removeClsListener(Cls cls, ClsListener listener);


    /**
     * @see OWLModel#removeClassListener
     * @deprecated
     */
    void removeClsListener(ClsListener listener);


    /**
     * @deprecated not needed
     */
    void removeFrameListener(Frame frame, FrameListener listener);


    /**
     * @see OWLModel#removePropertyValueListener
     * @deprecated
     */
    void removeFrameListener(FrameListener listener);


    /**
     * @deprecated not needed
     */
    void removeJavaLoadPackage(String path);


    /**
     * @see OWLModel#removeModelListener
     * @deprecated
     */
    void removeKnowledgeBaseListener(KnowledgeBaseListener listener);


    /**
     * @see RDFResource#removeResourceListener
     * @deprecated
     */
    void removeInstanceListener(Instance instance, InstanceListener listener);


    /**
     * @see OWLModel#removeResourceListener
     * @deprecated
     */
    void removeInstanceListener(InstanceListener listener);


    /**
     * @see RDFProperty#removePropertyListener
     * @deprecated
     */
    void removeSlotListener(Slot slot, SlotListener listener);


    /**
     * @see OWLModel#removePropertyListener
     * @deprecated
     */
    void removeSlotListener(SlotListener listener);


    /**
     * @deprecated not supported in OWL
     */
    void setAutoUpdateFacetValues(boolean b);


    /**
     * @deprecated not needed
     */
    void setBuildString(String s);


    /**
     * @deprecated not needed
     */
    void setChanged(boolean b);


    /**
     * @deprecated not needed
     */
    void setDefaultClsMetaCls(Cls cls);


    /**
     * @deprecated not needed
     */
    void setDefaultFacetMetaCls(Cls cls);


    /**
     * @deprecated not needed
     */
    void setDefaultSlotMetaCls(Cls cls);

    boolean setUndoEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    boolean setGenerateDeletingFrameEventsEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    void setPollForEvents(boolean enabled);


    /**
     * @deprecated not needed
     */
    boolean setJournalingEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    boolean isJournalingEnabled();


    /**
     * @deprecated not needed
     */
    boolean setArgumentCheckingEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    boolean setChangeMonitorEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    boolean setCleanDispatchEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    boolean setFacetCheckingEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    void setFrameNamePrefix(String name);


    /**
     * @deprecated not needed
     */
    void setName(String name);


    /**
     * @deprecated not needed
     */
    void setNextFrameNumber(int i);


    void setProject(Project project);


    /**
     * @deprecated not needed
     */
    void setUserName(String name);


    /**
     * @deprecated not needed
     */
    void setValueChecking(boolean b);


    /**
     * @deprecated not needed
     */
    void setVersionString(String s);


    /**
     * @see RDFResource#addPropertyValue
     * @deprecated
     */
    void addOwnSlotValue(Frame frame, Slot slot, Object value);


    /**
     * @see RDFResource#getComments
     * @deprecated
     */
    Collection getDocumentation(Frame frame);


    /**
     * @deprecated not needed
     */
    String getName(Frame frame);


    /**
     * @deprecated not needed
     */
    boolean getOwnSlotAllowsMultipleValues(Frame frame, Slot slot);


    /**
     * @see RDFResource#getPropertyValues(RDFProperty, boolean)
     * @deprecated
     */
    Collection getOwnSlotAndSubslotValues(Frame frame, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getOwnSlotDefaultValues(Frame frame, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getOwnSlotFacets(Frame frame, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getOwnSlotFacetValues(Frame frame, Slot slot, Facet facet);


    /**
     * @see RDFResource#getPossibleRDFProperties
     * @deprecated
     */
    Collection getOwnSlots(Frame frame);


    /**
     * @see RDFResource#getPropertyValues
     * @deprecated
     */
    Collection getOwnSlotValues(Frame frame, Slot slot);


    /**
     * @see RDFResource#getPropertyValue
     * @deprecated
     */
    Object getDirectOwnSlotValue(Frame frame, Slot slot);


    /**
     * @see RDFResource#getPropertyValues
     * @deprecated
     */
    List getDirectOwnSlotValues(Frame frame, Slot slot);


    /**
     * @see RDFResource#getPropertyValues
     * @deprecated
     */
    Object getOwnSlotValue(Frame frame, Slot slot);


    /**
     * @see RDFResource#getPropertyValueCount
     * @deprecated
     */
    int getOwnSlotValueCount(Frame frame, Slot slot);


    /**
     * @deprecated not needed
     */
    ValueType getOwnSlotValueType(Frame frame, Slot slot);


    /**
     * @deprecated not needed (get domain of rdf:type instead)
     */
    boolean hasOwnSlot(Frame frame, Slot slot);


    /**
     * @see RDFResource#removePropertyValue
     * @deprecated
     */
    void removeOwnSlotValue(Frame frame, Slot slot, Object value);


    /**
     * @see RDFResource#setComment(String)
     * @deprecated
     */
    void setDocumentation(Frame frame, String text);


    /**
     * @see RDFResource#setComments
     * @deprecated
     */
    void setDocumentation(Frame frame, Collection text);


    /**
     * @see RDFResource#setPropertyValues
     * @deprecated
     */
    void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values);


    /**
     * @see RDFResource#setPropertyValues
     * @deprecated
     */
    void setOwnSlotValues(Frame frame, Slot slot, Collection values);


    /**
     * @deprecated not needed
     */
    void notifyVisibilityChanged(Frame frame);


    /**
     * @deprecated not needed
     */
    void addFacetListener(FacetListener listener);


    /**
     * @deprecated not needed
     */
    void removeFacetListener(FacetListener listener);


    /**
     * @deprecated not needed
     */
    void addFacetListener(Facet facet, FacetListener listener);


    /**
     * @deprecated not needed
     */
    void removeFacetListener(Facet facet, FacetListener listener);


    /**
     * @deprecated not needed
     */
    Slot getAssociatedSlot(Facet facet);


    /**
     * @deprecated not needed
     */
    void setAssociatedSlot(Facet facet, Slot slot);


    /**
     * @see RDFSClass#addSuperclass
     * @deprecated
     */
    void addDirectSuperclass(Cls cls, Cls superclass);


    /**
     * @see RDFSClass#removeSuperclass
     * @deprecated
     */
    void removeDirectSuperclass(Cls cls, Cls superclass);


    /**
     * @see RDFProperty#addUnionDomainClass
     * @deprecated
     */
    void addDirectTemplateSlot(Cls cls, Slot slot);


    /**
     * @see RDFProperty#removeUnionDomainClass
     * @deprecated
     */
    void removeDirectTemplateSlot(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    void addTemplateFacetValue(Cls cls, Slot slot, Facet facet, Object value);


    /**
     * @deprecated not supported in OWL
     */
    void addTemplateSlotValue(Cls cls, Slot slot, Object value);


    /**
     * @deprecated not supported in OWL
     */
    Slot getNameSlot();


    /**
     * @see RDFSClass#getInstanceCount
     * @deprecated
     */
    int getDirectInstanceCount(Cls cls);


    /**
     * @see RDFSClass#getInstances
     * @deprecated
     */
    Collection getDirectInstances(Cls cls);


    /**
     * @see RDFSClass#getSubclassCount
     * @deprecated
     */
    int getDirectSubclassCount(Cls cls);


    /**
     * @see RDFSClass#getSubclasses
     * @deprecated
     */
    Collection getDirectSubclasses(Cls cls);


    /**
     * @see RDFSClass#getSuperclassCount
     * @deprecated
     */
    int getDirectSuperclassCount(Cls cls);


    /**
     * @see RDFSClass#getSuperclasses
     * @deprecated
     */
    Collection getDirectSuperclasses(Cls cls);


    /**
     * @deprecated not supported in OWL
     */
    List getDirectTemplateFacetValues(Cls cls, Slot slot, Facet facet);


    /**
     * @see RDFSClass#getUnionDomainProperties
     * @deprecated
     */
    Collection getDirectTemplateSlots(Cls cls);


    /**
     * @deprecated not supported in OWL
     */
    List getDirectTemplateSlotValues(Cls cls, Slot slot);


    /**
     * @see RDFSClass#getInstanceCount
     * @deprecated
     */
    int getInstanceCount(Cls cls);


    /**
     * @deprecated not needed
     */
    int getSimpleInstanceCount();


    /**
     * @see RDFSClass#getSuperclasses
     * @deprecated
     */
    Collection getSuperclasses(Cls cls);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateFacets(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Object getTemplateFacetValue(Cls cls, Slot slot, Facet facet);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateFacetValues(Cls cls, Slot slot, Facet facet);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateSlotAllowedClses(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateSlotAllowedParents(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateSlotAllowedValues(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    boolean getTemplateSlotAllowsMultipleValues(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateSlotDefaultValues(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateSlotDocumentation(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    int getTemplateSlotMaximumCardinality(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Number getTemplateSlotMaximumValue(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    int getTemplateSlotMinimumCardinality(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Number getTemplateSlotMinimumValue(Cls cls, Slot slot);


    /**
     * @see RDFSClass#getUnionDomainProperties(boolean)
     * @deprecated
     */
    Collection getTemplateSlots(Cls cls);


    /**
     * @deprecated not supported in OWL
     */
    Object getTemplateSlotValue(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getTemplateSlotValues(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    ValueType getTemplateSlotValueType(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    boolean hasDirectlyOverriddenTemplateFacet(Cls cls, Slot slot, Facet facet);


    /**
     * @deprecated not supported in OWL
     */
    boolean hasDirectlyOverriddenTemplateSlot(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getDirectlyOverriddenTemplateSlots(Cls cls);


    /**
     * @deprecated not supported in OWL
     */
    Collection getDirectlyOverriddenTemplateFacets(Cls cls, Slot slot);


    /**
     * @deprecated not needed
     */
    boolean hasDirectSuperclass(Cls cls, Cls superclass);


    /**
     * @deprecated not needed
     */
    boolean hasDirectSuperslot(Slot slot, Slot superslot);


    /**
     * @deprecated not needed
     */
    boolean hasSuperslot(Slot slot, Slot superslot);


    /**
     * @deprecated not supported in OWL
     */
    void moveDirectSubslot(Slot slot, Slot subslot, Slot afterSlot);


    /**
     * @deprecated not supported in OWL
     */
    void moveDirectTemplateSlot(Cls cls, Slot slot, int toIndex);


    /**
     * @see RDFSClass#getUnionDomainProperties
     * @deprecated
     */
    boolean hasDirectTemplateSlot(Cls cls, Slot slot);


    /**
     * @see RDFSClass#getUnionDomainProperties
     * @deprecated
     */
    boolean hasInheritedTemplateSlot(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    boolean hasOverriddenTemplateSlot(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    boolean hasOverriddenTemplateFacet(Cls cls, Slot slot, Facet facet);


    /**
     * @see RDFSClass#hasSuperclass
     * @deprecated
     */
    boolean hasSuperclass(Cls cls, Cls superclass);


    /**
     * @deprecated
     */
    boolean hasTemplateSlot(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    boolean isAbstract(Cls cls);


    /**
     * @deprecated not needed
     */
    boolean isMetaCls(Cls cls);


    /**
     * @deprecated not supported in OWL
     */
    void moveDirectSubclass(Cls cls, Cls subclass, Cls afterclass);


    /**
     * @deprecated not supported in OWL
     */
    void removeTemplateFacetOverrides(Cls cls, Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    void setAbstract(Cls cls, boolean isAbstract);


    /**
     * @deprecated not needed
     */
    void setDirectTypeOfSubclasses(Cls cls, Cls type);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateFacetValue(Cls cls, Slot slot, Facet facet, Object value);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateFacetValues(Cls cls, Slot slot, Facet facet, Collection values);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotAllowedClses(Cls cls, Slot slot, Collection values);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotAllowedParents(Cls cls, Slot slot, Collection values);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotAllowedValues(Cls cls, Slot slot, Collection values);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotAllowsMultipleValues(Cls cls, Slot slot, boolean allowsMultiple);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotDefaultValues(Cls cls, Slot slot, Collection values);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotDocumentation(Cls cls, Slot slot, String doc);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotDocumentation(Cls cls, Slot slot, Collection docs);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotMaximumCardinality(Cls cls, Slot slot, int value);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotMaximumValue(Cls cls, Slot slot, Number value);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotMinimumCardinality(Cls cls, Slot slot, int value);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotMinimumValue(Cls cls, Slot slot, Number value);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotValue(Cls cls, Slot slot, Object value);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotValues(Cls cls, Slot slot, Collection value);


    /**
     * @deprecated not supported by OWL
     */
    void setTemplateSlotValueType(Cls cls, Slot slot, ValueType type);


    /**
     * @see RDFResource#addProtegeType
     * @deprecated
     */
    void addInstance(Instance instance, String name, Cls type, boolean isNew);


    /**
     * @see RDFResource#getBrowserText
     * @deprecated
     */
    String getBrowserText(Instance instance);


    /**
     * @see RDFResource#getProtegeType
     * @deprecated
     */
    Cls getDirectType(Instance instance);


    /**
     * @see RDFResource#getProtegeTypes
     * @deprecated
     */
    Collection getDirectTypes(Instance instance);


    /**
     * @see RDFResource#hasProtegeType
     * @deprecated
     */
    boolean hasDirectType(Instance instance, Cls cls);


    /**
     * @see RDFResource#hasProtegeType
     * @deprecated
     */
    boolean hasType(Instance instance, Cls cls);


    /**
     * @see RDFResource#setProtegeType
     * @deprecated
     */
    Instance setDirectType(Instance instance, Cls cls);


    /**
     * @see RDFResource#setProtegeTypes
     * @deprecated
     */
    Instance setDirectTypes(Instance instance, Collection types);


    /**
     * @see RDFProperty#addSuperproperty
     * @deprecated
     */
    void addDirectSuperslot(Slot slot, Slot superslot);


    /**
     * @see RDFProperty#getUnionRangeClasses
     * @deprecated
     */
    Collection getAllowedClses(Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getAllowedParents(Slot slot);


    /**
     * @see RDFProperty#getRange
     * @deprecated
     */
    Collection getAllowedValues(Slot slot);


    /**
     * @see RDFProperty#isFunctional
     * @deprecated
     */
    boolean getAllowsMultipleValues(Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Facet getAssociatedFacet(Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getDefaultValues(Slot slot);


    /**
     * @see RDFProperty#getSubpropertyCount
     * @deprecated
     */
    int getDirectSubslotCount(Slot slot);


    /**
     * @see RDFProperty#getSubproperties
     * @deprecated
     */
    Collection getDirectSubslots(Slot slot);


    /**
     * @see RDFProperty#getSuperproperties
     * @deprecated
     */
    Collection getDirectSuperslots(Slot slot);


    /**
     * @see RDFProperty#getSuperpropertyCount
     * @deprecated
     */
    int getDirectSuperslotCount(Slot slot);


    /**
     * @see RDFProperty#getInverseProperty
     * @deprecated
     */
    Slot getInverseSlot(Slot slot);


    /**
     * @see RDFProperty#isFunctional
     * @deprecated
     */
    int getMaximumCardinality(Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Number getMaximumValue(Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    int getMinimumCardinality(Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Number getMinimumValue(Slot slot);


    /**
     * @see RDFProperty#getSubproperties
     * @deprecated
     */
    Collection getSubslots(Slot slot);


    /**
     * @see RDFProperty#getSuperproperties
     * @deprecated
     */
    Collection getSuperslots(Slot slot);


    /**
     * @see RDFProperty#getUnionDomain
     * @deprecated
     */
    Collection getDirectDomain(Slot slot);


    /**
     * @see RDFProperty#getUnionDomain(boolean)
     * @deprecated
     */
    Collection getDomain(Slot slot);


    /**
     * @deprecated not supported in OWL
     */
    Collection getValues(Slot slot);


    /**
     * @see RDFProperty#getRange
     * @deprecated
     */
    ValueType getValueType(Slot slot);


    /**
     * @deprecated not needed
     */
    boolean hasSlotValueAtSomeFrame(Slot slot);


    /**
     * @see RDFProperty#removeSuperproperty
     * @deprecated
     */
    void removeDirectSuperslot(Slot slot, Slot superslot);


    /**
     * @see RDFProperty#setUnionRangeClasses
     * @deprecated
     */
    void setAllowedClses(Slot slot, Collection clses);


    /**
     * @deprecated not supported in OWL
     */
    void setAllowedParents(Slot slot, Collection parents);


    /**
     * @deprecated not supported in OWL
     */
    void setAllowedValues(Slot slot, Collection values);


    /**
     * @see RDFProperty#setFunctional
     * @deprecated
     */
    void setAllowsMultipleValues(Slot slot, boolean allowsMultiple);


    /**
     * @deprecated not supported in OWL
     */
    void setAssociatedFacet(Slot slot, Facet facet);


    /**
     * @deprecated not supported in OWL
     */
    void setDefaultValues(Slot slot, Collection values);


    /**
     * @deprecated not needed
     */
    void setDirectTypeOfSubslots(Slot slot, Cls type);


    /**
     * @see RDFProperty#setInverseProperty
     * @deprecated
     */
    void setInverseSlot(Slot slot, Slot inverseSlot);


    /**
     * @see RDFProperty#setFunctional
     * @deprecated
     */
    void setMaximumCardinality(Slot slot, int max);


    /**
     * @deprecated not supported in OWL
     */
    void setMaximumValue(Slot slot, Number max);


    /**
     * @deprecated not supported in OWL
     */
    void setMinimumCardinality(Slot slot, int max);


    /**
     * @deprecated not supported in OWL
     */
    void setMinimumValue(Slot slot, Number min);


    /**
     * @deprecated not supported in OWL
     */
    void setValues(Slot slot, Collection values);


    /**
     * @see RDFProperty#setRange
     * @deprecated
     */
    void setValueType(Slot slot, ValueType type);


    /**
     * @deprecated not supported in OWL
     */
    Collection getOverriddenTemplateFacets(Cls cls, Slot slot);


    /**
     * @deprecated not needed
     */
    Collection getCurrentUsers();


    /**
     * @see RDFResource#addProtegeType
     * @deprecated
     */
    void addDirectType(Instance instance, Cls directType);


    /**
     * @see RDFResource#removeProtegeType
     * @deprecated
     */
    void removeDirectType(Instance instance, Cls directType);


    /**
     * @deprecated not needed
     */
    CommandManager getCommandManager();


    /**
     * @deprecated not needed
     */
    void setFrameNameValidator(FrameNameValidator validator);


    /**
     * @see OWLModel#isValidResourceName
     * @deprecated
     */
    boolean isValidFrameName(String s, Frame frame);


    /**
     * @deprecated not needed
     */
    String getInvalidFrameNameDescription(String s, Frame frame);


    /**
     * @see RDFSClass#setDirectBrowserSlot
     * @deprecated
     */
    void setDirectBrowserSlot(Cls cls, Slot slot);


    /**
     * @deprecated internal Protege detail
     */
    void insertFrameStore(FrameStore newFrameStore, int position);


    /**
     * @deprecated internal Protege detail
     */
    void insertFrameStore(FrameStore newFrameStore);


    /**
     * @deprecated internal Protege detail
     */
    void removeFrameStore(FrameStore frameStore);


    /**
     * @deprecated internal Protege detail
     */
    List getFrameStores();


    /**
     * @deprecated internal Protege detail
     */
    SystemFrames getSystemFrames();


    /**
     * @deprecated whatever this does
     */
    void clearAllListeners();


    /**
     * @deprecated not needed
     */
    FrameCounts getFrameCounts();


    /**
     * @see RDFSClass#setDirectBrowserSlotPattern
     * @deprecated
     */
    void setDirectBrowserSlotPattern(Cls cls, BrowserSlotPattern pattern);


    /**
     * @see RDFSClass#getDirectBrowserSlotPattern
     * @deprecated
     */
    BrowserSlotPattern getDirectBrowserSlotPattern(Cls cls);


    /**
     * @deprecated not needed
     */
    Set getDirectOwnSlotValuesClosure(Frame frame, Slot slot);


    /**
     * @deprecated not needed
     */
    boolean setCallCachingEnabled(boolean enabled);


    /**
     * @deprecated not needed
     */
    boolean isCallCachingEnabled();


    /**
     * @deprecated not needed
     */
    boolean getValueChecking();


    /**
     * @deprecated not needed
     */
    void startJournaling(URI uri);


    /**
     * @deprecated not needed
     */
    void stopJournaling();


    /**
     * @see OWLModel#getOWLJavaFactory
     * @deprecated
     */
    FrameFactory getFrameFactory();


    /**
     * @see OWLModel#setOWLJavaFactory
     * @deprecated
     */
    void setFrameFactory(FrameFactory factory);
}
