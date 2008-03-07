package edu.stanford.smi.protegex.owl.ui.conditions.tests;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.conditions.AddCoveringAxiomAction;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;

import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddCoveringAxiomActionTestCase extends AbstractJenaTestCase {

    public void testAddCoveringAxiomToPrimitiveClass() {
        try {
            OWLNamedClass colorClass = owlModel.createOWLNamedClass("Color");
            OWLNamedClass redClass = owlModel.createOWLNamedSubclass("Red", colorClass);
            OWLNamedClass greenClass = owlModel.createOWLNamedSubclass("Green", colorClass);
            OWLNamedClass blueClass = owlModel.createOWLNamedSubclass("Blue", colorClass);
            AddCoveringAxiomAction action = new AddCoveringAxiomAction();
            ResourceDisplay component = new ResourceDisplay(owlModel.getProject());
            assertFalse(action.isSuitable(component, redClass));
            assertTrue(action.isSuitable(component, colorClass));
            action.initialize(component, colorClass);
            action.actionPerformed(null);
            assertSize(1, colorClass.getEquivalentClasses());
            OWLUnionClass unionCls = (OWLUnionClass) colorClass.getDefinition();
            assertSize(3, unionCls.getOperands());
            assertContains(redClass, unionCls.getOperands());
            assertContains(greenClass, unionCls.getOperands());
            assertContains(blueClass, unionCls.getOperands());
            assertFalse(action.isSuitable(component, colorClass));
        }
        catch (HeadlessException ex) {
            // Ignore
        }
    }
}
