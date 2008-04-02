package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateRDFSSubClassOfTestCase extends AbstractTripleStoreTestCase {

    public void testDefaultSuperclass() {
        String name = "Class";
        RDFResource c = createRDFResource(name);
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Cls cls = owlModel.getOWLNamedClass(name);
        assertSize(1, cls.getDirectSuperclasses());
        assertContains(owlThing, cls.getDirectSuperclasses());
    }


    public void testDefaultSuperclassDespiteRestriction() {
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        String name = "Class";
        RDFResource c = createRDFResource(name);
        RDFResource restriction = createRDFResource(null);
        ts.add(c, owlModel.getRDFSSubClassOfProperty(), restriction);
        ts.add(restriction, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        ts.add(restriction, owlModel.getRDFProperty(OWLNames.Slot.CARDINALITY), new Integer(1));
        ts.add(restriction, owlModel.getRDFProperty(OWLNames.Slot.ON_PROPERTY), property);
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Cls cls = owlModel.getOWLNamedClass(name);
        assertSize(2, cls.getDirectSuperclasses());
        assertContains(owlThing, cls.getDirectSuperclasses());
        assertContains(restriction, cls.getDirectSuperclasses());
    }


    public void testSimpleSuperclass() {
        RDFResource subclass = createRDFResource("subclass");
        RDFResource superclass = createRDFResource("superclass");
        ts.add(subclass, owlModel.getRDFSSubClassOfProperty(), superclass);
        ts.add(subclass, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        ts.add(superclass, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Cls subcls = owlModel.getOWLNamedClass(subclass.getName());
        Cls supercls = owlModel.getOWLNamedClass(superclass.getName());
        assertSize(1, supercls.getDirectSuperclasses());
        assertContains(owlThing, supercls.getDirectSuperclasses());
        assertSize(1, subcls.getDirectSuperclasses());
        assertContains(supercls, subcls.getDirectSuperclasses());
        assertSize(1, supercls.getDirectSubclasses());
        assertContains(subcls, supercls.getDirectSubclasses());
    }
}
