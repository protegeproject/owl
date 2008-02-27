package edu.stanford.smi.protegex.owl.jena.creator.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.framestore.ProtegeOWLFrameStore;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateProtegeFramesTestCase extends AbstractJenaCreatorTestCase {
    private static transient final Logger log = Log.getLogger(CreateProtegeFramesTestCase.class);

    public void testCreateUnlinkedPALConstraint() {

        ensureProtegeMetaOntologyImported();
        owlModel.getFrameStoreManager().setProtegeOwlFrameStoreEnabled(true);

        RDFSNamedClass constraintClass = owlModel.getRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
        RDFIndividual constraint = constraintClass.createRDFIndividual("MyIndividual");

        RDFProperty palNameProperty = owlModel.getRDFProperty(Model.Slot.PAL_NAME);
        constraint.setPropertyValue(palNameProperty, "MyName");

        RDFProperty palStatementProperty = owlModel.getRDFProperty(Model.Slot.PAL_STATEMENT);
        constraint.setPropertyValue(palStatementProperty, "MyStatement");

        OntModel newModel = runJenaCreator();
        Jena.dumpRDF(newModel, log, Level.FINE);

        OntClass constraintOntClass = newModel.getOntClass(ProtegeOWLFrameStore.convertProtegeFrameNameToOwl(Model.Cls.PAL_CONSTRAINT));
        assertNotNull(constraintOntClass);
        OntProperty palNameOntProperty = newModel.getOntProperty(ProtegeOWLFrameStore.convertProtegeFrameNameToOwl(Model.Slot.PAL_NAME));
        assertNotNull(palNameOntProperty);
        OntProperty palStatementOntProperty = newModel.getOntProperty(ProtegeOWLFrameStore.convertProtegeFrameNameToOwl(Model.Slot.PAL_STATEMENT));
        assertNotNull(palStatementOntProperty);
        Individual constraintIndividual = newModel.getIndividual(constraint.getURI());
        assertNotNull(constraintIndividual);
        assertSize(1, constraintIndividual.listPropertyValues(palNameOntProperty));
        assertSize(1, constraintIndividual.listPropertyValues(palStatementOntProperty));
    }


    public void testCreateLinkedPALConstraint() {

        ensureProtegeMetaOntologyImported();
        owlModel.getFrameStoreManager().setProtegeOwlFrameStoreEnabled(true);

        RDFSNamedClass constraintClass = owlModel.getRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
        RDFIndividual constraint = constraintClass.createRDFIndividual("MyIndividual");

        RDFSNamedClass rdfsClass = owlModel.createRDFSNamedClass("MyClass");
        RDFProperty constraintsProperty = owlModel.getRDFProperty(Model.Slot.CONSTRAINTS);
        rdfsClass.setPropertyValue(constraintsProperty, constraint);

        OntModel newModel = runJenaCreator();
        Jena.dumpRDF(newModel, log, Level.FINE);
        OntProperty constraintsOntProperty = newModel.getOntProperty(ProtegeOWLFrameStore.convertProtegeFrameNameToOwl(Model.Slot.CONSTRAINTS));
        assertNotNull(constraintsOntProperty);

        Individual constraintIndividual = newModel.getIndividual(constraint.getURI());
        OntClass ontClass = newModel.getOntClass(rdfsClass.getURI());
        assertSize(1, ontClass.listPropertyValues(constraintsOntProperty));
        assertEquals(constraintIndividual, ontClass.getPropertyValue(constraintsOntProperty));
    }
}
