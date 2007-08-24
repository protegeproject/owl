package edu.stanford.smi.protegex.owl.model.impl.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLModelTestCase extends AbstractJenaTestCase {

    public void testThing() {
        assertEquals(owlModel.getOWLNamedClassClass(), owlThing.getProtegeType());
    }


    public void testAllRestriction() {
        Slot allValuesFromSlot = owlModel.getSlot(OWLNames.Slot.ALL_VALUES_FROM);
        assertSize(1, allValuesFromSlot.getDirectDomain());
        Cls allRestrictionCls = owlModel.getCls(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION);
        assertSize(1, allRestrictionCls.getDirectTemplateSlots());
    }


    public void testRDFResources() {
        Collection resources = owlModel.getRDFResources();
        for (Iterator it = resources.iterator(); it.hasNext();) {
            Frame o = (Frame) it.next();
            assertTrue(o instanceof RDFResource);
        }
        assertEquals(owlModel.getRDFResourceCount(), resources.size());
    }


    public void testRDFSClasses() {
        Collection classes = owlModel.getRDFSClasses();
        assertFalse(classes.contains(owlModel.getCls(Model.Cls.PAL_CONSTRAINT)));
        assertFalse(classes.contains(owlModel.getCls(Model.Cls.DIRECTED_BINARY_RELATION)));
        assertFalse(classes.contains(owlModel.getCls(OWLNames.Cls.OWL_CLASS)));
        assertFalse(classes.contains(owlModel.getCls(OWLNames.Cls.ANONYMOUS_ROOT)));
        assertEquals(owlModel.getRDFSClassCount(), classes.size());
        for (Iterator it = classes.iterator(); it.hasNext();) {
            assertTrue(it.next() instanceof RDFSClass);
        }
    }
}
