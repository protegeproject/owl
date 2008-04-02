package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protegex.owl.model.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateRDFPropertyTestCase extends AbstractTripleStoreTestCase {

    public void testCreateRDFProperty() {
        RDFResource c = createRDFResource("test");
        ts.add(c, rdfTypeProperty, owlModel.getRDFPropertyClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        assertTrue(owlModel.getRDFProperty(c.getName()) instanceof RDFProperty);
        assertFalse(owlModel.getRDFProperty(c.getName()) instanceof OWLProperty);
    }


    public void testCreateFunctionalRDFProperty() {
        RDFResource c = createRDFResource("property");
        ts.add(c, rdfTypeProperty, owlModel.getOWLFunctionalPropertyClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        RDFProperty property = owlModel.getRDFProperty(c.getName());
        assertTrue(property.isFunctional());
    }


    public void testCreateSymmetricProperty() {
        RDFResource c = createRDFResource("test");
        ts.add(c, rdfTypeProperty, owlModel.getRDFSNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY));
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        RDFProperty property = owlModel.getRDFProperty(c.getName());
        assertTrue(property instanceof OWLObjectProperty);
        assertTrue(((OWLObjectProperty) property).isSymmetric());
    }


    public void testCreateTransitiveProperty() {
        RDFResource c = createRDFResource("test");
        ts.add(c, rdfTypeProperty, owlModel.getRDFSNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY));
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        RDFProperty property = owlModel.getRDFProperty(c.getName());
        assertTrue(property instanceof OWLObjectProperty);
        assertTrue(((OWLObjectProperty) property).isTransitive());
    }


    public void testCreateSubProperty() {
        RDFProperty superproperty = owlModel.createRDFProperty("super");
        RDFProperty subproperty = owlModel.createRDFProperty("sub");
        ts.add(subproperty, owlModel.getRDFProperty(RDFSNames.Slot.SUB_PROPERTY_OF), superproperty);
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        assertSize(1, subproperty.getSuperproperties(false));
        assertContains(superproperty, subproperty.getSuperproperties(false));
        assertSize(1, superproperty.getSubproperties(false));
        assertContains(subproperty, superproperty.getSubproperties(false));
    }
}
