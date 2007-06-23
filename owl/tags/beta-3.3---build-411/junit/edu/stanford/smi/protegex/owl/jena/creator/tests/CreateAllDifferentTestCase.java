package edu.stanford.smi.protegex.owl.jena.creator.tests;

import com.hp.hpl.jena.ontology.AllDifferent;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateAllDifferentTestCase extends AbstractJenaCreatorTestCase {

    public void testCreateAllDifferent() {
        OWLNamedClass colorClass = owlModel.createOWLNamedClass("Color");
        OWLIndividual red = colorClass.createOWLIndividual("red");
        OWLIndividual blue = colorClass.createOWLIndividual("blue");
        OWLAllDifferent owlAllDifferent = owlModel.createOWLAllDifferent();
        owlAllDifferent.addDistinctMember(red);
        owlAllDifferent.addDistinctMember(blue);

        OntModel ontModel = runJenaCreator();

        Individual redIndividual = ontModel.getIndividual(red.getURI());
        Individual blueIndividual = ontModel.getIndividual(blue.getURI());
        assertSize(1, ontModel.listAllDifferent());
        AllDifferent allDifferent = ((AllDifferent) ((Resource) ontModel.listAllDifferent().next()).as(AllDifferent.class));
        assertSize(2, allDifferent.listDistinctMembers());
        assertContains(redIndividual, allDifferent.listDistinctMembers());
        assertContains(blueIndividual, allDifferent.listDistinctMembers());
    }
}
