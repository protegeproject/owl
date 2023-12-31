package edu.stanford.smi.protegex.owl.jena.creator.tests;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNames;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateRDFLiteralValuesTestCase extends AbstractJenaCreatorTestCase {
    private static final transient Logger log = Log.getLogger(CreateRDFLiteralValuesTestCase.class);


    public void testValuesOfImportedProperties() throws Exception {
        log.setLevel(Level.FINE);
        
        // ProtegeOWLPluginFolderRepository.setDefaultOlwRepositoryFolder(new File("etc"));
        loadRemoteOntology("importSWRL.owl");
        String defaultOntology = owlModel.getNamespaceManager().getDefaultNamespace();
        
        OWLNamedClass datavaluedPropertyAtomCls = owlModel.getSystemFrames().getDataValuedPropertyAtomCls();
        assertNotNull(datavaluedPropertyAtomCls);
        RDFProperty argument2RDFProperty = owlModel.getSystemFrames().getArgument2Property();
        assertNotNull(argument2RDFProperty);
        RDFResource atom = datavaluedPropertyAtomCls.createInstance(defaultOntology + "atom");
        atom.addPropertyValue(argument2RDFProperty, "aldi");
        
        OntModel newModel = runJenaCreator();
        Jena.dumpRDF(newModel, log, Level.FINE);
        
        Resource atomResource = newModel.getResource(atom.getURI());
        assertNotNull(atomResource);
        Property argument2OntProperty = newModel.getProperty(argument2RDFProperty.getURI());
        assertNotNull(argument2OntProperty);
        assertSize(1, atomResource.listProperties(argument2OntProperty));
    }
    
    private String getFullName(String shortName) {
        return NamespaceUtil.getFullName(owlModel, shortName);
    }
}
