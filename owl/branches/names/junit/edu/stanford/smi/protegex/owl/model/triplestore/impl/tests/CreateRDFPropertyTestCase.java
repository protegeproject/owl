package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNames;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateRDFPropertyTestCase extends AbstractTripleStoreTestCase {

    public void testCreateRDFProperty() {
        RDFResource c = createRDFResource("test");
        ts.add(c, rdfTypeProperty, owlModel.getRDFPropertyClass());
        assertTrue(owlModel.getRDFProperty(c.getName()) instanceof RDFProperty);
        assertFalse(owlModel.getRDFProperty(c.getName()) instanceof OWLProperty);
    }


    public void testCreateFunctionalRDFProperty() {
        RDFResource c = createRDFResource("property");
        ts.add(c, rdfTypeProperty, owlModel.getOWLFunctionalPropertyClass());
        RDFProperty property = owlModel.getRDFProperty(c.getName());
        assertTrue(property.isFunctional());
    }


    public void testCreateSymmetricProperty() {
        RDFResource c = createRDFResource("test");
        ts.add(c, rdfTypeProperty, owlModel.getRDFSNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY));
        RDFProperty property = owlModel.getRDFProperty(c.getName());
        assertTrue(property instanceof OWLObjectProperty);
        assertTrue(((OWLObjectProperty) property).isSymmetric());
    }


    public void testCreateTransitiveProperty() {
        RDFResource c = createRDFResource("test");
        ts.add(c, rdfTypeProperty, owlModel.getRDFSNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY));
        RDFProperty property = owlModel.getRDFProperty(c.getName());
        assertTrue(property instanceof OWLObjectProperty);
        assertTrue(((OWLObjectProperty) property).isTransitive());
    }


    public void testCreateSubProperty() {
        RDFProperty superproperty = owlModel.createRDFProperty("super");
        RDFProperty subproperty = owlModel.createRDFProperty("sub");
        ts.add(subproperty, owlModel.getRDFProperty(RDFSNames.Slot.SUB_PROPERTY_OF), superproperty);
        assertSize(1, subproperty.getSuperproperties(false));
        assertContains(superproperty, subproperty.getSuperproperties(false));
        assertSize(1, superproperty.getSubproperties(false));
        assertContains(subproperty, superproperty.getSubproperties(false));
    }
}
