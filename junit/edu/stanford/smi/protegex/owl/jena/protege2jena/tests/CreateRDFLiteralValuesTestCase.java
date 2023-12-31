package edu.stanford.smi.protegex.owl.jena.protege2jena.tests;

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
public class CreateRDFLiteralValuesTestCase extends AbstractProtege2JenaTestCase {




    public void testValuesOfImportedProperties() throws Exception {
        loadRemoteOntology("importSWRL.owl");
        OWLNamedClass datavaluedPropertyAtomCls = (OWLNamedClass) owlModel.getOWLNamedClass("swrl:DatavaluedPropertyAtom");
        assertNotNull(datavaluedPropertyAtomCls);
        RDFProperty argument2RDFProperty = (RDFProperty) owlModel.getRDFProperty("swrl:argument2");
        assertNotNull(argument2RDFProperty);
        RDFResource atom = (RDFResource) datavaluedPropertyAtomCls.createInstance("atom");
        atom.addPropertyValue(argument2RDFProperty, "aldi");
        OntModel newModel = createOntModel();
        Resource atomResource = newModel.getResource(atom.getURI());
        assertNotNull(atomResource);
        Property argument2OntProperty = newModel.getProperty(argument2RDFProperty.getURI());
        assertNotNull(argument2OntProperty);
        assertSize(1, atomResource.listProperties(argument2OntProperty));
    }
}
