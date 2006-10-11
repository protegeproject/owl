package edu.stanford.smi.protegex.owl.jena.importer.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImportPALConstraintsTestCase extends AbstractOWLImporterTestCase {

    public void testImportPALConstraint() {
        OWLNamedClass oldCls = owlModel.createOWLNamedClass("Cls");
        Cls oldConstraintCls = owlModel.getCls(Model.Cls.PAL_CONSTRAINT);
        Instance oldConstraint = oldConstraintCls.createDirectInstance("PAL");
        oldConstraint.setDirectOwnSlotValue(owlModel.getSlot(Model.Slot.PAL_NAME), "name");
        oldConstraint.setDirectOwnSlotValue(owlModel.getSlot(Model.Slot.PAL_RANGE), "range");
        ((Cls) oldCls).addOwnSlotValue(owlModel.getSlot(Model.Slot.CONSTRAINTS), oldConstraint);
        KnowledgeBase kb = runOWLImporter();
        Cls newCls = kb.getCls(oldCls.getName());
        Collection newConstraints = newCls.getDirectOwnSlotValues(kb.getSlot(Model.Slot.CONSTRAINTS));
        assertSize(1, newConstraints);
        Instance newConstraint = (Instance) newConstraints.iterator().next();
        assertEquals(Model.Cls.PAL_CONSTRAINT, newConstraint.getDirectType().getName());
        assertEquals("name", newConstraint.getDirectOwnSlotValue(kb.getSlot(Model.Slot.PAL_NAME)));
        assertEquals("range", newConstraint.getDirectOwnSlotValue(kb.getSlot(Model.Slot.PAL_RANGE)));
    }
}
