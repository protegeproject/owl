package edu.stanford.smi.protegex.owl.model.impl.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

public class UnionDomainTestCase extends TestCase {
    
    @SuppressWarnings("unchecked")
    public void testUnionDomain() {
        List errors = new ArrayList();
        Project p = new Project("junit/projects/domain.pprj", errors);
        assertTrue(errors.isEmpty());
        OWLModel owlModel = (OWLModel) p.getKnowledgeBase();
        
        OWLNamedClass a = owlModel.getOWLNamedClass("A");
        OWLNamedClass b = owlModel.getOWLNamedClass("B");
    }

}
