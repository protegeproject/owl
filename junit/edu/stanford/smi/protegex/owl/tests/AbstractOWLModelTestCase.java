package edu.stanford.smi.protegex.owl.tests;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The base class of various JUnit tests on OWL ontologies.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLModelTestCase extends TestCase {

    protected OWLModel owlModel;

    protected Project project;


    protected void setUp() throws Exception {
        super.setUp();
        Collection errors = new ArrayList();
        project = new Project(null, errors);
        final JenaKnowledgeBaseFactory factory = new JenaKnowledgeBaseFactory();
        project.setKnowledgeBaseFactory(factory);
        project.createDomainKnowledgeBase(factory, errors, false);
        owlModel = (OWLModel) project.getKnowledgeBase();
    }
}
