package edu.stanford.smi.protegex.owl.tests;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromReaderCreator;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromUriCreator;

/**
 * The base class of various JUnit tests in this package.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractJenaTestCase extends AbstractOWLTestCase {

    protected OntModel ontModel;

    protected Model owlFullModel;


    protected int getNamedClassesCount() {
        return list(ontModel.listNamedClasses()).size();
    }


    public void loadRemoteOntology(String localFileName) throws Exception {
        loadTestOntology(getRemoteOntologyURI(localFileName));
    }


    protected void loadRemoteOntologyWithProtegeMetadataOntology() throws Exception {
        loadRemoteOntology("import-protege.owl");
    }


    public void loadTestOntology(URI uri) throws Exception {
        Collection errors = new ArrayList();
        OwlProjectFromUriCreator creator = new OwlProjectFromUriCreator();
        creator.setOntologyUri(uri.toString());
        owlModel = (JenaOWLModel) creator.create(errors).getKnowledgeBase();
        owlModel.setExpandShortNameInMethods(true);
    }


    protected boolean ontResourceExists(Iterator it, String uri) {
        while (it.hasNext()) {
            OntResource ontResource = (OntResource) it.next();
            String otherURI = ontResource.getURI();
            if (otherURI.equals(uri)) {
                return true;
            }
        }
        return false;
    }


    public JenaOWLModel reload(JenaOWLModel owlModel) throws Exception {
        Collection errors = new ArrayList();
        OntModel ontModel = owlModel.getOntModel();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Jena.dumpRDF(ontModel, stream);
        String str = stream.toString();
        StringReader reader = new StringReader(str);
        OwlProjectFromReaderCreator creator = new OwlProjectFromReaderCreator();
        creator.setReader(reader);
        return (JenaOWLModel) creator.create(errors).getKnowledgeBase();
    }


    public JenaOWLModel reloadWithJenaLoader(JenaOWLModel owlModel) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Jena.dumpRDF(owlModel.getOntModel(), stream);
        String str = stream.toString();
        return ProtegeOWL.createJenaOWLModelFromReader(new StringReader(str));
    }
}
