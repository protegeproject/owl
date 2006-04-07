package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLHasValue;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

/**
 * A FrameStore with specific support for OWL ontologies.
 * While most calls simply forward to their default implementation, some calls are intercepted
 * to do some extra work:
 * <UL>
 * <LI>Deleting depending anonymous classes when their host is deleted</LI>
 * <LI>Synchronizing facet overrides and restriction superclasses (RestrictionUpdaters)</LI>
 * <LI>Ensuring that equivalent classes and superclasses are synchronized</LI>
 * </UL>
 * The goal of this architecture is to make sure that there a no unused anonymous classes
 * around.  This cannot be done through a garbage collection because then components such as
 * the Jena synchronizers could not be able to delete a class when it should be deleted.
 * <BR>
 * Normally, all anonymous classes are used only in one place (their host class).  The host
 * can be a restriction (as facet override in the all and some restrictions), a slot (e.g.
 * via domain and range), or a named class (e.g. as superclass or disjoint class).  Much
 * of the logic for this deleting of depending classes is encapsulated in the subtypes of
 * RDFSClass.  Some extra cases, however (especially restrictions) must be handled with care.
 * <BR>
 * With restrictions, the rule that anonymous classes are only attached to a single host is
 * no longer valid.  Restrictions store their filler as a facet override.  In the case of
 * all and some restrictions, these overrides can be (anonymous) classes.  The same classes
 * are mapped into facet overrides in the host class -- they are not cloned because there
 * is only one representation in the corresponding Jena/OWL model.
 * <BR>
 * These anonymous classes have the restriction as host, and they are deleted when the
 * restriction is deleted or changes its filler.  As a consequence, the following actions
 * are performed by this FrameStore:
 * <UL>
 * <LI>If the filler of a restriction changes, then the filler is also assigned to the
 * (named) subclass of the restriction as facet override</LI>
 * <LI>If the facet override changes in the named subclass, then the old restriction is
 * replaced with a new one that has the override value as filler (i.e. also as
 * override)</LI>
 * <LI>If the named class is deleted, then the depending restrictions are deleted</LI>
 * <LI>If the restriction is deleted, then the facet override is removed from the
 * named class</LI>
 * </UL>
 * <BR>
 * Finally, the OWLFrameStore also makes sure that if a named class has an intersection as
 * equivalent class, then all named operands of these intersections are also superclasses
 * of the host class.  Note that this is maintained in both direction, i.e. if someone
 * removes an intersection, then the named operands are also automatically removed, no
 * matter whether the user intended to also add the class as a superclass.
 * These side effects need to be considered by OWL API users.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFrameStore extends FrameStoreAdapter {

    /**
     * A Hashtable from Java restriction Class objects to the responsible RestrictionUpdaters
     */
    private Hashtable class2Updater = new Hashtable();

    /**
     * A Hashtable from Facets to the responsible RestrictionUpdaters
     */
    private Hashtable facet2Updater = new Hashtable();

    /**
     * A flag to prevent infinite recursion when a superclass has been added or removed
     */
    protected boolean facetHandlingBlocked;

    private AllValuesFromRestrictionUpdater allValuesFromRestrictionUpdater;

    private CardinalityRestrictionUpdater cardinalityRestrictionUpdater;

    private HasValueRestrictionUpdater hasValueRestrictionUpdater;

    private AbstractOWLModel owlModel;

    /**
     * A flag to prevent infinite recursion when a facet override has been changed.
     */
    protected boolean superclassHandlingBlocked;

    private boolean suppressUpdateTemplateSlots = false;

    public final static String IGNORE_PREFIXES_IN_SEARCH = "OWL-TOLERATE-PREFIXES-IN-SEARCH";

    private boolean superclassSynchronizationBlocked = false;

    private boolean deletingRDFSDatatype = false;


    public OWLFrameStore(AbstractOWLModel owlModel) {
        this.owlModel = owlModel;
        initRestrictionUpdaters();
    }


    public void addDirectSuperclass(Cls cls, Cls superCls) {
        if (!cls.hasDirectSuperclass(superCls)) {   // Disallow duplicates

            // log("-> " +cls.getBrowserText() + " ADDED " + superCls.getBrowserText());
            super.addDirectSuperclass(cls, superCls);
            if (superCls instanceof OWLIntersectionClass &&
                cls instanceof OWLNamedClass &&
                superCls.hasDirectSuperclass(cls)) {
                addNamedOperandsToDirectSuperclasses((OWLNamedClass) cls, (OWLIntersectionClass) superCls);
            }
            else if (cls instanceof OWLIntersectionClass &&
                     superCls instanceof OWLNamedClass &&
                     superCls.hasDirectSuperclass(cls)) {
                addNamedOperandsToDirectSuperclasses((OWLNamedClass) superCls, (OWLIntersectionClass) cls);
            }

            if (!superclassHandlingBlocked && cls instanceof OWLNamedClass) {
                OWLNamedClass namedCls = (OWLNamedClass) cls;
                if (superCls instanceof OWLRestriction) {
                    copyFacetValuesIntoOWLNamedClass(namedCls, (OWLRestriction) superCls);
                }
            }

            if (cls instanceof OWLNamedClass &&
                superCls instanceof OWLNamedClass &&
                cls.isEditable() &&
                ((OWLNamedClass) superCls).getSubclassesDisjoint()) {
                OWLUtil.ensureSubclassesDisjoint((OWLNamedClass) superCls);
            }

            if (!superclassSynchronizationBlocked) {
                if (cls instanceof RDFSNamedClass) {
                    updateRDFSSubClassOf((RDFSNamedClass) cls);
                }
                if (superCls instanceof RDFSNamedClass) {
                    updateRDFSSubClassOf((RDFSNamedClass) superCls);
                }
            }
        }
    }


    public void addDirectSuperslot(Slot slot, Slot superSlot) {
        super.addDirectSuperslot(slot, superSlot);
        if (slot instanceof RDFProperty) {
            RDFProperty property = (RDFProperty) slot;
            if (property instanceof OWLProperty && superSlot instanceof OWLProperty && ((OWLProperty) superSlot).isAnnotationProperty()) {
                addDirectType(slot, owlModel.getCls(OWLNames.Cls.ANNOTATION_PROPERTY));
            }
            if (property.getRange() == null) {
                slot.setDirectOwnSlotValue(owlModel.getSlot(Model.Slot.VALUE_TYPE), null);
            }
            if (!property.isDomainDefined()) {
                slot.setDirectOwnSlotValue(owlModel.getSlot(Model.Slot.DIRECT_DOMAIN), null);
            }
        }
    }


    public void addDirectType(Instance instance, Cls type) {
        if (instance instanceof RDFProperty) {
            if (type.equals(owlModel.getOWLFunctionalPropertyClass())) {
                ((Slot) instance).setAllowsMultipleValues(false);
            }
        }
        if (instance instanceof RDFResource &&
            !instance.getDirectOwnSlotValues(owlModel.getRDFTypeProperty()).contains(type) &&
            !type.equals(owlModel.getRDFUntypedResourcesClass())) {
            instance.addOwnSlotValue(owlModel.getRDFTypeProperty(), type);
        }
        super.addDirectType(instance, type);
    }


    private void addNamedOperandsToDirectSuperclasses(OWLNamedClass cls, OWLIntersectionClass superCls) {
        for (Iterator it = superCls.getOperands().iterator(); it.hasNext();) {
            RDFSClass operand = (RDFSClass) it.next();
            if (operand instanceof OWLNamedClass) {
                cls.addSuperclass(operand);
            }
        }
    }


    private List convertInternalFormatToRDFSLiterals(Collection values) {
        List result = new LinkedList();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof String) {
                final String str = (String) o;
                if (DefaultRDFSLiteral.isRawValue(str)) {
                    result.add(new DefaultRDFSLiteral(owlModel, str));
                }
                else {
                    result.add(o);
                }
            }
            else {
                result.add(o);
            }
        }
        return result;
    }


    private List convertRDFSLiteralsToInternalFormat(Collection values) {
        final List result = new LinkedList();
        for (Iterator it = values.iterator(); it.hasNext();) {
            final Object o = it.next();
            if (o instanceof RDFSLiteral) {
                final DefaultRDFSLiteral literal = (DefaultRDFSLiteral) o;
                final Object optimized = literal.getPlainValue();
                if (optimized != null) {
                    result.add(optimized);
                }
                else {
                    result.add(literal.getRawValue());
                }
            }
            else {
                result.add(o);
            }
        }
        return result;
    }


    private void copyFacetValuesIntoOWLNamedClass(RDFSNamedClass cls, OWLRestriction restriction) {
        Class clazz = restriction.getClass();
        RestrictionUpdater ru = (RestrictionUpdater) class2Updater.get(clazz);
        if (ru != null) {
            facetHandlingBlocked = true;
            ru.copyFacetValuesIntoNamedClass(cls, restriction);
            facetHandlingBlocked = false;
        }
    }


    private void copyFacetValuesIntoOWLNamedClass(OWLRestriction restriction) {
        if (restriction.getSubclasses(false).size() == 1) {
            RDFSNamedClass namedCls = (RDFSNamedClass) restriction.getSubclasses(false).toArray()[0];
            copyFacetValuesIntoOWLNamedClass(namedCls, restriction);
        }
    }


    public void copyFacetValuesIntoNamedClses() {
        boolean oldUndo = owlModel.isUndoEnabled();
        owlModel.setUndoEnabled(false);
        for (Iterator it = ((AbstractOWLModel) owlModel).getRDFSClasses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls instanceof OWLRestriction) {  // Convert restrictions into facet overrides
                copyFacetValuesIntoOWLNamedClass((OWLRestriction) cls);
            }
        }
        owlModel.setUndoEnabled(oldUndo);
    }


    public Cls createCls(FrameID id, String name, Collection directTypes, Collection directSuperclasses, boolean loadDefaults) {
        Cls cls = super.createCls(id, name, directTypes, directSuperclasses, loadDefaults);
        if (cls instanceof OWLNamedClass && cls.isEditable()) {
            for (Iterator it = directSuperclasses.iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof OWLNamedClass &&
                    ((OWLNamedClass) superCls).getSubclassesDisjoint()) {
                    OWLUtil.ensureSubclassesDisjoint((OWLNamedClass) superCls);
                }
            }
        }
        if (cls instanceof RDFSNamedClass) {
            ((RDFSNamedClass) cls).setPropertyValues(owlModel.getRDFSSubClassOfProperty(), directSuperclasses);
            cls.setDirectOwnSlotValues(owlModel.getRDFTypeProperty(), directTypes);
        }
        else if (cls instanceof OWLRestriction) {
            cls.setDirectOwnSlotValue(owlModel.getRDFTypeProperty(), owlModel.getRDFSNamedClass(OWLNames.Cls.RESTRICTION));
        }
        else if (cls instanceof OWLAnonymousClass) {
            cls.setDirectOwnSlotValue(owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        }
        return cls;
    }


    public Slot createSlot(FrameID id, String name, Collection directTypes, Collection directSuperslots, boolean loadDefaults) {
        Slot slot = super.createSlot(id, name, directTypes, directSuperslots, loadDefaults);
        if (slot instanceof RDFProperty) {
            RDFProperty rdfProperty = (RDFProperty) slot;
            slot.setAllowsMultipleValues(true);
            if (slot instanceof OWLObjectProperty && directSuperslots.isEmpty()) {
                ((Slot) rdfProperty).setValueType(ValueType.INSTANCE);
            }
            slot.setDirectOwnSlotValues(owlModel.getRDFTypeProperty(), directTypes);
            rdfProperty.setDomainDefined(false); // true
        }
        return slot;
    }


    public SimpleInstance createSimpleInstance(FrameID id, String name, Collection directTypes, boolean loadDefaults) {
        SimpleInstance instance = super.createSimpleInstance(id, name, directTypes, loadDefaults);
        if (instance instanceof RDFResource && !directTypes.contains(owlModel.getRDFUntypedResourcesClass())) {
            instance.setDirectOwnSlotValues(owlModel.getRDFTypeProperty(), directTypes);
        }
        return instance;
    }


    private void deleteAnonymousClass(OWLAnonymousClass cls) {
        Collection refs = cls.getReferringAnonymousClasses();
        deleteDependingListInstances(cls);
        if (refs.size() > 0) {
            deleteAnonymousClses(refs);  // Will also delete cls
        }
        else {
            deleteAnonymousClses(Collections.singleton(cls));
        }
    }


    private void deleteAnonymousClses(Collection clses) {
        deleteAnonymousClasses(clses, Collections.EMPTY_LIST);
    }


    private void deleteAnonymousClasses(Collection clses, Collection survivors) {
        if (!clses.isEmpty()) {
            Collection roots = new HashSet();
            for (Iterator it = clses.iterator(); it.hasNext();) {
                Object next = it.next();
                if (next instanceof OWLAnonymousClass) {
                    OWLAnonymousClass root = ((OWLAnonymousClass) next).getExpressionRoot();
                    if (!survivors.contains(root)) {
                        roots.add(root);
                    }
                }
                else if (next instanceof OWLDataRange) {
                    ((OWLDataRange) next).delete();
                }
            }

            for (Iterator it = roots.iterator(); it.hasNext();) {
                OWLAnonymousClass anonymousCls = (OWLAnonymousClass) it.next();
                deleteAnonymousTree(anonymousCls);
            }
        }
    }


    private void deleteAnonymousTree(OWLAnonymousClass anonymousClass) {

        if (anonymousClass instanceof OWLIntersectionClass) {
            OWLIntersectionClass intersectionClass = (OWLIntersectionClass) anonymousClass;
            ensureRDFSSubClassOfStatementsExistForNamedOperands(intersectionClass);
        }

        Collection dependants = anonymousClass.getDependingClasses();
        for (Iterator dit = dependants.iterator(); dit.hasNext();) {
            OWLAnonymousClass dependentCls = (OWLAnonymousClass) dit.next();
            deleteAnonymousTree(dependentCls);
        }
        deleteRDFListsThatArePropertyValues(anonymousClass);
        if (anonymousClass instanceof OWLQuantifierRestriction) {
            RDFResource filler = ((OWLQuantifierRestriction) anonymousClass).getFiller();
            if (filler instanceof RDFSDatatype && filler.isAnonymous()) {
                deletingRDFSDatatype = true;
                filler.delete();
                deletingRDFSDatatype = false;
            }
        }
        super.deleteCls(anonymousClass);
    }


    public void deleteCls(Cls cls) {
        if (cls instanceof OWLAnonymousClass) {
            deleteAnonymousClass((OWLAnonymousClass) cls);
        }
        else if (cls instanceof RDFSNamedClass) {
            deleteNamedClass((RDFSNamedClass) cls);
        }
        else {
            deleteDependingListInstances(cls);
            super.deleteCls(cls);  // Default handling for anything else
        }
    }


    private void deleteDependingAnonymousClses(Cls cls) {
        Collection ds = getClsesToDelete(cls);
        for (Iterator it = ds.iterator(); it.hasNext();) {
            Cls dc = (Cls) it.next();
            if (dc instanceof RDFSNamedClass) {
                Collection refs = ((RDFSNamedClass) dc).getReferringAnonymousClasses();
                deleteAnonymousClses(refs);
                for (Iterator j = new ArrayList(dc.getDirectSuperclasses()).iterator(); j.hasNext();) {
                    Cls superCls = (Cls) j.next();
                    if (superCls instanceof OWLAnonymousClass) {
                        superCls.delete();
                    }
                }
            }
        }
    }


    private void deleteDependingAnonymousClses(Slot slot) {
        Collection ds = getSlotsToDelete(slot);
        for (Iterator it = ds.iterator(); it.hasNext();) {
            Slot dc = (Slot) it.next();
            if (dc instanceof RDFProperty) {
                Collection refs = ((RDFProperty) dc).getReferringAnonymousClasses();
                deleteAnonymousClses(refs);
            }
        }
    }


    private void deleteDependingListInstances(Instance instance) {

        final RDFList nil = owlModel.getRDFNil();

        // Delete RDFList nodes that have the value as rdf:first
        Collection refs = getReferences(instance);
        Slot firstSlot = owlModel.getRDFFirstProperty();
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (firstSlot.equals(reference.getSlot())) {
                RDFList li = (RDFList) reference.getFrame();
                if (!nil.equals(li)) {
                    deleteListInstance(li);
                }
            }
        }

        // Delete any RDFLists that are direct own slot values of the instance
        if (instance instanceof RDFResource) {
            deleteRDFListsThatArePropertyValues((RDFResource) instance);
        }
    }


    private void deleteRDFListsThatArePropertyValues(RDFResource resource) {
        RDFResource nil = owlModel.getRDFNil();
        for (Iterator it = ((RDFResource) resource).getPossibleRDFProperties().iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            Collection values = new ArrayList(resource.getPropertyValues(property));
            for (Iterator vit = values.iterator(); vit.hasNext();) {
                Object o = vit.next();
                if (o instanceof RDFList && !nil.equals(o)) {
                    RDFList l = (RDFList) o;
                    resource.removePropertyValue(property, l);
                    //System.out.println("Deleting " + l.getBrowserText() + " at " + instance.getBrowserText() + " . " + property.getBrowserText());
                    l.delete();
                }
            }
        }
    }


    /**
     * Deletes a RDFList cleanly from its list.
     * This will update all references to it (e.g. if it is a direct own slot
     * value somewhere, then it will replace those with the rest of this.
     *
     * @param li the RDFList to delete
     */
    private void deleteListInstance(RDFList li) {
        RDFList rest = li.getRest();
        if (owlModel.getRDFNil().equals(rest)) {
            rest = null;
        }
        li.setRest(null);
        if (rest != null) {
            RDFProperty restProperty = owlModel.getRDFRestProperty();
            for (Iterator it = getReferences(li).iterator(); it.hasNext();) {
                Reference reference = (Reference) it.next();
                Frame frame = reference.getFrame();
                Slot slot = reference.getSlot();
                if (!slot.isSystem() ||
                    slot.equals(restProperty) ||
                    slot.equals(owlModel.getOWLIntersectionOfProperty()) ||
                    slot.equals(owlModel.getOWLUnionOfProperty()) ||
                    slot.equals(owlModel.getOWLOneOfProperty()) ||
                    slot.equals(owlModel.getOWLDistinctMembersProperty())) {
                    frame.addOwnSlotValue(slot, rest);
                }
            }
        }
        li.delete();
    }


    private void deleteNamedClass(RDFSNamedClass cls) {
        // Delete depending anonymous classes before they are handled further down
        deleteNamedClassFromDomainsAndRanges(cls);
        deleteDependingAnonymousClses(cls);
        deleteAnonymousClses(cls.getSuperclasses(false));
        deleteAnonymousClses(cls.getReferringAnonymousClasses());
        deleteDependingListInstances(cls);
        super.deleteCls(cls);
    }


    private void deleteNamedClassFromDomainsAndRanges(RDFSNamedClass cls) {
        Collection anons = cls.getReferringAnonymousClasses();
        for (Iterator it = anons.iterator(); it.hasNext();) {
            OWLAnonymousClass anon = (OWLAnonymousClass) it.next();
            if (anon instanceof OWLUnionClass) {
                Iterator refs = owlModel.getReferences(anon).iterator();
                while (refs.hasNext()) {
                    Reference reference = (Reference) refs.next();
                    if (reference.getFrame() instanceof RDFProperty) {
                        RDFProperty property = (RDFProperty) reference.getFrame();
                        if (reference.getSlot().equals(owlModel.getRDFSDomainProperty())) {
                            property.removeUnionDomainClass(cls);
                        }
                        else if (reference.getSlot().equals(owlModel.getRDFSRangeProperty())) {
                            Collection remainingClasses = new ArrayList(property.getUnionRangeClasses());
                            remainingClasses.remove(cls);
                            property.setUnionRangeClasses(remainingClasses);
                        }
                    }
                }
            }
        }

        // remove from the domain if not in a union
        Collection refs = owlModel.getReferences(cls);
        for (Iterator i=refs.iterator(); i.hasNext();){
            Reference ref = (Reference) i.next();
            if (ref.getSlot().equals(owlModel.getRDFSDomainProperty())) {
                RDFProperty prop = (RDFProperty) ref.getFrame();
                prop.removeUnionDomainClass(cls);
            }
        }
    }


    /**
     * Deletes all QuantifierRestrictions (and the expressions where they are used)
     * which restrict a given propertyerty.  This is needed when a property changes its type.
     *
     * @param property the restricted property
     */
    public void deleteQuantifierRestrictions(OWLProperty property) {
        Collection roots = new HashSet();
        for (Iterator it = owlModel.getReferences(property).iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (reference.getFrame() instanceof OWLQuantifierRestriction) {
                OWLQuantifierRestriction r = (OWLQuantifierRestriction) reference.getFrame();
                roots.add(r.getExpressionRoot());
            }
        }
        for (Iterator it = roots.iterator(); it.hasNext();) {
            OWLAnonymousClass cls = (OWLAnonymousClass) it.next();
            log("- Deleting quantifier restriction root " + cls.getBrowserText());
            cls.delete();
        }
    }


    public void deleteSimpleInstance(SimpleInstance simpleInstance) {
        if (simpleInstance instanceof RDFList) {
            deleteListChain((RDFList) simpleInstance);
        }
        deleteDependingListInstances(simpleInstance);
        if (simpleInstance instanceof RDFResource && !deletingRDFSDatatype) {
            RDFResource resource = (RDFResource) simpleInstance;
            deleteAnonymousClses(resource.getReferringAnonymousClasses());
        }
        super.deleteSimpleInstance(simpleInstance);
    }


    private void deleteListChain(RDFList list) {
        Object first = list.getFirst();
        if (first instanceof OWLAnonymousClass) {
            list.setFirst(null);
            ((OWLAnonymousClass) first).delete();
        }
        RDFList rest = list.getRest();
        if (rest != null && !owlModel.getRDFNil().equals(rest)) {
            rest.delete();
        }
    }


    /**
     * Deletes the depending classes (anonymous domains and ranges) of the slot
     * being deleted.
     *
     * @param slot the Slot being deleted
     */
    public void deleteSlot(Slot slot) {
        deleteDependingListInstances(slot);
        if (slot instanceof RDFProperty) {
            RDFProperty rdfProperty = (RDFProperty) slot;
            deleteDependingAnonymousClses(rdfProperty);
            if (rdfProperty.hasObjectRange()) {
                deleteAnonymousClses(rdfProperty.getPropertyValues(owlModel.getRDFSRangeProperty()));
            }
            Collection domain = slot.getDirectDomain();
            deleteAnonymousClses(domain);
            RDFResource range = rdfProperty.getRange();
            if (range instanceof RDFSDatatype && range.isAnonymous()) {
                range.delete();
            }
        }
        super.deleteSlot(slot);
    }


    private void ensureRDFSSubClassOfStatementsExistForNamedOperands(OWLIntersectionClass intersectionClass) {
        Collection superclasses = intersectionClass.getSuperclasses(false);
        if (superclasses.size() == 1) {
            RDFSNamedClass namedClass = (RDFSNamedClass) superclasses.iterator().next();
            RDFProperty scp = owlModel.getRDFSSubClassOfProperty();
            Iterator operands = intersectionClass.listOperands();
            while (operands.hasNext()) {
                RDFSClass operand = (RDFSClass) operands.next();
                if (operand instanceof RDFSNamedClass) {
                    if (!namedClass.getPropertyValues(scp).contains(operand)) {
                        namedClass.addPropertyValue(scp, operand);
                    }
                }
            }
        }
    }


    /**
     * A slighly adjusted version of DeleteSimplificationFrameStore
     * that ignores links through the anonymous root class.
     *
     * @param cls
     * @return see the super method
     */
    public Collection getClsesToDelete(Cls cls) {
        Collection subclasses = getSubclasses(cls);
        Collection clsesToBeDeleted = new HashSet(subclasses);
        clsesToBeDeleted.add(cls);

        Iterator it = subclasses.iterator();
        while (it.hasNext()) {
            Cls subclass = (Cls) it.next();
            if (!subclass.equals(cls) && isReachableByAnotherRoute(subclass, clsesToBeDeleted)) {
                clsesToBeDeleted.remove(subclass);
                Collection subsubclasses = new HashSet(getSubclasses(subclass));
                subsubclasses.remove(cls);
                clsesToBeDeleted.removeAll(subsubclasses);
            }
        }
        return clsesToBeDeleted;
    }


    private DeleteSimplificationFrameStore getDeleteSimplificationFrameStore() {
        for (Iterator it = owlModel.getFrameStores().iterator(); it.hasNext();) {
            FrameStore frameStore = (FrameStore) it.next();
            if (frameStore instanceof DeleteSimplificationFrameStore) {
                return (DeleteSimplificationFrameStore) frameStore;
            }
        }
        return null;
    }


    public List getDirectOwnSlotValuesConverting(Frame frame, Slot slot) {
        final List values = super.getDirectOwnSlotValues(frame, slot);
        if (!values.isEmpty() && frame instanceof RDFResource && slot instanceof RDFProperty) {
            for (Iterator it = values.iterator(); it.hasNext();) {
                final Object o = it.next();
                if (o instanceof String && DefaultRDFSLiteral.isRawValue((String) o)) {
                    return convertInternalFormatToRDFSLiterals(values);
                }
            }
        }
        return values;
    }


    public List getPropertyValueLiterals(RDFResource frame, RDFProperty slot) {
        final List values = new ArrayList(OWLUtil.getPropertyValues(frame, slot, false));
        if (!values.isEmpty()) {
            return getLiteralValues(values);
        }
        else {
            return values;
        }
    }


    public List getLiteralValues(final List values) {
        List result = new ArrayList();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSLiteral) {
                result.add(o);
            }
            else {
                result.add(owlModel.createRDFSLiteral(o));
            }
        }
        return result;
    }

    /*public List getDirectOwnSlotValues(Frame frame, Slot slot) {
       List result = super.getDirectOwnSlotValues(frame, slot);
       return flattenList(result);
   }


   public Collection getOwnSlotValues(Frame frame, Slot slot) {
       Collection results = super.getOwnSlotValues(frame, slot);
       return flattenList(results);
   }


   private List flattenList(Collection results) {
       if(results.size() == 1) {
           Object value = results.iterator().next();
           if(value instanceof RDFList) {
               return ((RDFList)value).getValues();
           }
       }
       if(results instanceof List) {
           return (List) results;
       }
       else {
           return new ArrayList(results);
       }
   } */


    public Collection getOwnSlotValuesConverting(Frame frame, Slot slot) {
        Collection values = super.getOwnSlotValues(frame, slot);
        return getConvertedValues(values);
    }


    public Collection getConvertedValues(Collection values) {
        if (!values.isEmpty()) {
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object o = it.next();
                if (o instanceof String && DefaultRDFSLiteral.isRawValue((String) o)) {
                    return convertInternalFormatToRDFSLiterals(values);
                }
            }
        }
        return values;
    }


    public Set getClsesWithMatchingBrowserText(String value, Collection superclasses, int maxMatches) {
        Set results = new HashSet();
        results.addAll(super.getClsesWithMatchingBrowserText(value, superclasses, maxMatches));
        if (isIgnorePrefixesInSearch(owlModel)) {
            Iterator it = owlModel.getNamespaceManager().getPrefixes().iterator();
            while (it.hasNext()) {
                String prefix = (String) it.next();
                results.addAll(super.getClsesWithMatchingBrowserText(prefix + ":" + value, superclasses, maxMatches));
            }
        }
        return results;
    }

    public Set getFramesWithMatchingDirectOwnSlotValue(Slot slot, String value, int maxMatches) {
        Set results = super.getFramesWithMatchingDirectOwnSlotValue(slot, value, maxMatches);
        if (isIgnorePrefixesInSearch(owlModel)) {
            Iterator it = owlModel.getNamespaceManager().getPrefixes().iterator();
            while (it.hasNext()) {
                String prefix = (String) it.next();
                results.addAll(super.getFramesWithMatchingDirectOwnSlotValue(slot, prefix + ":" + value, maxMatches));
            }
        }
        return results;
    }


    public Set getMatchingReferences(String value, int maxMatches) {
        Set results = new HashSet();
        results.addAll(super.getMatchingReferences(value, maxMatches));
        if (isIgnorePrefixesInSearch(owlModel)) {
            Iterator it = owlModel.getNamespaceManager().getPrefixes().iterator();
            while (it.hasNext()) {
                String prefix = (String) it.next();
                results.addAll(super.getMatchingReferences(prefix + ":" + value, maxMatches));
            }
        }
        return results;
    }


    private Collection getSlotsToDelete(Slot slot) {
        DeleteSimplificationFrameStore fs = getDeleteSimplificationFrameStore();
        return fs.getSlotsToDelete(slot);
    }


    private void initRestrictionUpdaters() {

        allValuesFromRestrictionUpdater = new AllValuesFromRestrictionUpdater(owlModel);
        cardinalityRestrictionUpdater = new CardinalityRestrictionUpdater(owlModel);
        hasValueRestrictionUpdater = new HasValueRestrictionUpdater(owlModel);

        facet2Updater.put(owlModel.getFacet(Model.Facet.VALUE_TYPE), allValuesFromRestrictionUpdater);
        facet2Updater.put(owlModel.getFacet(Model.Facet.MAXIMUM_CARDINALITY), cardinalityRestrictionUpdater);
        facet2Updater.put(owlModel.getFacet(Model.Facet.MINIMUM_CARDINALITY), cardinalityRestrictionUpdater);
        facet2Updater.put(owlModel.getFacet(Model.Facet.VALUES), hasValueRestrictionUpdater);

        // TODO: This should be generalized, independent from Default implementation classes
        class2Updater.put(DefaultOWLAllValuesFrom.class, allValuesFromRestrictionUpdater);
        class2Updater.put(DefaultOWLCardinality.class, cardinalityRestrictionUpdater);
        class2Updater.put(DefaultOWLHasValue.class, hasValueRestrictionUpdater);
        class2Updater.put(DefaultOWLMaxCardinality.class, cardinalityRestrictionUpdater);
        class2Updater.put(DefaultOWLMinCardinality.class, cardinalityRestrictionUpdater);
    }


    public static boolean isIgnorePrefixesInSearch(OWLModel owlModel) {
        return Boolean.TRUE.equals(owlModel.getOWLProject().getSettingsMap().getBoolean(IGNORE_PREFIXES_IN_SEARCH));
    }


    private boolean isReachableByAnotherRoute(Cls subclass, Collection classesToBeDeleted) {
        Collection superclasses = new ArrayList(getDirectSuperclasses(subclass));
        superclasses.remove(owlModel.getAnonymousRootCls());
        if (superclasses.size() > 1) {
            Iterator it = superclasses.iterator();
            while (it.hasNext()) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof RDFSNamedClass && !classesToBeDeleted.contains(superCls)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static void log(String str) {
        // System.out.println("[OWLFrameStore] " + str);
    }


    /**
     * An ugly trick to prevent anonymous classes from being deleted as a side effect
     */
    public static boolean autoDeleteOfAnonymousClses = true;


    public void removeDirectSuperclass(Cls cls, Cls superCls) {

        boolean wasEquivalentCls = superCls.hasDirectSuperclass(cls);

        // log("-> " +cls.getBrowserText() + " REMOVED " + superCls.getBrowserText());
        super.removeDirectSuperclass(cls, superCls);

        if (cls instanceof OWLNamedClass && superCls instanceof OWLIntersectionClass && wasEquivalentCls) {
            removeNamedOperandsFromDirectSuperclasses((OWLNamedClass) cls,
                                                      (OWLIntersectionClass) superCls, owlModel.getSlot(Model.Slot.DIRECT_SUPERCLASSES));
        }
        else if (superCls instanceof OWLNamedClass && cls instanceof OWLIntersectionClass && wasEquivalentCls) {
            removeNamedOperandsFromDirectSuperclasses((OWLNamedClass) superCls,
                                                      (OWLIntersectionClass) cls, owlModel.getSlot(Model.Slot.DIRECT_SUBCLASSES));
        }

        if (!superclassHandlingBlocked) {
            if (cls instanceof OWLNamedClass && superCls instanceof OWLRestriction) {
                copyFacetValuesIntoOWLNamedClass((OWLNamedClass) cls, (OWLRestriction) superCls);
            }
        }

        if (superCls instanceof OWLAnonymousClass && autoDeleteOfAnonymousClses) {
            superCls.delete();
        }

        if (cls instanceof RDFSNamedClass) {
            updateRDFSSubClassOf((RDFSNamedClass) cls);
        }
        else if (superCls instanceof RDFSNamedClass) {
            updateRDFSSubClassOf((RDFSNamedClass) superCls);
        }
    }


    public void removeDirectSuperslot(Slot slot, Slot superslot) {
        super.removeDirectSuperslot(slot, superslot);
        if (slot instanceof OWLObjectProperty &&
            slot.getDirectSuperslotCount() == 0 &&
            slot.getAllowedClses().isEmpty()) {
            slot.setValueType(ValueType.INSTANCE);
        }
        if (slot instanceof RDFProperty) {
            if (slot.getDirectDomain().isEmpty() && slot.getDirectSuperslotCount() == 0) {
                ((Cls) owlModel.getOWLThingClass()).addDirectTemplateSlot(slot);
            }
        }
    }


    public void removeDirectType(Instance instance, Cls directType) {
        if (instance instanceof RDFProperty) {
            if (directType.equals(owlModel.getOWLFunctionalPropertyClass())) {
                ((Slot) instance).setAllowsMultipleValues(true);
            }
        }
        if (instance instanceof RDFResource) {
            instance.removeOwnSlotValue(owlModel.getRDFTypeProperty(), directType);
        }
        super.removeDirectType(instance, directType);
    }


    private void removeNamedOperandsFromDirectSuperclasses(OWLNamedClass cls,
                                                           OWLIntersectionClass intersectionCls,
                                                           Slot slot) {
        Collection toRemove = intersectionCls.getNamedOperands();
        if (!toRemove.isEmpty()) {
            for (Iterator it = ((Cls) cls).getDirectOwnSlotValues(slot).iterator(); it.hasNext();) {
                RDFSClass superClass = (RDFSClass) it.next();
                if (superClass instanceof OWLIntersectionClass) {
                    toRemove.removeAll(((OWLIntersectionClass) superClass).getNamedOperands());
                }
            }
            for (Iterator it = toRemove.iterator(); it.hasNext();) {
                RDFSNamedClass namedCls = (RDFSNamedClass) it.next();
                if (!namedCls.hasEquivalentClass(cls)) {
                    cls.removeSuperclass(namedCls);
                }
            }
        }
    }


    public static boolean allowDuplicateOwnSlotValues = false;


    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {

        final int valueCount = values.size();
        if (valueCount > 0) {
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object o = it.next();
                if (o instanceof RDFSLiteral) {
                    values = convertRDFSLiteralsToInternalFormat(values);
                    break;
                }
            }
        }
        if (!allowDuplicateOwnSlotValues &&
            valueCount > 1 &&
            valueCount != new HashSet(values).size()) {
            System.err.println("[OWLFrameStore] Warning: Attempted to assign duplicate value to " +
                               frame.getBrowserText() + "." + slot.getBrowserText());
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object o = it.next();
                System.err.println("[OWLFrameStore]  - " + o);
            }
            values = new HashSet(values);
        }
        if (frame instanceof RDFProperty && slot.equals(owlModel.getRDFSRangeProperty())) {
            List oldValues = frame.getDirectOwnSlotValues(slot);
            if (values.size() > 0 && values.iterator().next() instanceof RDFSClass) {
                updatePropertyAllowedClasses((RDFProperty) frame, values);
            }
            else {
                updatePropertyValueType((RDFProperty) frame, values);
            }
            super.setDirectOwnSlotValues(frame, slot, values);
            deleteAnonymousClasses(oldValues, values);
        }
        else if (frame instanceof RDFProperty && slot.equals(owlModel.getRDFSDomainProperty())) {
            if (values.size() > 1 && values.contains(owlModel.getOWLThingClass())) {
                values = new ArrayList(values);
                values.remove(owlModel.getOWLThingClass());
            }
            List oldValues = frame.getDirectOwnSlotValues(slot);
            super.setDirectOwnSlotValues(frame, slot, values);
            if (!suppressUpdateTemplateSlots) {
                updateSlotDomain((Slot) frame, values);
            }
            deleteAnonymousClasses(oldValues, values);
        }
        else {
            super.setDirectOwnSlotValues(frame, slot, values);
        }
    }


    public void setSuperclassSynchronizationBlocked(boolean value) {
        superclassSynchronizationBlocked = value;
    }

    // ----- Debugging only block

    public boolean suppressUpdateRDFSDomain = false;


    public void addDirectTemplateSlot(Cls cls, Slot slot) {
        super.addDirectTemplateSlot(cls, slot);
        if (!suppressUpdateRDFSDomain && slot instanceof RDFProperty && cls instanceof RDFSClass) {
            //printDeprecationWarning("addDirectTemplateSlot");
            updateRDFSDomain((RDFProperty) slot);
        }
    }


    private void printDeprecationWarning(String methodName) {
        System.out.println("Warning: The method " + methodName + " is not recommended in OWL.");
        System.out.println("         Please use the rdfs:domain methods in RDFProperty.");
        System.out.println("         The following stack trace helps you replace your code.");
        System.out.println("         This message may be deleted in future versions.");
        try {
            throw new Exception();
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
    }


    public void removeDirectTemplateSlot(Cls cls, Slot slot) {
        super.removeDirectTemplateSlot(cls, slot);
        if (!suppressUpdateRDFSDomain && slot instanceof RDFProperty && cls instanceof RDFSClass) {
            //printDeprecationWarning("removeDirectTemplateSlot");
            updateRDFSDomain((RDFProperty) slot);
        }
    }


    private void updateRDFSDomain(RDFProperty property) {
        Collection domainClses = ((Slot) property).getDirectDomain();
        RDFSClass newDomain = null;
        if (domainClses.size() == 1) {
            newDomain = (RDFSClass) domainClses.iterator().next();
        }
        else {
            newDomain = owlModel.createOWLUnionClass(domainClses);
        }
        suppressUpdateTemplateSlots = true;
        property.setDomain(newDomain);
        suppressUpdateTemplateSlots = false;
    }

    // ----- End


    public void setDirectTemplateFacetValues(Cls cls, Slot slot, Facet facet, Collection values) {

        super.setDirectTemplateFacetValues(cls, slot, facet, values);

        if (!facetHandlingBlocked) {
            if (cls instanceof OWLNamedClass && slot instanceof RDFProperty) {
                updateRestrictions((OWLNamedClass) cls, (RDFProperty) slot, facet);
            }
        }

        if (!superclassHandlingBlocked) {
            if (cls instanceof OWLRestriction) {
                OWLRestriction restriction = (OWLRestriction) cls;
                copyFacetValuesIntoOWLNamedClass(restriction);
            }
        }
    }


    public void setDirectTemplateSlotValues(Cls cls, Slot slot, Collection values) {

        super.setDirectTemplateSlotValues(cls, slot, values);

        if (!facetHandlingBlocked) {
            if (cls instanceof OWLNamedClass && slot instanceof RDFProperty) {
                updateRestrictions((OWLNamedClass) cls, (RDFProperty) slot, owlModel.getFacet(Model.Facet.VALUES));
            }
        }
    }


    private void updatePropertyAllowedClasses(RDFProperty property, Collection values) {
        ((Slot) property).setValueType(ValueType.INSTANCE);
        RDFSClass rangeClass = (RDFSClass) values.iterator().next();
        if (rangeClass instanceof OWLUnionClass) {
            ((Slot) property).setAllowedClses(((OWLUnionClass) rangeClass).getOperands());
        }
        else {
            ((Slot) property).setAllowedClses(Collections.singleton(rangeClass));
        }
    }


    /**
     * Updates the ValueType of a datatype slot in response to changes in the range.
     */
    private void updatePropertyValueType(RDFProperty property, Collection values) {
        ValueType newValueType = ValueType.ANY;
        if (property instanceof OWLObjectProperty && property.getSuperpropertyCount() == 0) {
            newValueType = ValueType.INSTANCE;
        }
        if (!values.isEmpty()) {
            Object range = values.iterator().next();
            if (range instanceof RDFSDatatype) {
                newValueType = XMLSchemaDatatypes.getValueType(((RDFSDatatype) range).getURI());
            }
            else if (range instanceof RDFSClass) {
                newValueType = ValueType.INSTANCE;
            }
            else if (range instanceof OWLDataRange) {
                RDFSDatatype datatype = ((OWLDataRange) range).getRDFDatatype();
                if (datatype != null) {
                    newValueType = XMLSchemaDatatypes.getValueType(datatype.getURI());
                }
            }
        }
        if (newValueType != ((Slot) property).getValueType()) {
            ((Slot) property).setValueType(newValueType);
        }
        if (newValueType == ValueType.INSTANCE) {
            ((Slot) property).setAllowedClses(Collections.EMPTY_LIST);
        }
    }


    private void updateRestrictions(OWLNamedClass cls, RDFProperty slot, Facet facet) {
        RestrictionUpdater ru = (RestrictionUpdater) facet2Updater.get(facet);
        if (ru != null) {
            superclassHandlingBlocked = true;
            ru.updateRestrictions(cls, slot, facet);
            superclassHandlingBlocked = false;
        }
    }


    /**
     * Updates the values of rdfs:subClassOf (and owl:equivalentClass) in response
     * to changes in the :SLOT-DIRECT-SUPERCLASSES.
     *
     * @param cls the RDFSClass that has changed its superclasses
     */
    private void updateRDFSSubClassOf(RDFSNamedClass cls) {

        Slot directSuperclassesSlot = owlModel.getSlot(Model.Slot.DIRECT_SUPERCLASSES);
        RDFProperty rdfsSubClassOfProperty = owlModel.getRDFSSubClassOfProperty();
        RDFProperty owlEquivalentClassProperty = owlModel.getOWLEquivalentClassProperty();

        Collection oldSuperclasses = new HashSet(cls.getPropertyValues(rdfsSubClassOfProperty));
        Collection oldEquivalentClasses = new HashSet(cls.getPropertyValues(owlEquivalentClassProperty));

        Collection newSuperclasses = new ArrayList();
        Collection newEquivalentClasses = new ArrayList();

        for (Iterator it = ((Cls) cls).getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls superClass = (Cls) it.next();
            if (superClass instanceof RDFSClass) {
                if (superClass.hasDirectSuperclass(cls)) {  // is equivalent class
                    newEquivalentClasses.add(superClass);
                    if (!oldEquivalentClasses.contains(superClass)) {
                        TripleStore ts = TripleStoreUtil.getTripleStoreOf(cls, directSuperclassesSlot, superClass);
                        TripleStoreUtil.addToTripleStore(owlModel, ts, cls, owlEquivalentClassProperty, superClass);
                    }
                    if (superClass instanceof RDFSNamedClass) {
                        newSuperclasses.add(superClass);
                        if (!oldSuperclasses.contains(superClass)) {
                            TripleStore ts = TripleStoreUtil.getTripleStoreOf(cls, directSuperclassesSlot, superClass);
                            TripleStoreUtil.addToTripleStore(owlModel, ts, cls, rdfsSubClassOfProperty, superClass);
                        }
                    }
                }
                else {
                    newSuperclasses.add(superClass);
                    if (!oldSuperclasses.contains(superClass)) {
                        TripleStore ts = TripleStoreUtil.getTripleStoreOf(cls, directSuperclassesSlot, superClass);
                        TripleStoreUtil.addToTripleStore(owlModel, ts, cls, rdfsSubClassOfProperty, superClass);
                    }
                }
            }
        }

        // Remove all old values that are no longer needed
        oldSuperclasses.removeAll(newSuperclasses);
        for (Iterator it = oldSuperclasses.iterator(); it.hasNext();) {
            RDFSClass oldSuperclass = (RDFSClass) it.next();
            cls.removePropertyValue(rdfsSubClassOfProperty, oldSuperclass);
        }
        oldEquivalentClasses.removeAll(newEquivalentClasses);
        for (Iterator it = oldEquivalentClasses.iterator(); it.hasNext();) {
            RDFSClass oldEquivalentClass = (RDFSClass) it.next();
            cls.removePropertyValue(owlEquivalentClassProperty, oldEquivalentClass);
        }
    }


    /**
     * Updates the values of :SLOT-DIRECT-DOMAIN and :SLOT-DIRECT-TEMPLATE-SLOTS
     * in response to changes in rdfs:domain.
     *
     * @param slot
     * @param values
     */
    private void updateSlotDomain(Slot slot, Collection values) {
        suppressUpdateRDFSDomain = true;
        Collection clses = new ArrayList();
        if (values.size() == 1) {
            RDFSClass cls = (RDFSClass) values.iterator().next();
            if (cls instanceof OWLUnionClass) {
                clses.addAll(((OWLUnionClass) cls).getOperands());
            }
            else {
                clses.add(cls);
            }
        }
        for (Iterator it = new ArrayList(slot.getDirectDomain()).iterator(); it.hasNext();) {
            Cls oldDomainCls = (Cls) it.next();
            if (!clses.contains(oldDomainCls)) {
                oldDomainCls.removeDirectTemplateSlot(slot);
            }
        }
        Collection oldDomain = new ArrayList(slot.getDirectDomain());
        for (Iterator it = clses.iterator(); it.hasNext();) {
            Cls rdfsClass = (Cls) it.next();
            if (!oldDomain.contains(rdfsClass)) {
                rdfsClass.addDirectTemplateSlot(slot);
            }
        }
        suppressUpdateRDFSDomain = false;
    }
}
