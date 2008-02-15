package edu.stanford.smi.protegex.owl.jena.parser.tests;

import edu.stanford.smi.protegex.owl.jena.parser.DefaultURI2NameConverter;
import edu.stanford.smi.protegex.owl.jena.parser.URI2NameConverter;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultURI2NameConverterTestCase extends AbstractJenaTestCase {

    public void testNameStartsWithDigits() {
        final String namespace = "http://aldi.de/ontology.owl#";
        String validURI = namespace + "Number";
        String invalidURI = namespace + "23 Number";
        owlModel.getNamespaceManager().setDefaultNamespace(namespace);
        URI2NameConverter converter = new DefaultURI2NameConverter(owlModel, null, false);
        assertEquals("Number", converter.getRDFResourceName(validURI));
        assertEquals("_23_Number", converter.getRDFResourceName(invalidURI));
    }
}
