package edu.stanford.smi.protegex.owl.jena.creator.tests;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;

import java.net.URI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateRDFLiteralValuesTestCase extends AbstractJenaCreatorTestCase {

    public void testRDFValueInRDFLiteral() throws Exception {
        loadTestOntology(new URI("http://www.daml.org/2004/04/swrl/swrl.owl"));
        OWLNamedClass datavaluedPropertyAtomCls = (OWLNamedClass) owlModel.getCls("swrl:DatavaluedPropertyAtom");
        assertNotNull(datavaluedPropertyAtomCls);
        RDFProperty argument2Property = (RDFProperty) owlModel.getSlot("swrl:argument2");
        assertNotNull(argument2Property);
        RDFResource atom = (RDFResource) datavaluedPropertyAtomCls.createInstance("atom");
        Cls literalCls = owlModel.getCls(RDFSNames.Cls.LITERAL);
        Instance literal = literalCls.createDirectInstance("myLiteral");
        Slot valueSlot = owlModel.getSlot(RDFNames.Slot.VALUE);
        final String value = "aldi";
        literal.setDirectOwnSlotValue(valueSlot, value);
        atom.addPropertyValue(argument2Property, literal);
        // Should do some testing here
    }


    public void testValuesOfImportedProperties() throws Exception {
        loadRemoteOntology("importSWRL.owl");
        OWLNamedClass datavaluedPropertyAtomCls = (OWLNamedClass) owlModel.getCls("swrl:DatavaluedPropertyAtom");
        assertNotNull(datavaluedPropertyAtomCls);
        RDFProperty argument2RDFProperty = (RDFProperty) owlModel.getSlot("swrl:argument2");
        assertNotNull(argument2RDFProperty);
        RDFResource atom = (RDFResource) datavaluedPropertyAtomCls.createInstance("atom");
        atom.addPropertyValue(argument2RDFProperty, "aldi");
        OntModel newModel = runJenaCreator();
        Resource atomResource = newModel.getResource(atom.getURI());
        assertNotNull(atomResource);
        Property argument2OntProperty = newModel.getProperty(argument2RDFProperty.getURI());
        assertNotNull(argument2OntProperty);
        assertSize(1, atomResource.listProperties(argument2OntProperty));
    }
}
