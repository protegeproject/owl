package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.*;

/**
 * The default implementation of the OWLProperty interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultRDFProperty extends DefaultSlot implements RDFProperty {


    public DefaultRDFProperty(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultRDFProperty() {
    }


    public void addPropertyListener(PropertyListener listener) {
        if (!(listener instanceof PropertyAdapter)) {
            throw new IllegalArgumentException("Listener must be subclass of PropertyAdapter");
        }
        addSlotListener(listener);
    }


    public void addSuperproperty(RDFProperty superProperty) {
        getKnowledgeBase().addDirectSuperslot(this, superProperty);
    }


    public void addEquivalentProperty(RDFProperty property) {
        Slot equivalentClassesSlot = getOWLModel().getOWLEquivalentPropertyProperty();
        addOwnSlotValue(equivalentClassesSlot, property);
    }


    public void addUnionDomainClass(RDFSClass domainClass) {
        Collection directDomain = getDirectDomain();
        if (directDomain.isEmpty()) {
            setDomain(domainClass);
        }
        else {
            OWLUnionClass unionClass = getOWLModel().createOWLUnionClass(directDomain);
            unionClass.addOperand(domainClass);
            setDomain(unionClass);
        }
        // ((Cls) domainClass).addDirectTemplateSlot(this);
    }


    public boolean equalsStructurally(RDFObject object) {
        return equals(object);
    }


    public ImageIcon getBaseImageIcon() {
        return OWLIcons.getImageIcon(OWLIcons.RDF_PROPERTY);
    }


    public RDFSClass getDomain(boolean includingSuperproperties) {
        Collection domains = getDomains(includingSuperproperties);
        if (domains.isEmpty()) {
            return null;
        }
        return (RDFSClass) domains.iterator().next();
    }


    public Collection getDomains(boolean includingSuperproperties) {
        if (includingSuperproperties) {
            Collection values = getDirectOwnSlotValues(getOWLModel().getRDFSDomainProperty());
            if (values.isEmpty()) {
                Collection result = new ArrayList();
                for (Iterator it = getSuperproperties(false).iterator(); it.hasNext();) {
                    RDFProperty superproperty = (RDFProperty) it.next();
                    Collection superDomains = superproperty.getDomains(true);
                    result.addAll(superDomains);
                }
                return result;
            }
            else {
                return values;
            }
        }
        else {
            return getDirectOwnSlotValues(getOWLModel().getRDFSDomainProperty());
        }
    }


    public Collection getEquivalentProperties() {
        Slot equivalentClassesSlot = getOWLModel().getOWLEquivalentPropertyProperty();
        return getOwnSlotValues(equivalentClassesSlot);
    }


    public RDFProperty getFirstSuperproperty() {
        return (RDFProperty) getPropertyValue(getOWLModel().getRDFSSubPropertyOfProperty());
    }


    public Icon getInheritedIcon() {
        return OWLIcons.getImageIcon(OWLIcons.RDF_PROPERTY_INHERITED);
    }


    public Icon getIcon() {
        if (isEditable()) {
            return getBaseImageIcon();
        }
        else {
            return OWLIcons.getReadOnlyPropertyIcon(OWLIcons.getImageIcon(OWLIcons.RDF_PROPERTY));
        }
    }


    public String getIconName() {
        return OWLIcons.RDF_PROPERTY;
    }


    public RDFProperty getInverseProperty() {
        return (RDFProperty) getKnowledgeBase().getInverseSlot(this);
    }


    public RDFResource getRange() {
        return getRange(false);
    }


    public RDFResource getRange(boolean includingSuperproperties) {
        Object r = getPropertyValue(getOWLModel().getRDFSRangeProperty());
        if (r instanceof RDFResource) {
            return (RDFResource) r;
        }
        else if (r == null && includingSuperproperties) {
            for (Iterator it = getSuperproperties(false).iterator(); it.hasNext();) {
                RDFProperty superproperty = (RDFProperty) it.next();
                RDFResource range = superproperty.getRange(true);
                if (range != null) {
                    return range;
                }
            }
        }
        return null;
    }


    public RDFSDatatype getRangeDatatype() {
        RDFResource range = getRange();
        if (range instanceof RDFSDatatype) {
            return (RDFSDatatype) range;
        }
        else if (range instanceof OWLDataRange) {
            return ((OWLDataRange) range).getRDFDatatype();
        }
        return null;
    }


    public Collection getRanges(boolean includingSuperproperties) {
        Collection ranges = getPropertyValues(getOWLModel().getRDFSRangeProperty());
        if (ranges.isEmpty() && includingSuperproperties) {
            for (Iterator it = getSuperproperties(false).iterator(); it.hasNext();) {
                RDFProperty superproperty = (RDFProperty) it.next();
                ranges = superproperty.getRanges(true);
                if (ranges != null) {
                    return ranges;
                }
            }
        }
        return ranges;
    }


    public Collection getSubproperties(boolean transitive) {
        if (transitive) {
            return getKnowledgeBase().getSubslots(this);
        }
        else {
            return getKnowledgeBase().getDirectSubslots(this);
        }
    }


    public int getSubpropertyCount() {
        return getKnowledgeBase().getDirectSubslotCount(this);
    }


    public Collection getSuperproperties(boolean transitive) {
        if (transitive) {
            return getKnowledgeBase().getSuperslots(this);
        }
        else {
            return getKnowledgeBase().getDirectSuperslots(this);
        }
    }


    public int getSuperpropertyCount() {
        return getKnowledgeBase().getDirectSuperslotCount(this);
    }


    public Collection getUnionDomain() {
        return getKnowledgeBase().getDirectDomain(this);
    }


    public Collection getUnionDomain(boolean includingSuperproperties) {
        if (includingSuperproperties) {
            return getKnowledgeBase().getDomain(this);
        }
        else {
            return getKnowledgeBase().getDirectDomain(this);
        }
    }


    public Collection getUnionRangeClasses() {
        return AbstractOWLModel.getRDFResources(getOWLModel(),
                                                getKnowledgeBase().getAllowedClses(this));
    }


    public boolean hasDatatypeRange() {
        RDFResource range = getRange(true);
        return range instanceof RDFSDatatype || range instanceof OWLDataRange;
    }


    public boolean hasObjectRange() {
        RDFResource range = getRange(true);
        return range instanceof RDFSClass;
    }


    public boolean hasRange(boolean includingSuperproperties) {
        Collection ranges = getRanges(includingSuperproperties);
        return !ranges.isEmpty();
    }


    public boolean isSubpropertyOf(RDFProperty superProperty, boolean transitive) {
        if (transitive) {
            return getKnowledgeBase().hasSuperslot(this, superProperty);
        }
        else {
            return getKnowledgeBase().hasDirectSuperslot(this, superProperty);
        }
    }


    public boolean isAnnotationProperty() {
        return hasProtegeType(getOWLModel().getOWLAnnotationPropertyClass());
    }


    public boolean isDomainDefined() {
        if (getDirectOwnSlotValue(getKnowledgeBase().getSlot(Model.Slot.DIRECT_DOMAIN)) == null) {
            return getDirectSuperslotCount() == 0;
        }
        else {
            return !getKnowledgeBase().getRootCls().getDirectTemplateSlots().contains(this);
        }
    }


    public boolean isDomainDefined(boolean transitive) {
        if (!transitive) {
            return isDomainDefined();
        }
        else {
            if (getDirectOwnSlotValue(getKnowledgeBase().getSlot(Model.Slot.DIRECT_DOMAIN)) == null) {
                if (getDirectSuperslotCount() == 0) {
                    return false;
                }
                else {
                    return isDomainDefined(new HashSet());
                }
            }
            else {
                return !getKnowledgeBase().getRootCls().hasDirectTemplateSlot(this);
            }
        }
    }


    private boolean isDomainDefined(Set reached) {
        reached.add(this);
        if (getDirectOwnSlotValue(getKnowledgeBase().getSlot(Model.Slot.DIRECT_DOMAIN)) == null) {
            for (Iterator it = getDirectSuperslots().iterator(); it.hasNext();) {
                Slot superSlot = (Slot) it.next();
                if (!reached.contains(superSlot) && superSlot instanceof DefaultRDFProperty) {
                    if (((DefaultRDFProperty) superSlot).isDomainDefined(reached)) {
                        return true;
                    }
                }
            }
            return false;
        }
        else {
            return !getKnowledgeBase().getRootCls().hasDirectTemplateSlot(this);
        }
    }


    public boolean isFunctional() {
        if (hasProtegeType(getOWLModel().getOWLFunctionalPropertyClass())) {
            return true;
        }
        for (Iterator it = getSuperproperties(false).iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (property.isFunctional()) {
                return true;
            }
        }
        return false;
    }


    public boolean isRangeDefined() {
        return getDirectOwnSlotValue(getKnowledgeBase().getSlot(Model.Slot.VALUE_TYPE)) != null;
    }


    public boolean isReadOnly() {
        Slot readOnlySlot = ((OWLModel) getKnowledgeBase()).getProtegeReadOnlyProperty();
        if (readOnlySlot != null) {
            Object value = getDirectOwnSlotValue(readOnlySlot);
            return Boolean.TRUE.equals(value);
        }
        return false;
    }


    public void removeEquivalentProperty(OWLProperty property) {
        Slot equivalentClassesSlot = getOWLModel().getOWLEquivalentPropertyProperty();
        removeOwnSlotValue(equivalentClassesSlot, property);
    }


    public void removePropertyListener(PropertyListener listener) {
        if (!(listener instanceof PropertyAdapter)) {
            throw new IllegalArgumentException("Listener must be subclass of PropertyAdapter");
        }
        removeSlotListener(listener);
    }


    public void removeSuperproperty(RDFProperty property) {
        getKnowledgeBase().removeDirectSuperslot(this, property);
    }


    public void removeUnionDomainClass(RDFSClass domainClass) {
        Collection directDomain = new ArrayList(getDirectDomain());
        directDomain.remove(domainClass);
        if (directDomain.isEmpty()) {
            RDFSClass owlThing = domainClass.getOWLModel().getOWLThingClass();
            if ((domainClass != owlThing) &&
                (getSuperpropertyCount() == 0)) {
                setDomain(owlThing);
            }
            else {
                setDomain(null);
            }
        }
        else if (directDomain.size() == 1) {
            setDomain((RDFSClass) directDomain.iterator().next());
        }
        else {
            OWLUnionClass unionClass = getOWLModel().createOWLUnionClass(directDomain);
            setDomain(unionClass);
        }
        // ((Cls) domainClass).removeDirectTemplateSlot(this);
    }


    public void setDomainDefined(boolean value) {
        if (value != isDomainDefined()) {
            if (value) {
                setDomain(null);
            }
            else {
                if (getSuperpropertyCount() > 0) {
                    setDomain(null);
                }
                else {
                    setDomain(getOWLModel().getOWLThingClass());
                }
            }
        }
        else if (!value && getSuperpropertyCount() > 0) {
            setDomain(null);
        }
    }


    public void setEquivalentProperties(Collection slots) {
        Slot equivalentClassesSlot = getOWLModel().getOWLEquivalentPropertyProperty();
        setOwnSlotValues(equivalentClassesSlot, slots);
    }


    public void setFunctional(boolean value) {
        updateRDFType(value, getOWLModel().getOWLFunctionalPropertyClass());
    }


    public void setInverseProperty(RDFProperty inverseProperty) {
        getKnowledgeBase().setInverseSlot(this, inverseProperty);
    }


    public void setRange(RDFResource range) {
        setPropertyValue(getOWLModel().getRDFSRangeProperty(), range);
    }


    public void setRanges(Collection ranges) {
        setPropertyValues(getOWLModel().getRDFSRangeProperty(), ranges);
    }


    public void setRDFTypeOfSubproperties(RDFSNamedClass type) {
        getKnowledgeBase().setDirectTypeOfSubslots(this, type);
    }


    public void setUnionRangeClasses(Collection classes) {
        RDFResource newRange = null;
        if (classes.size() == 1) {
            newRange = (RDFResource) classes.iterator().next();
        }
        else if (classes.size() > 1) {
            newRange = getOWLModel().createOWLUnionClass(classes);
        }
        setRange(newRange);
    }


    public void synchronizeDomainAndRangeOfInverse() {
        if (getValueType() == ValueType.INSTANCE) {
            Slot inverse = getInverseSlot();
            if (inverse != null && inverse.getValueType() == ValueType.INSTANCE) {
                synchronizeRangeOfInverseWithDomainOfThis();
                synchronizeDomainOfInverseWithRangeOfThis();
            }
        }
    }


    private void synchronizeDomainOfInverseWithRangeOfThis() {
        RDFProperty inverseProperty = getInverseProperty();
        Slot domainSlot = getKnowledgeBase().getSlot(Model.Slot.DIRECT_DOMAIN);
        Collection range = getUnionRangeClasses();
        if (range.isEmpty() && inverseProperty.getSuperpropertyCount() == 0) {
            range = Collections.singleton(getOWLModel().getOWLThingClass());
        }
        final Collection inverseDomain = new ArrayList(((Slot) inverseProperty).getDirectOwnSlotValues(domainSlot));
        if (range.isEmpty()) {
            for (Iterator it = inverseDomain.iterator(); it.hasNext();) {
                RDFSClass oldDomainCls = (RDFSClass) it.next();
                inverseProperty.removeUnionDomainClass(oldDomainCls);
            }
        }
        else {
            for (Iterator it = inverseDomain.iterator(); it.hasNext();) {
                RDFSClass inverseRangeCls = (RDFSClass) it.next();
                if (!range.contains(inverseRangeCls)) {
                    inverseProperty.removeUnionDomainClass(inverseRangeCls);
                }
            }
            for (Iterator it = range.iterator(); it.hasNext();) {
                RDFSClass rangeCls = (RDFSClass) it.next();
                if (!((Cls) rangeCls).hasDirectTemplateSlot(inverseProperty)) {
                    inverseProperty.addUnionDomainClass(rangeCls);
                }
            }
        }
    }


    private void synchronizeRangeOfInverseWithDomainOfThis() {
        RDFProperty inverse = getInverseProperty();
        Slot domainSlot = getKnowledgeBase().getSlot(Model.Slot.DIRECT_DOMAIN);
        Collection domain = new ArrayList(getDirectOwnSlotValues(domainSlot));
        domain.remove(getKnowledgeBase().getRootCls());
        final Collection inverseRange = inverse.getUnionRangeClasses();
        if (domain.isEmpty()) {
            inverse.setRange(null);
        }
        else {
            Collection newRange = new HashSet(inverseRange);
            for (Iterator it = inverseRange.iterator(); it.hasNext();) {
                Cls inverseRangeCls = (Cls) it.next();
                if (!domain.contains(inverseRangeCls)) {
                    newRange.remove(inverseRangeCls);
                }
            }
            newRange.addAll(domain);
            inverse.setUnionRangeClasses(newRange);
        }
    }


    protected void updateRDFType(boolean value, RDFSClass metaclass) {
        if (hasProtegeType(metaclass) != value) {
            if (value) {
                addProtegeType(metaclass);
            }
            else {
                removeProtegeType(metaclass);
            }
        }
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


    public Collection getRDFProperties() {
        return OWLUtil.getRDFProperties(this);
    }


    public RDFSClass getRDFType() {
        return OWLUtil.getRDFType(this);
    }


    public Collection getRDFTypes() {
        return OWLUtil.getRDFTypes(this);
    }


    public Set getReferringAnonymousClasses() {
        return OWLUtil.getReferringAnonymousClses(this);
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


    public boolean isAnonymous() {
        return getOWLModel().isAnonymousResourceName(getName());
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


    public void setDomain(RDFSClass domainClass) {
        if (domainClass == null) {
            setDomains(Collections.EMPTY_LIST);
        }
        else {
            setDomains(Collections.singleton(domainClass));
        }
    }


    public void setDomains(Collection domainClasses) {
        setDirectOwnSlotValues(getOWLModel().getRDFSDomainProperty(), domainClasses);
    }


    public void setInferredTypes(Collection types) {
        OWLUtil.setInferredTypes(this, types);
    }


    public void setDocumentation(String value) {
        OWLUtil.setComment(this, value);
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


    public void setProtegeTypes(Collection types) {
        OWLUtil.setProtegeTypes(this, types);
    }


    public void setRDFType(RDFSClass type) {
        OWLUtil.setRDFType(this, type);
    }


    public void setRDFTypes(Collection types) {
        OWLUtil.setRDFTypes(this, types);
    }

    // Deprecatable -----------------------------------------------------------


    public boolean isDeprecated() {
        RDFSClass c = getOWLModel().getRDFSNamedClass(OWLNames.Cls.DEPRECATED_PROPERTY);
        return getProtegeTypes().contains(c);
    }


    public void setDeprecated(boolean value) {
        if (isDeprecated() != value) {
            RDFSClass c = getOWLModel().getRDFSNamedClass(OWLNames.Cls.DEPRECATED_PROPERTY);
            if (value) {
                addProtegeType(c);
            }
            else {
                removeProtegeType(c);
            }
        }
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitRDFProperty(this);
    }

}
