package edu.stanford.smi.protegex.owl.model.framestore.tests;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeleteTestCase extends AbstractJenaTestCase {

    private OWLObjectProperty childrenSlot;

    private OWLNamedClass genderCls;

    private OWLObjectProperty genderSlot;

    private Instance male;

    private OWLNamedClass personCls;


    /**
     * Installs the structure:
     * OWLIntersectionClass
     * Person
     * Complement
     * OWLAllValuesFrom
     * children
     * OWLHasValue
     * gender
     * male
     */
    private void setup() {
        personCls = owlModel.createOWLNamedClass("Person");
        childrenSlot = owlModel.createOWLObjectProperty("children");
        genderCls = owlModel.createOWLNamedClass("Gender");
        male = genderCls.createInstance("male");
        genderSlot = owlModel.createOWLObjectProperty("gender");
        genderSlot.setRange(genderCls);
        OWLHasValue hasRestriction = owlModel.createOWLHasValue(genderSlot, male);
        OWLAllValuesFrom allRestriction = owlModel.createOWLAllValuesFrom(childrenSlot, hasRestriction);
        OWLComplementClass complementCls = owlModel.createOWLComplementClass(allRestriction);
        OWLIntersectionClass intersectionCls = owlModel.createOWLIntersectionClass();
        intersectionCls.addOperand(personCls);
        intersectionCls.addOperand(complementCls);
    }


    public void testDeletePerson() {
        int count = owlModel.getClsCount();
        setup();
        personCls.delete();  // Should delete everything
        assertEquals(count + 1, owlModel.getClsCount());
    }
}
