package edu.stanford.smi.protegex.owl.model.impl.tests;

import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class GetCommonSuperclassTestCase extends AbstractJenaTestCase {

    public void testSimple() {
        RDFSNamedClass top = owlModel.createOWLNamedClass("Top");
        RDFSNamedClass a = owlModel.createSubclass("A", top);
        RDFSNamedClass b = owlModel.createSubclass("B", top);
        Collection classes = new ArrayList();
        classes.add(a);
        classes.add(b);
        assertEquals(top, owlModel.getCommonSuperclass(classes));
    }


    public void testThing() {
        RDFSNamedClass a = owlModel.createSubclass("A", owlThing);
        RDFSNamedClass a1 = owlModel.createSubclass("Aa", a);
        RDFSNamedClass b = owlModel.createSubclass("B", owlThing);
        RDFSNamedClass b1 = owlModel.createSubclass("B1", b);
        Collection classes = new ArrayList();
        classes.add(a1);
        classes.add(b1);
        assertEquals(owlThing, owlModel.getCommonSuperclass(classes));
    }
}
