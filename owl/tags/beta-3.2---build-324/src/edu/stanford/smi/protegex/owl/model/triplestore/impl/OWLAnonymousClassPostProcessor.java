package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class OWLAnonymousClassPostProcessor {

    private KnowledgeBase kb;

    private OWLModel owlModel;

    private Collection properties = new ArrayList();


    OWLAnonymousClassPostProcessor(OWLModel owlModel) {

        this.owlModel = owlModel;
        this.kb = owlModel;

        properties.add(owlModel.getRDFProperty(OWLNames.Slot.ON_PROPERTY));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.ALL_VALUES_FROM));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.SOME_VALUES_FROM));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.HAS_VALUE));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.MIN_CARDINALITY));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.MAX_CARDINALITY));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.CARDINALITY));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.COMPLEMENT_OF));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.INTERSECTION_OF));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.UNION_OF));
        properties.add(owlModel.getRDFProperty(OWLNames.Slot.ONE_OF));

        convertNamedClasses(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION);
        convertNamedClasses(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
        convertNamedClasses(OWLNames.Cls.HAS_VALUE_RESTRICTION);
        convertNamedClasses(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION);
        convertNamedClasses(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION);
        convertNamedClasses(OWLNames.Cls.CARDINALITY_RESTRICTION);

        convertNamedClasses(OWLNames.Cls.COMPLEMENT_CLASS);
        convertNamedClasses(OWLNames.Cls.UNION_CLASS);
        convertNamedClasses(OWLNames.Cls.INTERSECTION_CLASS);

        convertNamedEnumeratedClasses();
    }


    private void convertNamedEnumeratedClasses() {
        RDFSClass metaClass = owlModel.getRDFSNamedClass(OWLNames.Cls.ENUMERATED_CLASS);
        RDFProperty owlOneOfProperty = owlModel.getOWLOneOfProperty();
        Collection namedClasses = owlModel.getOWLNamedClassClass().getInstances(false);
        for (Iterator it = namedClasses.iterator(); it.hasNext();) {
            OWLNamedClass namedClass = (OWLNamedClass) it.next();
            if (namedClass.getPropertyValueCount(owlOneOfProperty) > 0) {
                String name = namedClass.getName();
                if (owlModel.isAnonymousResourceName(name)) {
                    TripleStoreUtil.ensureActiveTripleStore((RDFResource) namedClass);
                    namedClass.setProtegeType(metaClass);
                    namedClass.setPropertyValue(owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
                }
                else {
                    convertNamedClass(namedClass, metaClass);
                }
            }
        }
    }


    private void convertNamedClasses(String metaclassName) {
        final Cls metaclass = kb.getCls(metaclassName);
        final Collection instances = new ArrayList(metaclass.getDirectInstances());
        final TripleStoreModel tsm = owlModel.getTripleStoreModel();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            final RDFSClass cls = (RDFSClass) it.next();
            final String name = cls.getName();
            if (owlModel.isAnonymousResourceName(name)) {
                TripleStore ts = tsm.getHomeTripleStore(cls);
                if (!ts.listTriplesWithObject(cls).hasNext()) { // Is top-level axiom
                    tsm.setActiveTripleStore(ts); // for correct newName
                    String newName = owlModel.createNewResourceName("Axiom");
                    ts.setRDFResourceName(cls, newName);
                    convertNamedClass(cls, metaclass);
                }
            }
            else {
                convertNamedClass(cls, metaclass);
            }
        }
    }


    private void convertNamedClass(Cls namedClass, final Cls metaclass) {
        final String anonName = owlModel.getNextAnonymousResourceName();
        TripleStore ts = TripleStoreUtil.ensureActiveTripleStore((RDFResource) namedClass);
        final Cls anonClass = (Cls) metaclass.createDirectInstance(anonName);
        for (Iterator pit = properties.iterator(); pit.hasNext();) {
            RDFProperty property = (RDFProperty) pit.next();
            Collection values = namedClass.getDirectOwnSlotValues(property);
            if (!values.isEmpty()) {
                ts.getNarrowFrameStore().setValues(anonClass, property, null, false, values);
                // anonClass.setOwnSlotValues(property, values);
                ts.getNarrowFrameStore().setValues(namedClass, property, null, false, Collections.EMPTY_LIST);
                // namedClass.setOwnSlotValues(property, Collections.EMPTY_LIST);
            }
        }
        final String name = namedClass.getName();
        namedClass.setDirectType(owlModel.getOWLNamedClassClass());
        namedClass = owlModel.getOWLNamedClass(name);
        namedClass.addDirectSuperclass(anonClass);
        anonClass.addDirectSuperclass(namedClass);
        //if (!namedClass.getDirectOwnSlotValues(owlModel.getOWLEquivalentClassProperty()).contains(anonClass)) {
        //    namedClass.addOwnSlotValue(owlModel.getOWLEquivalentClassProperty(), anonClass);
        //}
    }
}
