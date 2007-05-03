package edu.stanford.smi.protegex.owl.model.impl.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeFramesTestCase extends AbstractJenaTestCase {

    public void testRelationFrames() {
        Cls cls = owlModel.getCls(Model.Cls.DIRECTED_BINARY_RELATION);
        assertTrue(cls instanceof RDFSNamedClass);
        Slot fromSlot = owlModel.getSlot(Model.Slot.FROM);
        assertTrue(fromSlot instanceof RDFProperty);
        Slot toSlot = owlModel.getSlot(Model.Slot.TO);
        assertTrue(toSlot instanceof RDFProperty);
    }


    public void testPALFrames() {
        assertTrue(owlModel.getCls(Model.Cls.PAL_CONSTRAINT) instanceof RDFSNamedClass);
        assertTrue(owlModel.getSlot(Model.Slot.PAL_DESCRIPTION) instanceof RDFProperty);
        assertTrue(owlModel.getSlot(Model.Slot.PAL_NAME) instanceof RDFProperty);
        assertTrue(owlModel.getSlot(Model.Slot.PAL_RANGE) instanceof RDFProperty);
        assertTrue(owlModel.getSlot(Model.Slot.PAL_STATEMENT) instanceof RDFProperty);
        assertTrue(owlModel.getSlot(Model.Slot.CONSTRAINTS) instanceof RDFProperty);

        RDFSNamedClass constraintClass = owlModel.getRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
        Collection properties = constraintClass.getUnionDomainProperties();
        assertSize(4, properties);
    }


    public void testProtegeURIs() {
        final String name = Model.Slot.TO;
        RDFProperty property = owlModel.getRDFProperty(name);
        assertEquals(ProtegeNames.NS + name.substring(1), property.getURI());
        assertEquals(name, owlModel.getResourceNameForURI(property.getURI()));
    }
}
