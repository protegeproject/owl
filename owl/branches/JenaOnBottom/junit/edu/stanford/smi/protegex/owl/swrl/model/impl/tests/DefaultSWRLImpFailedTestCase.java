package edu.stanford.smi.protegex.owl.swrl.model.impl.tests;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultSWRLImpFailedTestCase extends AbstractSWRLTestCase {

    public void testCreateClone() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Cls");
        SWRLVariable x = factory.createVariable("x");
        SWRLClassAtom headAtom = factory.createClassAtom(cls, x);
        SWRLClassAtom bodyAtom = factory.createClassAtom(cls, x);
        SWRLImp imp = factory.createImp(headAtom, Collections.singleton(bodyAtom));
        SWRLImp c = imp.createClone();
        assertFalse(imp.equals(c));
        assertEquals(c.getBrowserText(), imp.getBrowserText());
    }
}
