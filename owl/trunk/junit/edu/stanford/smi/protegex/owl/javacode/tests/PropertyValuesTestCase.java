package edu.stanford.smi.protegex.owl.javacode.tests;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.code.generation.test03.Arm;
import edu.stanford.smi.protegex.owl.code.generation.test03.Factory;
import edu.stanford.smi.protegex.owl.code.generation.test03.StudySchema;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import junit.framework.TestCase;

public class PropertyValuesTestCase extends TestCase {
    private Project p;
    private OWLModel owlModel;
    private Factory factory;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Collection errors = new ArrayList();
        p = new Project(GenerateJunitCode.SOURCE_ONTOLOGY_03, errors);
        GenerateJunitCode.handleErrors(errors);
        owlModel = (OWLModel)  p.getKnowledgeBase();
        factory = new Factory(owlModel);
    }
    
    @Override
    protected void tearDown() throws Exception {
        Collection errors = new ArrayList();
        for (Object o : owlModel.getUserDefinedRDFIndividuals(true)) {
            if (o instanceof RDFIndividual) {
                ((RDFIndividual) o).delete();
            }
        }
        p.save(errors);
        GenerateJunitCode.handleErrors(errors);
        p.dispose();
        p = null;
        owlModel = null;
    }
    
    public void testSetPropertiesAndSave() throws OntologyLoadException {
        String schemaName = "MyStudySchema";
        StudySchema schema = factory.createStudySchema(schemaName);
        Arm a1 = factory.createArm("Arm1");
        Arm a2 = factory.createArm("Arm2");
        Arm a3 = factory.createArm("Arm3");
        schema.addHasArms(a1);
        schema.addHasArms(a2);
        schema.addHasArms(a3);
        assertEquals(3, schema.getHasArms().size());
        for (Object o : schema.getHasArms()) {
            assertTrue(o instanceof Arm);
        }
        
        Collection errors = new ArrayList();
        p.save(errors);
        p = new Project(GenerateJunitCode.SOURCE_ONTOLOGY_03, errors);
        GenerateJunitCode.handleErrors(errors);
        owlModel = (OWLModel) p.getKnowledgeBase();
        
        schema = factory.getStudySchema(schemaName);
        assertEquals(3, schema.getHasArms().size());
        for (Object o : schema.getHasArms()) {
            assertTrue(o instanceof Arm);
        }
        
    }
}
