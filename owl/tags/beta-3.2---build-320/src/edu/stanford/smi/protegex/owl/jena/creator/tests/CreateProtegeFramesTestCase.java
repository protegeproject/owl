package edu.stanford.smi.protegex.owl.jena.creator.tests;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateProtegeFramesTestCase extends AbstractJenaCreatorTestCase {

    public void testCreateUnlinkedPALConstraint() {

        owlModel.ensureProtegeMetaOntologyImported();

        RDFSNamedClass constraintClass = owlModel.getRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
        RDFIndividual constraint = constraintClass.createRDFIndividual("MyIndividual");

        RDFProperty palNameProperty = owlModel.getRDFProperty(Model.Slot.PAL_NAME);
        constraint.setPropertyValue(palNameProperty, "MyName");

        RDFProperty palStatementProperty = owlModel.getRDFProperty(Model.Slot.PAL_STATEMENT);
        constraint.setPropertyValue(palStatementProperty, "MyStatement");

        OntModel newModel = runJenaCreator();

        OntClass constraintOntClass = newModel.getOntClass(constraintClass.getURI());
        assertNotNull(constraintOntClass);
        String palNamePropertyURI = palNameProperty.getURI();
        OntProperty palNameOntProperty = newModel.getOntProperty(palNamePropertyURI);
        assertNotNull(palNameOntProperty);
        OntProperty palStatementOntProperty = newModel.getOntProperty(palStatementProperty.getURI());
        assertNotNull(palStatementOntProperty);
        Individual constraintIndividual = newModel.getIndividual(constraint.getURI());
        assertNotNull(constraintIndividual);
        assertSize(1, constraintIndividual.listPropertyValues(palNameOntProperty));
        assertSize(1, constraintIndividual.listPropertyValues(palStatementOntProperty));
    }


    public void testCreateLinkedPALConstraint() {

        owlModel.ensureProtegeMetaOntologyImported();

        RDFSNamedClass constraintClass = owlModel.getRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
        RDFIndividual constraint = constraintClass.createRDFIndividual("MyIndividual");

        RDFSNamedClass rdfsClass = owlModel.createRDFSNamedClass("MyClass");
        RDFProperty constraintsProperty = owlModel.getRDFProperty(Model.Slot.CONSTRAINTS);
        rdfsClass.setPropertyValue(constraintsProperty, constraint);

        OntModel newModel = runJenaCreator();
        OntProperty constraintsOntProperty = newModel.getOntProperty(constraintsProperty.getURI());
        assertNotNull(constraintsOntProperty);

        Individual constraintIndividual = newModel.getIndividual(constraint.getURI());
        OntClass ontClass = newModel.getOntClass(rdfsClass.getURI());
        assertSize(1, ontClass.listPropertyValues(constraintsOntProperty));
        assertEquals(constraintIndividual, ontClass.getPropertyValue(constraintsOntProperty));
    }
}
