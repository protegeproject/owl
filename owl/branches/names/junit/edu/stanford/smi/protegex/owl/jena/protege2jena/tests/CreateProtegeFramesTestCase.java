package edu.stanford.smi.protegex.owl.jena.protege2jena.tests;

import java.util.logging.Level;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.parser.FrameCreatorUtility;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.jena.parser.TripleProcessor;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateProtegeFramesTestCase extends AbstractProtege2JenaTestCase {
    
    public static void enableDebug() {
        Log.setLoggingLevel(AbstractJenaTestCase.class, Level.FINE);
        Log.setLoggingLevel(ProtegeOWLParser.class, Level.FINEST);
        Log.setLoggingLevel(TripleProcessor.class, Level.FINEST);
        Log.setLoggingLevel(FrameCreatorUtility.class, Level.FINEST);
    }

    public void testCreateUnlinkedPALConstraint() throws Exception {

        
        ensureProtegeMetaOntologyImported();
        owlModel.getFrameStoreManager().setProtegeOwlFrameStoreEnabled(true);

        RDFSNamedClass constraintClass = owlModel.getRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
        RDFIndividual constraint = constraintClass.createRDFIndividual("MyIndividual");

        RDFProperty palNameProperty = owlModel.getRDFProperty(Model.Slot.PAL_NAME);
        constraint.setPropertyValue(palNameProperty, "MyName");

        RDFProperty palStatementProperty = owlModel.getRDFProperty(Model.Slot.PAL_STATEMENT);
        constraint.setPropertyValue(palStatementProperty, "MyStatement");

        owlModel = reloadWithJenaLoader(owlModel);
        OntModel newModel = createOntModel();

        OntClass constraintOntClass = newModel.getOntClass(constraintClass.getURI());
        assertNotNull(constraintOntClass);
        OntProperty palNameOntProperty = newModel.getOntProperty(palNameProperty.getURI());
        assertNotNull(palNameOntProperty);
        OntProperty palStatementOntProperty = newModel.getOntProperty(palStatementProperty.getURI());
        assertNotNull(palStatementOntProperty);
        Individual constraintIndividual = newModel.getIndividual(constraint.getURI());
        assertNotNull(constraintIndividual);
        assertSize(1, constraintIndividual.listPropertyValues(palNameOntProperty));
        assertSize(1, constraintIndividual.listPropertyValues(palStatementOntProperty));
    }


    public void testCreateLinkedPALConstraint() throws Exception {
        Log.setLoggingLevel(AbstractJenaTestCase.class, Level.FINE);

        ensureProtegeMetaOntologyImported();
        owlModel.getFrameStoreManager().setProtegeOwlFrameStoreEnabled(true);
        owlModel = reloadWithJenaLoader(owlModel);

        RDFSNamedClass constraintClass = owlModel.getRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
        RDFIndividual constraint = constraintClass.createRDFIndividual("MyIndividual");

        RDFSNamedClass rdfsClass = owlModel.createRDFSNamedClass("MyClass");
        RDFProperty constraintsProperty = owlModel.getRDFProperty(Model.Slot.CONSTRAINTS);
        rdfsClass.setPropertyValue(constraintsProperty, constraint);

        OntModel newModel = createOntModel();
        OntProperty constraintsOntProperty = newModel.getOntProperty(constraintsProperty.getURI());
        assertNotNull(constraintsOntProperty);

        Individual constraintIndividual = newModel.getIndividual(constraint.getURI());
        OntClass ontClass = newModel.getOntClass(rdfsClass.getURI());
        assertSize(1, ontClass.listPropertyValues(constraintsOntProperty));
        assertEquals(constraintIndividual, ontClass.getPropertyValue(constraintsOntProperty));
    }
}
